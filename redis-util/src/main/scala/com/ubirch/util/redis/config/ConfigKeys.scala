package com.ubirch.util.redis.config

/**
  * author: cvandrei
  * since: 2017-03-15
  */
object ConfigKeys {

  private val redis = s"redis"

  final val HOST = s"$redis.host"

  final val PORT = s"$redis.port"

  final val PASSWORD = s"$redis.password"

}
