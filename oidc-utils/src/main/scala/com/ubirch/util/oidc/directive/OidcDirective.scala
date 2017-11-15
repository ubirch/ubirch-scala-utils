package com.ubirch.util.oidc.directive

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import akka.stream.Materializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.user.client.rest.UserServiceClientRest
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.oidc.config.OidcUtilsConfig
import com.ubirch.util.oidc.model.UserContext
import com.ubirch.util.oidc.util.OidcUtil
import com.ubirch.util.redis.RedisClientUtil
import org.json4s.native.Serialization.read
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-17
  */
class OidcDirective()(implicit system: ActorSystem, httpClient: HttpExt, materializer: Materializer)
  extends StrictLogging {

  implicit private val formatter = JsonFormats.default

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
                  logger.error("Unable to log in with provided token")
                  reject(AuthorizationFailedRejection).toDirective[Tuple1[UserContext]]

              }.get

            }

        }

      case Some(token: String) =>

        onComplete(tokenToUserContext(token = token)) flatMap { u =>

          u.map(provide).recover {

            case _: VerificationException =>
              logger.error("Unable to log in with provided token")
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
          val context = splt(0)
          val mailHash = splt(1)
          val signature = splt(2)
          UserServiceClientRest.userGET(providerId = "ubirchToken", externalUserId = mailHash).map {
            case Some(user) if user.id.isDefined =>
              val uc = UserContext(
                context = context,
                providerId = "ubirchToken",
                userId = mailHash,
                userName = user.displayName,
                locale = user.locale
              )
              redis.append[String](ubToken, Json4sUtil.any2String(uc).get)
              uc
            case _ =>
              logger.debug(s"ubToken does not exist: redisKey=$ubToken")
              throw new VerificationException()
          }
        }
        else {
          logger.debug(s"ubToken does not exist: redisKey=$ubToken")
          Future(throw new VerificationException())
        }
      case Some(json) =>
        logger.debug(s"ubToken is valid...will update it's TTL now (tokenKey=$ubToken)")
        updateExpiry(redis, ubToken)
        Future(read[UserContext](json))
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
    redis.expire(tokenKey, seconds = refreshInterval) map { res =>

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
