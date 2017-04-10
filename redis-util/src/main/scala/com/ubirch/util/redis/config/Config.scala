package com.ubirch.util.redis.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-03-15
  */

case class RedisConfig(host: String, port: Int, password: Option[String])

object Config extends ConfigBase {

  def redisConfig = RedisConfig(
    host = config.getString(ConfigKeys.REDIS_HOST),
    port = config.getInt(ConfigKeys.REDIS_PORT),
    password = getPassword
  )

  private def getPassword: Option[String] = {

    val key = ConfigKeys.REDIS_PASSWORD

    if (config.hasPath(key)) {
      Some(config.getString(key))
    } else {
      None
    }

  }
}
