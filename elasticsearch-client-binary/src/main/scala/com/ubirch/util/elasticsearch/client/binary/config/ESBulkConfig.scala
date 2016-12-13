package com.ubirch.util.elasticsearch.client.binary.config

import com.ubirch.util.config.ConfigBase

/**
  * Bulk storage saves changes with a delay (whenever any of several criteria is fulfilled) and comes with other
  * configurable options as well.
  *
  * author: cvandrei
  * since: 2016-12-13
  */
object ESBulkConfig extends ConfigBase {

  /**
    * @return upper limit of number of changes triggering a database flush
    */
  def bulkActions: Int = config.getInt(ESBulkConfigKeys.BULK_ACTIONS)

  /**
    * @return maximum total size of changed documents triggering a database flush
    */
  def bulkSize: Long = config.getLong(ESBulkConfigKeys.BULK_SIZE)

  /**
    * @return maximum number of seconds after which a database flush is triggered
    */
  def flushInterval: Long = config.getLong(ESBulkConfigKeys.FLUSH_INTERVAL)

  /**
    * @return maximum number of concurrent requests (in terms of a connection pool)
    */
  def concurrentRequests: Int = config.getInt(ESBulkConfigKeys.CONCURRENT_REQUESTS)

}
