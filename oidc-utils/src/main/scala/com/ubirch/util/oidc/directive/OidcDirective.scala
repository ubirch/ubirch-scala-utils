package com.ubirch.util.oidc.directive

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.oidc.util.OidcUtil
import com.ubirch.util.redis.RedisClientUtil

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1, Directives, Route}
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-17
  */
trait OidcDirective extends Directives
  with StrictLogging {

  def oidcToken2UserContext(routes: => Route, configPrefix: String)(implicit system: ActorSystem): Directive1[UserContext] = {

    ubirchContextFromHeader.flatMap { context =>
      ubirchProviderFromHeader.flatMap { provider =>
        bearerToken.flatMap {

          case None => reject(AuthorizationFailedRejection)

          case Some(token) =>

            onComplete(
              tokenToUserId(
                configPrefix = configPrefix,
                provider = provider,
                context = context,
                token = token,
                system = system
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

  private def tokenToUserId(configPrefix: String,
                            provider: String,
                            context: String,
                            token: String,
                            system: ActorSystem
                           ): Future[UserContext] = {

    val tokenKey = OidcUtil.tokenToHashedKey(provider, token)
    val redis: RedisClient = RedisClientUtil.newInstance(configPrefix)(system)
    redis.get[String](tokenKey) map {

      case None =>
        logger.debug(s"token does not exist: $provider:$token")
        throw new VerificationException()

      case Some(userId) =>
        updateExpiry(configPrefix, redis, tokenKey)
        UserContext(context = context, userId = userId)

    }

  }


  private def updateExpiry(configPrefix: String,
                           redis: RedisClient,
                           tokenKey: String
                          ): Future[Boolean] = {

    val refreshInterval = 1800L // 30 minutes TODO read from config
    redis.expire(tokenKey, seconds = refreshInterval)

  }

  val ubirchContextFromHeader: Directive1[String] = headerValueByName("X-UBIRCH-CONTEXT")

  val ubirchProviderFromHeader: Directive1[String] = headerValueByName("X-UBIRCH-PROVIDER")

  val bearerToken: Directive1[Option[String]] =
    optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }

}

case class UserContext(context: String, userId: String)

class VerificationException() extends Exception
