package com.ubirch.util.oidc.util

import com.ubirch.crypto.hash.HashUtil

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2017-02-10
  */
class OidcUtilSpec extends FeatureSpec
  with Matchers {

  feature("stateToHashedKey()") {

    scenario("all strings empty") {
      runStateTest("", "")
    }

    scenario("provider string empty") {
      runStateTest("", "123123")
    }

    scenario("state string empty") {
      runStateTest("google", "")
    }

    scenario("none of the strings empty") {
      runStateTest("google", "123123")
    }

  }

  feature("tokenToHashedKey()") {

    scenario("all strings empty") {
      runTokenTest("", "")
    }

    scenario("provider string empty") {
      runTokenTest("", "12341234")
    }

    scenario("token string empty") {
      runTokenTest("google", "")
    }

    scenario("none of the strings empty") {
      runTokenTest("google", "12341234")
    }

  }

  private def runStateTest(provider: String, state: String) = {

    // test
    val result = OidcUtil.stateToHashedKey(provider, state)

    // verify
    val expected = HashUtil.sha256HexString(s"state:$provider:$state")
    result shouldBe expected

  }

  private def runTokenTest(provider: String, token: String) = {

    // test
    val result = OidcUtil.tokenToHashedKey(provider, token)

    // verify
    val expected = HashUtil.sha256HexString(s"token:$provider:$token")
    result shouldBe expected

  }

}
