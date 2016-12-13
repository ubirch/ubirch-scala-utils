package com.ubirch.util.elasticsearch.client.binary.config

/**
  * author: cvandrei
  * since: 2016-12-13
  */
object ESBulkConfigKeys {

  private val prefix = "esBinaryClient.bulk"

  val BULK_ACTIONS: String = s"$prefix.bulkActions"

  val BULK_SIZE: String = s"$prefix.bulkSize"

  val FLUSH_INTERVAL: String = s"$prefix.flushInterval"

  val CONCURRENT_REQUESTS: String = s"$prefix.concurrentRequests"

}
