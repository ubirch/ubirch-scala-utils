package com.ubirch.util.elasticsearch.config

import org.scalatest.{FeatureSpec, Matchers}

class ESConfigSpec extends FeatureSpec with Matchers {

  feature("hosts()") {

    scenario("read list of hosts from config") {

      // verify
      EsHighLevelConfig.host shouldBe "localhost"
      EsHighLevelConfig.port shouldBe 9200
      EsHighLevelConfig.scheme shouldBe "http"
    }

  }

  feature("settings()") {

    scenario("read settings") {

      // test
      val settings = EsHighLevelConfig.settings

      // verify
      settings("cluster.name") shouldBe "my-test-cluster"

    }

  }

}
