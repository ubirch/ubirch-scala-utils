package com.ubirch.util.oidc.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-03-21
  */
object OidcUtilsConfig extends ConfigBase {

  final def redisUpdateExpirySeconds(): Long = config.getLong(OidcUtilsConfigKeys.updateExpirySeconds)

  final def skipEnvChecking(): Boolean = config.getBoolean(OidcUtilsConfigKeys.skipEnvChecking)

  final def allowInvalidSignature(): Boolean = config.getBoolean(OidcUtilsConfigKeys.allowInvalidSignature)

  final def maxTokenAge(): Int = config.getInt(OidcUtilsConfigKeys.maxTokenAge)

}
