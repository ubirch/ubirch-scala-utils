package com.ubirch.util.oidc.directive

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.oidc.util.OidcUtil
import com.ubirch.util.redis.RedisClientUtil

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-17
  */
trait OidcDirective extends Directives
  with StrictLogging {

  def verifyToken(routes: => Route)(implicit system: ActorSystem): Route = {

    val provider: String = "" // TODO extract from header (needed to check existence of token in Redis)
    val token: String = "" // TODO extract from "Authorization" header (use authenticateOAuth2Async directive?)

    // TODO extract context header?

    val userId = checkTokenExists(provider = provider, token = token, system = system)

    complete(OK)

  }

  private def checkTokenExists(provider: String,
                               token: String,
                               system: ActorSystem
                              ): Future[Option[String]] = {

    val tokenKey = OidcUtil.tokenToHashedKey(provider, token)
    val redis: RedisClient = RedisClientUtil.newInstance("")(system)
    redis.get[String](tokenKey) flatMap {

      case None =>
        logger.debug(s"token does not exist: $provider:$token")
        Future(None)

      case Some(userId) =>

        val refreshInterval = 1800L // 30 minutes TODO read from config
        redis.expire(tokenKey, seconds = refreshInterval) map {

          case true =>
            logger.debug(s"update token expiry by another $refreshInterval seconds")
            Some(userId)

          case false =>
            logger.error(s"failed to update token expiry")
            Some(userId)

        }

    }

  }

}
