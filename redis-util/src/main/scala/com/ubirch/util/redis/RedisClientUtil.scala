package com.ubirch.util.redis

import com.ubirch.util.model.DeepCheckResponse
import com.ubirch.util.redis.config.Config

import akka.actor.ActorSystem
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-15
  */

object RedisClientUtil {

  private val redisConfig = Config.redisConfig

  private var redisClient: Option[RedisClient] = None

  /**
    * Gives us an active redis client. If none exists yet it'll be created.
    *
    * @param _system actor system required for Redis connection
    * @return redis client
    */
  def getRedisClient()(implicit _system: ActorSystem): RedisClient = {

    if (redisClient.isEmpty) {

      redisClient = Some(
        RedisClient(
          host = redisConfig.host,
          port = redisConfig.port,
          password = redisConfig.password
        )
      )

    }

    redisClient.get

  }

  /**
    * Check if we can run a simple query on the database.
    *
    * @param _system actor system required for Redis connection
    * @return deep check response with _status:OK_ if ok; otherwise with _status:NOK_
    */
  def connectivityCheck(serviceName: String)
                       (implicit _system: ActorSystem): Future[DeepCheckResponse] = {

    getRedisClient()
      .exists("1")
      .map(_ => DeepCheckResponse())
      .recover {

        case t: Throwable =>
          DeepCheckResponse(
            status = "NOK",
            messages = Seq(s"[$serviceName] ${t.getMessage}")
          )

      }

  }

}
