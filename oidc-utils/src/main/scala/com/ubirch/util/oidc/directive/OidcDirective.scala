package com.ubirch.util.oidc.directive

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import akka.stream.Materializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.user.client.rest.UserServiceClientRest
import com.ubirch.util.config.ConfigBase
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.oidc.config.OidcUtilsConfig
import com.ubirch.util.oidc.model.UserContext
import com.ubirch.util.oidc.util.OidcUtil
import com.ubirch.util.redis.RedisClientUtil
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.Formats
import org.json4s.native.Serialization.read
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
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
  private val skipSignatureChecking = config.getBoolean("ubirch.oidcUtils.skipSignatureChecking")
  private val maxTokenAge = config.getInt("ubirch.oidcUtils.maxTokenAge")
  private val skipTokenAgeCheck = config.getInt("ubirch.oidcUtils.maxTokenAge")

  val bearerToken: Directive1[Option[String]] =
    optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)

  val ubirchToken: Directive1[Option[String]] =
    optionalHeaderValueByName("Authorization")

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
                  logger.error(s"Unable to log in with provided token: $ubToken")
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

  private def ubTokenToUserContext(ubToken: String)(implicit httpClient: HttpExt, materializer: Materializer): Future[UserContext] = {
    val redis = RedisClientUtil.getRedisClient
    redis.get[String](ubToken) flatMap {

      case None =>
        val splt = ubToken.split("::")
        if (splt.size == 3) {
          val context = splt(0).toLowerCase.replace("bearer", ""). trim

          if (!envid.equals(context)) {
            logger.debug(s"invalid enviroment id: $context")
            throw new VerificationException()
          }

          val token = splt(1)
          val tokenParts = token.split("##")
          val extUserId = tokenParts(0)
          val tsStr = if (tokenParts.size >= 2)
            Some(tokenParts(1))
          else
            None
          val signature = splt(2)

          UserServiceClientRest.userGET(providerId = "ubirchToken", externalUserId = extUserId).map {
            case Some(user) if user.id.isDefined && user.activeUser =>

              if (skipSignatureChecking || checkSignature(extUserId, token, signature, tsStr)) {

                val uc = UserContext(
                  context = context,
                  providerId = "ubirchToken",
                  externalUserId = extUserId,
                  userName = user.displayName,
                  locale = user.locale,
                  authToken = Some(ubToken)
                )

                redis.append[String](ubToken, Json4sUtil.any2String(uc).get)
                uc
              }
              else {
                logger.debug(s"Unable to log in with provided token signature is invalid: redisKey=$ubToken")
                throw new VerificationException()
              }
            case _ =>
              logger.debug(s"ubToken contains invalid userId: redisKey=$ubToken")
              throw new VerificationException()
          }
        }
        else {
          logger.debug(s"invalid ubToken: redisKey=$ubToken")
          Future(throw new VerificationException())
        }
      case Some(json)
      =>
        logger.debug(s"ubToken is valid...will update it's TTL now (tokenKey=$ubToken)")
        updateExpiry(redis, ubToken)
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
