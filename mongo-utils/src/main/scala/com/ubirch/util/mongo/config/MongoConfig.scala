package com.ubirch.util.mongo.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-03-31
  */
object MongoConfig extends ConfigBase {

  private val defaultPrefix = MongoConfigKeys.PREFIX

  def hosts(configPrefix: String = defaultPrefix): String = {
    val configPath = getConfigPath(configPrefix, MongoConfigKeys.HOSTS)
    config.getString(configPath)
  }

  private def getConfigPath(pathPrefix: String, pathPostfix: String): String = {

    if (pathPrefix == "") {
      pathPostfix
    } else {
      s"$pathPrefix.$pathPostfix"
    }

  }

}
