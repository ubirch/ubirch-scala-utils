package com.ubirch.util.redis.config

/**
  * author: cvandrei
  * since: 2017-03-15
  */
object ConfigKeys {

  private val redisUtilPrefix = s"ubirch.redisUtil"

  final val REDIS_HOST = s"$redisUtilPrefix.host"

  final val REDIS_PORT = s"$redisUtilPrefix.port"

  final val REDIS_PASSWORD = s"$redisUtilPrefix.password"

}
