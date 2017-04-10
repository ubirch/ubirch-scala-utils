package com.ubirch.util.redis

import akka.actor.ActorSystem
import com.ubirch.util.redis.config.Config
import redis.RedisClient

/**
  * author: cvandrei
  * since: 2017-03-15
  */

object RedisClientUtil {

  private val redisConfig = Config.redisConfig

  private var redisClient: Option[RedisClient] = None

  /**
    *
    * @param _system
    * @return
    */
  def getRedisClient()(implicit _system: ActorSystem) = {
    if (redisClient.isEmpty)
      redisClient = Some(RedisClient(
        host = redisConfig.host,
        port = redisConfig.port,
        password = redisConfig.password
      ))
    redisClient.get
  }
}
