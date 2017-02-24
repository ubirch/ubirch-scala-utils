package com.ubirch.util.elasticsearch.client.binary.storage

import com.ubirch.util.elasticsearch.client.binary.storage.base.{ESClient, ESStorageBase}

import org.elasticsearch.client.transport.TransportClient

/**
  * author: cvandrei
  * since: 2017-02-24
  */
trait ESSimpleStorage extends ESStorageBase {

  override protected val esClient: TransportClient = ESClient.esClient

}
