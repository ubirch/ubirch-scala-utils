package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.net.InetAddress

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient

/**
  * author: cvandrei
  * since: 2017-02-24
  */
trait ESClient {

  private val hostAddresses: Set[TransportAddress] = ESConfig.hosts map { host =>
    new TransportAddress(InetAddress.getByName(host.host), host.port)
  }

  final val esClient: TransportClient = {

    val settings = connectionSettings()

    val client = if (ESConfig.xPackEnabled) {
      new PreBuiltXPackTransportClient(settings)
    } else {
      new PreBuiltTransportClient(settings)
    }

    client.addTransportAddresses(hostAddresses.toSeq: _*)

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
