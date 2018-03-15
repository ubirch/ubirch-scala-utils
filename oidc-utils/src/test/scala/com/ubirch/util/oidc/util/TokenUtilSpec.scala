package com.ubirch.util.oidc.util

import com.ubirch.crypto.ecc.EccUtil
import com.ubirch.crypto.hash.HashUtil

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2018-03-15
  */
class TokenUtilSpec extends FeatureSpec with Matchers {

  feature("toUbirchToken()") {

    scenario("without private key") {

      // prepare
      val context = "ubirch-local"
      val email = "test@ubirch.com"

      // test
      val result = TokenUtil.toUbirchToken(
        context = context,
        email = email
      )

      // verify
      val hashedEmail = HashUtil.sha256HexString(email)
      val expected = s"$context${TokenUtil.delim}$hashedEmail${TokenUtil.delim}${TokenUtil.defaultSignature}"
      result shouldBe expected

    }

    scenario("with private key") {

      // prepare
      val context = "ubirch-local"
      val (_, privateKey) = EccUtil.generateEccKeyPairEncoded
      val email = "test@ubirch.com"

      // test
      val result = TokenUtil.toUbirchToken(
        context = context,
        email = email,
        privateKey = Some(privateKey)
      )

      // verify
      val hashedEmail = HashUtil.sha256HexString(email)
      val expected = s"$context${TokenUtil.delim}$hashedEmail${TokenUtil.delim}${TokenUtil.defaultSignature}"
      result shouldBe expected

    }

  }

}
