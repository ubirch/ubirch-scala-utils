package com.ubirch.util.redis.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-03-15
  */
object Config extends ConfigBase {

  def hostAndPort(configPrefix: String): (String, Int) = {

    val finalPrefix = getFinalPrefix(configPrefix)

    val host = config.getString(s"$finalPrefix${ConfigKeys.HOST}")
    val port = config.getInt(s"$finalPrefix${ConfigKeys.PORT}")

    (host, port)

  }

  def password(configPrefix: String): Option[String] = {

    val finalPrefix = getFinalPrefix(configPrefix)
    val key = s"$finalPrefix${ConfigKeys.PASSWORD}"

    if (config.hasPath(key)) {
      Some(key)
    } else {
      None
    }

  }

  private def getFinalPrefix(prefix: String): String = {

    if (prefix == "") {
      ""
    } else {
      s"$prefix."
    }

  }

}
