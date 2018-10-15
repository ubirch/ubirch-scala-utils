package com.ubirch.util.oidc.directive

import com.typesafe.scalalogging.slf4j.StrictLogging

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

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import akka.stream.Materializer
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

  private def hashedRedisKey(ubToken: String): String = OidcUtil.tokenToHashedKey(s"$envid--$ubToken")

  private def ubTokenToUserContext(ubToken: String)(implicit httpClient: HttpExt, materializer: Materializer): Future[UserContext] = {
    val redis = RedisClientUtil.getRedisClient
    redis.get[String](hashedRedisKey(ubToken)) flatMap {

      case None =>

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

              if (skipSignatureChecking || checkSignature(extUserId, token, signature, tsStr)) {

                val uc = UserContext(
                  context = context,
                  providerId = UbirchTokenUtil.providerId,
                  externalUserId = extUserId,
                  userName = user.displayName,
                  locale = user.locale
                )

                redis.append[String](hashedRedisKey(ubToken), Json4sUtil.any2String(uc).get)
                uc
              }
              else {
                logger.error(s"Unable to log in with provided token, signature is invalid: >>$ubToken<<")
                throw new VerificationException()
              }
            case _ =>
              logger.error(s"ubToken contains invalid userId: $extUserId")
              throw new VerificationException()
          }
        }
        else {
          logger.error(s"invalid ubToken: >>$ubToken<<")
          Future(throw new VerificationException())
        }

      case Some(json) =>

        logger.debug(s"ubToken is valid...will update it's TTL now (redisKey = >>$ubToken<<)")
        updateExpiry(redis, hashedRedisKey(ubToken))
        Future(read[UserContext](json))
    }
  }

  private def checkSignature(extUserId: String, token: String, signature: String, tsStr: Option[String]): Boolean = {

    //var pubKeys = Await.result(KeyServiceClientRest.currentlyValidPubKeys(extUserId), 5 seconds)

    val now = new DateTime(DateTimeZone.UTC)
    if (tsStr.isDefined) {
      val ts = tsStr.get.toLong
      val timestamp = new DateTime(ts, DateTimeZone.UTC)

      if (now.minusMinutes(maxTokenAge).isBefore(timestamp))
        true
      else {
        false
      }
    }
    false
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
