package com.ubirch.util.oidc.util

import com.ubirch.crypto.ecc.EccUtil
import com.ubirch.crypto.hash.HashUtil

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2018-03-15
  */
class UbirchTokenUtilSpec extends FeatureSpec with Matchers {

  feature("toUbirchToken()") {

    scenario("without private key") {

      // prepare
      val context = "ubirch-local"
      val email = "test@ubirch.com"

      // test
      val result = UbirchTokenUtil.toUbirchToken(
        context = context,
        email = email
      )

      // verify
      val hashedEmail = UbirchTokenUtil.hashEmail(email)
      val expected = s"$context${UbirchTokenUtil.delim}$hashedEmail${UbirchTokenUtil.delim}${UbirchTokenUtil.defaultSignature}"
      result shouldBe expected

    }

    scenario("with private key") {

      // prepare
      val context = "ubirch-local"
      val (_, privateKey) = EccUtil.generateEccKeyPairEncoded
      val email = "test@ubirch.com"

      // test
      val result = UbirchTokenUtil.toUbirchToken(
        context = context,
        email = email,
        privateKey = Some(privateKey)
      )

      // verify
      val hashedEmail = UbirchTokenUtil.hashEmail(email)
      val expected = s"$context${UbirchTokenUtil.delim}$hashedEmail${UbirchTokenUtil.delim}${UbirchTokenUtil.defaultSignature}"
      result shouldBe expected

    }

  }

  feature("hashEmail()") {

    scenario("hash --> sha256HexString() is applied") {

      // prepare
      val email = "test@ubirch.com"

      // test
      val result = UbirchTokenUtil.hashEmail(email)

      // verify
      val expected = HashUtil.sha256HexString(email)
      result shouldBe expected

    }

  }

}
