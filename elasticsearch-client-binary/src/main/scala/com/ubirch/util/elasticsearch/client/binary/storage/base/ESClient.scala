package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.net.InetAddress

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.{InetSocketTransportAddress, TransportAddress}
import org.elasticsearch.shield.ShieldPlugin

/**
  * author: cvandrei
  * since: 2017-02-24
  */
trait ESClient {

  private val hostAddresses: Set[TransportAddress] = ESConfig.hosts map { host =>
    new InetSocketTransportAddress(InetAddress.getByName(host.host), host.port)
  }

  final val esClient: TransportClient = {

    var client = TransportClient.builder()
      .settings(connectionSettings())

    if (ESConfig.xPackEnabled) {
      client = client.addPlugin(classOf[ShieldPlugin])
    }

    client.build()
      .addTransportAddresses(hostAddresses.toSeq: _*)

  }

  private def connectionSettings(): Settings = {

    val settingsBuilder = Settings.builder()
    ESConfig.settings foreach { setting =>
      settingsBuilder.put(setting._1, setting._2)
    }

    settingsBuilder.build()

  }

}

object ESClient extends ESClient {

  final val client: TransportClient = esClient

}
