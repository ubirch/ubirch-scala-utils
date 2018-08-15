package com.ubirch.util.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

/**
  * A base config class checking the environment for the config file's name and if not found reads the default file.
  *
  * author: cvandrei
  * since: 2016-08-23
  */
trait ConfigBase {

  private val envKey = "ubirch.env"

  protected val config: Config = {

    getEnvKey match {

      case Some(confFile) => ConfigFactory.load(confFile)

      case None =>
        //        dynamicConf // TODO activate once the method has been fully implemented
        ConfigFactory.load()

    }

  }

  protected def environmentId(): String = config.getString("ubirch.envid")

  protected def stringWithDefault(key: String, default: String): String = {

    if (config.hasPath(key)) {
      config.getString(key)
    } else {
      default
    }

  }

  protected def intWithDefault(key: String, default: Int): Int = {

    if (config.hasPath(key)) {
      config.getInt(key)
    } else {
      default
    }

  }

  protected def booleanWithDefault(key: String, default: Boolean): Boolean = {

    if (config.hasPath(key)) {
      config.getBoolean(key)
    } else {
      default
    }

  }

  private def getEnvKey: Option[String] = {

    System.getProperties.asScala.get(envKey) match {

      case Some(value) => Some(value)

      case None =>

        System.getenv.asScala.get(envKey) match {

          case Some(value) => Some(value)
          case None => None

        }

    }

  }

  private def dynamicConf: Config = {

    // TODO load config as string from some db maybe or through ConfigFactory.parseFile() from a File
    val applicationConf = ConfigFactory.load()
    ConfigFactory.parseString(
      """{
        |  bitcoin {
        |    wallet {
        |      directory = "/Users/cvandrei/"
        |    }
        |  }
        |}
      """.stripMargin
    ).withFallback(applicationConf)

  }

}
