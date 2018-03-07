package com.ubirch.util.config

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2018-03-07
  */
class ConfigBaseSpec extends FeatureSpec
  with Matchers
  with ConfigBase {

  feature("environmentId()") {

    scenario("read config --> ubirch-local") {
      environmentId() shouldBe "test-local"
    }

  }

}
