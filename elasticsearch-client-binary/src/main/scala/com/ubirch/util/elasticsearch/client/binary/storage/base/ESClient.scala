package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.net.InetAddress

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress

/**
  * author: cvandrei
  * since: 2017-02-24
  */
trait ESClient {

  private val address = new InetSocketTransportAddress(InetAddress.getByName(ESConfig.host), ESConfig.port)

  final val esClient: TransportClient = {

    val builder = ESConfig.cluster match {

      case None => TransportClient.builder()

      case Some(cluster) =>

        val settings: Settings = Settings.builder()
          .put("cluster.name", cluster)
          .build()

        TransportClient.builder()
          .settings(settings)

    }

    builder.build()
      .addTransportAddress(address)

  }

}

object ESClient extends ESClient {

  final val client: TransportClient = esClient

}
