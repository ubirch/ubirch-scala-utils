package com.ubirch.util.oidc.config

/**
  * author: cvandrei
  * since: 2017-03-21
  */
object OidcUtilsConfigKeys {

  final val PREFIX = "ubirch.oidcUtils"

  private final val redis = "redis"

  final def updateExpiry = s"$PREFIX.$redis.updateExpiry"
}
