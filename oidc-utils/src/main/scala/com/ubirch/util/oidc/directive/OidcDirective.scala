package com.ubirch.util.oidc.directive

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import akka.stream.Materializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.crypto.ecc.EccUtil
import com.ubirch.key.model.rest.PublicKey
import com.ubirch.keyservice.client.rest.cache.redis.KeyServiceClientRestCacheRedis
import com.ubirch.user.client.rest.UserServiceClientRest
import com.ubirch.util.config.ConfigBase
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.oidc.config.OidcUtilsConfig
import com.ubirch.util.oidc.model.UserContext
import com.ubirch.util.oidc.util.{OidcUtil, UbirchTokenUtil}
import com.ubirch.util.redis.RedisClientUtil
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.Formats
import org.json4s.native.Serialization.read
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-03-17
  */
class OidcDirective()(implicit system: ActorSystem, httpClient: HttpExt, materializer: Materializer)
  extends StrictLogging
    with ConfigBase {

  implicit private val formatter: Formats = JsonFormats.default

  private val envid = config.getString("ubirch.envid").toLowerCase
  private val skipEnvChecking = OidcUtilsConfig.skipEnvChecking()
  private val skipSignatureChecking = OidcUtilsConfig.skipSignatureChecking()
  private val maxTokenAge = OidcUtilsConfig.maxTokenAge()

  val bearerToken: Directive1[Option[String]] = optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)

  val ubirchToken: Directive1[Option[String]] = optionalHeaderValueByName("Authorization")

  val oidcToken2UserContext: Directive1[UserContext] = {

    bearerToken flatMap {

      case None =>

        ubirchToken flatMap {

          case None =>
            reject(AuthorizationFailedRejection)

          case Some(ubToken: String) =>

            onComplete(ubTokenToUserContext(ubToken = ubToken)) flatMap { u =>

              u.map(provide).recover {

                case _: VerificationException =>
                  logger.error(s"Unable to log in with provided token: >>$ubToken<<")
                  reject(AuthorizationFailedRejection).toDirective[Tuple1[UserContext]]

              }.get

            }

        }

      case Some(token: String) =>

        onComplete(tokenToUserContext(token = token)) flatMap { u =>

          u.map(provide).recover {

            case _: VerificationException =>
              logger.error(s"Unable to log in with provided token: $token")
              reject(AuthorizationFailedRejection).toDirective[Tuple1[UserContext]]

          }.get

        }
    }

  }

  private def hashedRedisKey(ubToken: String): String = {
    // TODO redis keys may be hashed but since ubirch tokens are typically stored long-term this would have to include a migration of existing keys (consider adding hash iterations, too)
    //OidcUtil.tokenToHashedKey(s"$envid--$ubToken")
    s"$envid--$ubToken"
  }

  private def ubTokenToUserContext(ubToken: String)(implicit httpClient: HttpExt, materializer: Materializer): Future[UserContext] = {
    val redis = RedisClientUtil.getRedisClient
    //Todo: Use another redis key, as the timestamp and signature always differ
    redis.get[String](hashedRedisKey(ubToken)) flatMap {

      case None =>
        logger.debug("examining token and generating userContext")
        val split = ubToken.split("::")
        if (split.size == 3) {
          val context = split(0).toLowerCase.replace("bearer", "").trim

          if (!skipEnvChecking && !envid.equals(context)) {
            logger.error(s"invalid environment id: $context")
            throw new VerificationException()
          }

          val token = split(1)
          val tokenParts = token.split("##")
          val extUserId = tokenParts(0).toLowerCase.trim
          val tsStr = if (tokenParts.size >= 2)
            Some(tokenParts(1))
          else
            None
          val signature = split(2)

          UserServiceClientRest.userGET(providerId = UbirchTokenUtil.providerId, externalUserId = extUserId).map {

            case Some(user) if user.id.isDefined && user.activeUser =>

              //Todo: Use checkSignature return value as validationResult
              checkSignature(extUserId, token, signature, tsStr, ubToken)
              val validationResult = true

              if (skipSignatureChecking || validationResult) {

                val uc = UserContext(
                  context = context,
                  providerId = UbirchTokenUtil.providerId,
                  externalUserId = extUserId,
                  userName = user.displayName,
                  locale = user.locale
                )

                redis.append[String](hashedRedisKey(ubToken), Json4sUtil.any2String(uc).get)
                uc
              } else {
                logger.error(s"Unable to log in with provided token, signature is invalid: >>$ubToken<<")
                throw new VerificationException()
              }

            case _ =>
              logger.error(s"ubToken contains invalid userId: $extUserId")
              throw new VerificationException()
          }
        } else {
          logger.error(s"invalid ubToken: >>$ubToken<<")
          Future.successful(throw new VerificationException())
        }

      case Some(json) =>

        logger.debug(s"ubToken is valid...will update it's TTL now (redisKey = >>$ubToken<<)")
        updateExpiry(redis, hashedRedisKey(ubToken))
        Future(read[UserContext](json))
    }
  }

  /**
    * This method validates the signature added to the authentication token.
    *
    * @param extUserId the id of the user.
    * @param token     the payload that has been signed.
    * @param signature the signature created with help of the user's private key.
    * @param tsStr     the timestamp that can be used to check the expiration of the auth token.
    * @return validation success
    */
  private def checkSignature(extUserId: String, token: String, signature: String, tsStr: Option[String], ubToken: String): Future[Boolean] = {

    KeyServiceClientRestCacheRedis.currentlyValidPubKeysCached(extUserId)
      .map {

        case Some(pubKeys) =>
          logger.debug(s"received number of pubKeys ${pubKeys.size}")

          val valid = validateSignature(pubKeys, signature, token.getBytes())

          if (valid.contains(true)) {
            logger.debug(s"successfully validated signature of auth token for user $extUserId")
            validateAuthTokenTimeLimit(tsStr)
          } else {
            if (pubKeys.isEmpty)
              logger.warn(s"no public key was found for auth token: $ubToken")
            else
              logger.warn(s"failed validating signature of auth token: with token $ubToken")
            false
          }

        case None =>
          logger.error(s"something went wrong retrieving public key for auth token: $ubToken")
          false
      }
  }


  /**
    * Method to encapsulate validation call with error handling.
    */
  private def validateSignature(pubKeys: Set[PublicKey], signature: String, payload: Array[Byte]) = {
    try {
      pubKeys.map { pubkey: PublicKey =>
        EccUtil.validateSignature(pubkey.pubKeyInfo.pubKey, signature, payload)
      }
    } catch {
      case ex: Throwable => logger.error("something went wrong validating the signature of a userInput: ", ex)
        Set(false)
    }
  }

  /**
    * This method parses the timestamp string and checks if the timestamp used in the
    * signature is too old or still valid. Accordingly the message delivered should
    * become (not) accepted.
    *
    * @param tsStr timestamp string extracted from the auth token.
    */
  private def validateAuthTokenTimeLimit(tsStr: Option[String]): Boolean = {

    val now = new DateTime(DateTimeZone.UTC)

    if (tsStr.isDefined) {

      try {
        val ts = tsStr.get.toLong
        val timestamp = new DateTime(ts, DateTimeZone.UTC)
        if (now.minusMinutes(maxTokenAge).isBefore(timestamp)) true
        else false
      } catch {
        case ex: Throwable =>
          logger.error(s"the timestamp $tsStr of the auth token couldn't become evaluated", ex)
          false
      }

    } else {
      false
    }
  }

  private def tokenToUserContext(token: String): Future[UserContext] = {

    val redis = RedisClientUtil.getRedisClient
    val tokenKey = OidcUtil.tokenToHashedKey(token)
    redis.get[String](tokenKey) map {

      case None =>
        logger.debug(s"token does not exist: redisKey=$tokenKey")
        throw new VerificationException()

      case Some(json) =>
        logger.debug(s"token is valid...will update it's TTL now (tokenKey=$tokenKey)")
        updateExpiry(redis, tokenKey)
        read[UserContext](json)
    }
  }

  private def updateExpiry(redis: RedisClient, tokenKey: String): Future[Boolean] = {

    val refreshInterval = OidcUtilsConfig.redisUpdateExpiry
    redis.expire(tokenKey, seconds = refreshInterval) map {
      res =>

        if (res) {
          logger.debug(s"refreshed token expiry ($refreshInterval seconds): tokenKey: $tokenKey")
        } else {
          logger.error(s"failed to refresh token expiry ($refreshInterval seconds): tokenKey: $tokenKey")
        }
        res
    }
  }

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }
}

class VerificationException() extends Exception
