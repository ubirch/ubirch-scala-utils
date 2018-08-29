package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.net.InetAddress

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig

import org.elasticsearch.common.transport.TransportAddress
import org.scalatest.{FeatureSpec, Matchers}

import scala.collection.JavaConversions._

/**
  * author: cvandrei
  * since: 2017-04-13
  */
class ESClientSpec extends FeatureSpec
  with ESClient
  with Matchers {

  feature("esClient") {

    scenario("addresses") {

      // verify
      val hosts = ESConfig.hosts map { host =>
        new TransportAddress(InetAddress.getByName(host.host), host.port)
      }
      esClient.transportAddresses() foreach { address =>
        hosts.contains(address) shouldBe true
      }

    }

    scenario("settings") {

      // verify
      esClient.settings.get("cluster.name") shouldBe "my-test-cluster"
      esClient.settings.get("xpack.security.transport.ssl.enabled") shouldBe "true"

    }

  }

}
