package com.ubirch.util.deepCheck.model

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2017-06-06
  */
class DeepCheckResponseSpec extends FeatureSpec
  with Matchers {

  feature("toJsonString()") {

    scenario("default object") {

      // prepare
      val deepCheckResult = DeepCheckResponse()

      // test
      val json = deepCheckResult.toJsonString

      // verify
      val expected = """{"status":true,"messages":[]}"""
      json should be(expected)

    }

    scenario("NOK; with two messages") {

      // prepare
      val deepCheckResult = DeepCheckResponse(status = false, messages = Seq("foo", "bar"))

      // test
      val json = deepCheckResult.toJsonString

      // verify
      val expected = """{"status":false,"messages":["foo","bar"]}"""
      json should be(expected)

    }

  }

}
