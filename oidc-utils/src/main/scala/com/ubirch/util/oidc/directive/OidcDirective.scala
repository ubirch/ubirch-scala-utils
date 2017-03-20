package com.ubirch.util.oidc.directive

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.oidc.util.OidcUtil
import com.ubirch.util.redis.RedisClientUtil

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, Directives, Route}
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-17
  */
trait OidcDirective extends Directives
  with StrictLogging {

  def verifyToken(routes: => Route, configPrefix: String)(implicit system: ActorSystem): Directive1[String] = {

    ubirchContextFromHeader { context =>
      ubirchProviderFromHeader { provider =>

        val token: String = "" // TODO extract from "Authorization" header (use authenticateOAuth2Async directive?)

        checkTokenExists(
          configPrefix = configPrefix,
          provider = provider,
          token = token,
          system = system
        ) map {
          case Some(userId) => (context, userId)
          case None => complete(StatusCodes.OK) // TODO complete w/ errorb
        }

      }
    }

  }

  private def checkTokenExists(configPrefix: String,
                               provider: String,
                               token: String,
                               system: ActorSystem
                              ): Future[Option[String]] = {

    val tokenKey = OidcUtil.tokenToHashedKey(provider, token)
    val redis: RedisClient = RedisClientUtil.newInstance(configPrefix)(system)
    redis.get[String](tokenKey) flatMap {

      case None =>
        logger.debug(s"token does not exist: $provider:$token")
        Future(None)

      case Some(userId) =>

        updateExpiry(redis, tokenKey) map {

          case true =>
            logger.debug(s"updated token expiry")
            Some(userId)

          case false =>
            logger.error(s"failed to update token expiry")
            Some(userId)

        }

    }

  }

  private def updateExpiry(redis: RedisClient, tokenKey: String): Future[Boolean] = {

    val refreshInterval = 1800L // 30 minutes TODO read from config
    redis.expire(tokenKey, seconds = refreshInterval)

  }

  val ubirchContextFromHeader: Directive1[String] = headerValueByName("X-UBIRCH-CONTEXT")

  val ubirchProviderFromHeader: Directive1[String] = headerValueByName("X-UBIRCH-PROVIDER")

}
