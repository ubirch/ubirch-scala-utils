package com.ubirch.util.oidc.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-03-21
  */
object OidcUtilsConfig extends ConfigBase {

  final val redisUpdateExpiry = config.getLong(OidcUtilsConfigKeys.updateExpiry)

  final def skipEnvChecking(): Boolean = config.getBoolean(OidcUtilsConfigKeys.skipEnvChecking)

  final def skipSignatureChecking(): Boolean = config.getBoolean(OidcUtilsConfigKeys.skipSignatureChecking)

  final def maxTokenAge(): Int = config.getInt(OidcUtilsConfigKeys.maxTokenAge)

}
