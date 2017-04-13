package com.ubirch.util.elasticsearch.client.binary.config

/**
  * author: cvandrei
  * since: 2016-12-13
  */
object ESConfigKeys {

  private val prefix = "esBinaryClient"

  /*
   * connection
   *****************************************************************************/

  private val connectionPrefix = s"$prefix.connection"

  val HOSTS = s"$connectionPrefix.hosts"

  val X_PACK_ENABLED = s"$connectionPrefix.xpackEnabled"

  val SETTINGS = s"$connectionPrefix.settings"

  /*
   * bulk
   *****************************************************************************/

  private val bulkPrefix = s"$prefix.bulk"

  val BULK_ACTIONS: String = s"$bulkPrefix.bulkActions"

  val BULK_SIZE: String = s"$bulkPrefix.bulkSize"

  val FLUSH_INTERVAL: String = s"$bulkPrefix.flushInterval"

  val CONCURRENT_REQUESTS: String = s"$bulkPrefix.concurrentRequests"

}
