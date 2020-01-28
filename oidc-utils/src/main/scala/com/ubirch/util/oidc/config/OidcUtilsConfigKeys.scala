package com.ubirch.util.oidc.config

/**
  * author: cvandrei
  * since: 2017-03-21
  */
object OidcUtilsConfigKeys {

  final val PREFIX = "ubirch.oidcUtils"

  private final val redis = "redis"

  final val updateExpirySeconds = s"$PREFIX.$redis.updateExpirySeconds"

  final val skipEnvChecking = s"$PREFIX.skipEnvChecking"

  final val allowInvalidSignature = s"$PREFIX.allowInvalidSignature"

  final val maxTokenAge = s"$PREFIX.maxTokenAge"

}
