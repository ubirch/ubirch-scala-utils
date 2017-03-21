package com.ubirch.util.oidc.config

/**
  * author: cvandrei
  * since: 2017-03-21
  */
object OidcUtilsConfigKeys {

  final val PREFIX = "ubirch.oidc-utils.redis"

  final def updateExpiry(prefix: String) = s"$prefix.updateExpiry"

}
