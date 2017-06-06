package com.ubirch.util.model

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
      val expected = """{"version":"1.0","status":"OK","messages":[]}"""
      json should be(expected)

    }

    scenario("NOK; with two messages") {

      // prepare
      val deepCheckResult = DeepCheckResponse(status = "NOK", messages = Seq("foo", "bar"))

      // test
      val json = deepCheckResult.toJsonString

      // verify
      val expected = """{"version":"1.0","status":"NOK","messages":["foo","bar"]}"""
      json should be(expected)

    }

  }

}
