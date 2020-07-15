package com.ubirch.util.elasticsearch

import com.ubirch.util.elasticsearch.config.EsHighLevelConfig
import org.apache.http.HttpHost
import org.elasticsearch.client.{RestClient, RestHighLevelClient}

trait EsHighLevelClient {

  private val host = EsHighLevelConfig.host
  private val port = EsHighLevelConfig.port
  private val scheme = EsHighLevelConfig.scheme

  val esClient: RestHighLevelClient = new RestHighLevelClient(
    RestClient.builder(
      new HttpHost(host, port, scheme)))

}

object EsHighLevelClient extends EsHighLevelClient {
  final val client: RestHighLevelClient = esClient
}