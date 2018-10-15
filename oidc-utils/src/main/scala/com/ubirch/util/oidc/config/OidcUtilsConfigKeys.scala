package com.ubirch.util.oidc.config

/**
  * author: cvandrei
  * since: 2017-03-21
  */
object OidcUtilsConfigKeys {

  final val PREFIX = "ubirch.oidcUtils"

  private final val redis = "redis"

  final val updateExpiry = s"$PREFIX.$redis.updateExpiry"

  final val skipEnvChecking = s"$PREFIX.skipEnvChecking"

  final val skipSignatureChecking = s"$PREFIX.skipSignatureChecking"

  final val maxTokenAge = s"$PREFIX.maxTokenAge"

}
