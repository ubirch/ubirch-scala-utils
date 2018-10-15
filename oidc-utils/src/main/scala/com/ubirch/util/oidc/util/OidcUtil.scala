package com.ubirch.util.oidc.util

import com.ubirch.crypto.hash.HashUtil

/**
  * author: cvandrei
  * since: 2017-02-09
  */
object OidcUtil {

  def stateToHashedKey(provider: String, state: String): String = {
    val key = s"state:$provider:$state"
    HashUtil.sha256HexString(key)
  }

  def tokenToHashedKey(token: String): String = HashUtil.sha512HexString(token)

}
