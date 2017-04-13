package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.net.InetAddress

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.shield.ShieldPlugin

/**
  * author: cvandrei
  * since: 2017-02-24
  */
trait ESClient {

  private val hostAddresses: Set[InetSocketTransportAddress] = ESConfig.hosts map { host =>
    new InetSocketTransportAddress(InetAddress.getByName(host.host), host.port)
  }

  final val esClient: TransportClient = {

    val builder = ESConfig.cluster match {

      case None => TransportClient.builder()

      case Some(cluster) =>

        val settings: Settings = Settings.builder()
          .put("cluster.name", cluster) // TODO refactor to read from general settings config
          /* TODO Shield/X-Pack config: read from general settings section
          .put("shield.user", "transport_client_user:changeme")
          .put("shield.ssl.keystore.path", "/path/to/client.jks")
          .put("shield.ssl.keystore.password", "password")
          .put("shield.transport.ssl", "true")
          */
          .build()

        TransportClient.builder()
          //.addPlugin(classOf[ShieldPlugin]) // TODO set based on new config key: shieldEnabled: Boolean = false
          .settings(settings)

    }

    val client = builder.build()
    hostAddresses foreach client.addTransportAddress

    client

  }

}

object ESClient extends ESClient {

  final val client: TransportClient = esClient

}
