package com.ubirch.util.elasticsearch.client.binary.config

import com.ubirch.util.config.ConfigBase

/**
  * Bulk storage saves changes with a delay (whenever any of several criteria is fulfilled) and comes with other
  * configurable options as well.
  *
  * author: cvandrei
  * since: 2016-12-13
  */
object ESConfig extends ConfigBase {

  /*
   * connection
   *****************************************************************************/

  def host: String = config.getString(ESConfigKeys.HOST)

  def port: Int = config.getInt(ESConfigKeys.PORT)

  def cluster: Option[String] = {

    if (config.hasPath(ESConfigKeys.CLUSTER)) {
      Some(config.getString(ESConfigKeys.CLUSTER))
    } else {
      None
    }

  }

  /*
   * bulk
   *****************************************************************************/

  /**
    * @return upper limit of number of changes triggering a database flush
    */
  def bulkActions: Int = config.getInt(ESConfigKeys.BULK_ACTIONS)

  /**
    * @return maximum total size of changed documents triggering a database flush
    */
  def bulkSize: Long = config.getLong(ESConfigKeys.BULK_SIZE)

  /**
    * @return maximum number of seconds after which a database flush is triggered
    */
  def flushInterval: Long = config.getLong(ESConfigKeys.FLUSH_INTERVAL)

  /**
    * @return maximum number of concurrent requests (in terms of a connection pool)
    */
  def concurrentRequests: Int = config.getInt(ESConfigKeys.CONCURRENT_REQUESTS)

}
