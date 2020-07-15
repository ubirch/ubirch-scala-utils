package com.ubirch.util.elasticsearch.config

import java.util.Map.Entry

import com.typesafe.config.{ConfigObject, ConfigValue}
import com.ubirch.util.config.ConfigBase

import scala.collection.JavaConverters._

object EsHighLevelConfig extends ConfigBase {


  /*
   * connection
   *****************************************************************************/

  val host: String = config.getString(EsHighLevelConfigKeys.HOST)
  val port: Int = config.getInt(EsHighLevelConfigKeys.PORT)
  val scheme: String = config.getString(EsHighLevelConfigKeys.SCHEME)


  def xPackEnabled: Boolean = {
    if (config.hasPath(EsHighLevelConfigKeys.X_PACK_ENABLED)) {
      config.getBoolean(EsHighLevelConfigKeys.X_PACK_ENABLED)
    } else {
      false
    }
  }

  def settings: Map[String, String] = {

    // code found at: http://deploymentzone.com/2013/07/25/typesafe-config-and-maps-in-scala/
    val objects: Iterable[ConfigObject] = config.getObjectList(EsHighLevelConfigKeys.SETTINGS).asScala

    val settingsIterable = for {

      item: ConfigObject <- objects
      entry: Entry[String, ConfigValue] <- item.entrySet().asScala
      key = entry.getKey
      value = entry.getValue.unwrapped().toString

    } yield (key, value)

    settingsIterable.toMap

  }

  /*
   * bulk
   *****************************************************************************/

  /**
    * @return upper limit of number of changes triggering a database flush
    */
  def bulkActions: Int = config.getInt(EsHighLevelConfigKeys.BULK_ACTIONS)

  /**
    * @return maximum total size of changed documents triggering a database flush
    */
  def bulkSize: Long = config.getLong(EsHighLevelConfigKeys.BULK_SIZE)

  /**
    * @return maximum number of seconds after which a database flush is triggered
    */
  def flushInterval: Long = config.getLong(EsHighLevelConfigKeys.FLUSH_INTERVAL)

  /**
    * @return maximum number of concurrent requests (in terms of a connection pool)
    */
  def concurrentRequests: Int = config.getInt(EsHighLevelConfigKeys.CONCURRENT_REQUESTS)
}