package com.ubirch.util.elasticsearch.client.binary.config

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2017-04-13
  */
class ESConfigSpec extends FeatureSpec
  with Matchers {

  feature("hosts()") {

    scenario("read list of hosts from config") {

      // test
      val hosts = ESConfig.hosts

      // verify
      val expected = Set(
        HostUri("localhost", 9300),
        HostUri("localhost", 9301)
      )
      hosts shouldBe expected

    }

  }

}
