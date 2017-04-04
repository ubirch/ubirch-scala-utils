package com.ubirch.util.oidc.directive

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.json.JsonFormats
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
class OidcDirective()(implicit system: ActorSystem)
  extends StrictLogging {

  implicit private val formatter = JsonFormats.default

  private val bearerToken: Directive1[Option[String]] =
    optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)

  val oidcToken2UserContext: Directive1[UserContext] = {

    bearerToken flatMap {

      case None => reject(AuthorizationFailedRejection)

      case Some(token) =>

        onComplete(tokenToUserContext(token = token)) flatMap {

          _.map(provide).recover {

            case _: VerificationException =>
              logger.error("Unable to log in with provided token")
              reject(AuthorizationFailedRejection).toDirective[Tuple1[UserContext]]

          }.get

        }

    }

  }

  private def tokenToUserContext(token: String): Future[UserContext] = {
    val redis = RedisClientUtil.getRedisClient
    val tokenKey = OidcUtil.tokenToHashedKey(token)
    redis.get[String](tokenKey) map {

      case None =>
        logger.debug(s"token does not exist: redisKey=$token")
        throw new VerificationException()

      case Some(json) =>
        logger.debug(s"token is valid...will update it's TTL now")
        updateExpiry(redis, tokenKey)
        read[UserContext](json)

    }

  }

  private def updateExpiry(redis: RedisClient, tokenKey: String): Future[Boolean] = {

    val refreshInterval = OidcUtilsConfig.redisUpdateExpiry
    redis.expire(tokenKey, seconds = refreshInterval)

  }

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }

}

class VerificationException() extends Exception
