package com.ubirch.util.oidc.directive

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.oidc.config.{OidcUtilsConfig, OidcUtilsConfigKeys}
import com.ubirch.util.oidc.util.{OidcHeaders, OidcUtil}
import com.ubirch.util.redis.RedisClientUtil

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-17
  */
class OidcDirective(configPrefix: String = OidcUtilsConfigKeys.PREFIX)(implicit system: ActorSystem) extends StrictLogging {

  private val ubirchContextFromHeader: Directive1[String] = headerValueByName(OidcHeaders.CONTEXT)

  private val ubirchProviderFromHeader: Directive1[String] = headerValueByName(OidcHeaders.PROVIDER)

  private val bearerToken: Directive1[Option[String]] =
    optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)

  val oidcToken2UserContext: Directive1[UserContext] = {

    ubirchContextFromHeader.flatMap { context =>
      ubirchProviderFromHeader.flatMap { provider =>
        bearerToken.flatMap {

          case None => reject(AuthorizationFailedRejection)

          case Some(token) =>

            onComplete(
              tokenToUserId(
                provider = provider,
                context = context,
                token = token
              )
            ).flatMap {

              _.map { userContext =>

                provide(userContext)

              }.recover {
                case e: VerificationException =>
                  logger.error("Unable to log in with provided token", e)
                  reject(AuthorizationFailedRejection).toDirective[Tuple1[UserContext]]
              }.get

            }

        }
      }
    }

  }

  private def tokenToUserId(provider: String,
                            context: String,
                            token: String
                           ): Future[UserContext] = {

    val tokenKey = OidcUtil.tokenToHashedKey(provider, token)
    val redis: RedisClient = RedisClientUtil.newInstance(configPrefix)(system)
    redis.get[String](tokenKey) map {

      case None =>
        logger.debug(s"token does not exist: $provider:$token")
        throw new VerificationException()

      case Some(userId) =>
        updateExpiry(redis, tokenKey)
        UserContext(context = context, userId = userId)

    }

  }

  private def updateExpiry(redis: RedisClient, tokenKey: String): Future[Boolean] = {

    val refreshInterval = OidcUtilsConfig.redisUpdateExpiry(configPrefix)
    redis.expire(tokenKey, seconds = refreshInterval)

  }

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }

}

case class UserContext(context: String, userId: String)

class VerificationException() extends Exception
