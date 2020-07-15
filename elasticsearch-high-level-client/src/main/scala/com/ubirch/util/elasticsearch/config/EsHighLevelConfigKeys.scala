package com.ubirch.util.elasticsearch.config

object EsHighLevelConfigKeys {

  private val prefix = "esHighLevelClient"

  /*
   * connection
   *****************************************************************************/

  private val connectionPrefix = s"$prefix.connection"

  val HOST = s"$connectionPrefix.host"
  val PORT = s"$connectionPrefix.port"
  val SCHEME = s"$connectionPrefix.scheme"

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
