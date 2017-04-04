package com.ubirch.util.mongo.connection

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.mongo.config.{MongoConfig, MongoConfigKeys}

import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-03-31
  */
class MongoUtil(configPrefix: String = MongoConfigKeys.PREFIX) extends StrictLogging {

  private val driver = MongoDriver()

  /**
    * Opens a database connection. Don't forget to call #close to free up all resources afterwards.
    *
    * MongoConnectionDebug demonstrates how to use it and can server as a starting point for debug tests.
    *
    * @return database connection
    */
  def db(): Future[DefaultDB] = {

    val hostUris = MongoConfig.hosts(configPrefix)

    for {
      uri <- Future.fromTry(MongoConnection.parseURI(hostUris))
      con = driver.connection(uri)
      dn <- Future(uri.db.get)
      db <- con.database(dn)
    } yield db

  }

  /**
    * Close the connection and other related connection related resources.
    */
  def close(): Unit = driver.close()

}
