package com.ubirch.util.elasticsearch.client.binary.storage

import com.ubirch.util.elasticsearch.client.binary.storage.base.{ESClient, ESBulkStorageBase}

import org.elasticsearch.client.transport.TransportClient

/**
  * author: cvandrei
  * since: 2017-02-24
  */
trait ESBulkStorage extends ESBulkStorageBase {

  override protected val esClient: TransportClient = ESClient.esClient

}
