package com.ubirch.util.elasticsearch.client.binary.config

import java.util.Map.Entry

import com.typesafe.config.{ConfigObject, ConfigValue}

import com.ubirch.util.config.ConfigBase

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

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

  def hosts: Set[HostUri] = {

    val hostUriList = config.getStringList(ESConfigKeys.HOSTS).toList
    val hostUriSeq = hostUriList map { hostUri =>
      val split = hostUri.split(":")
      HostUri(split(0), split(1).toInt)
    }

    hostUriSeq.toSet

  }

  def xPackEnabled: Boolean = {
    if (config.hasPath(ESConfigKeys.X_PACK_ENABLED)) {
      config.getBoolean(ESConfigKeys.X_PACK_ENABLED)
    } else {
      false
    }
  }

  def settings: Map[String, String] = {

    // code found at: http://deploymentzone.com/2013/07/25/typesafe-config-and-maps-in-scala/
    val objects : Iterable[ConfigObject] = config.getObjectList(ESConfigKeys.SETTINGS).asScala

    val settingsIterable = for {

      item : ConfigObject <- objects
      entry : Entry[String, ConfigValue] <- item.entrySet().asScala
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

case class HostUri(host: String, port: Int)

case class ESSetting(key: String, value: String)