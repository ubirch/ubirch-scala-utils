package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.net.InetAddress

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig

import org.elasticsearch.common.transport.InetSocketTransportAddress
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
        new InetSocketTransportAddress(InetAddress.getByName(host.host), host.port)
      }
      val c = esClient
      esClient.transportAddresses() foreach { address =>
        hosts.contains(address) shouldBe true
      }

    }

    scenario("settings") {

      // verify
      esClient.settings.get("cluster.name") shouldBe "my-test-cluster"
      esClient.settings.get("shield.transport.ssl") shouldBe "true"

    }

  }

}
