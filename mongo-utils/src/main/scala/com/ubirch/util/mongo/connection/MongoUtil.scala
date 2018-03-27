package com.ubirch.util.mongo.connection

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.deepCheck.model.DeepCheckResponse
import com.ubirch.util.mongo.config.{MongoConfig, MongoConfigKeys}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import scala.util.Failure

import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-03-31
  */
class MongoUtil(configPrefix: String = MongoConfigKeys.PREFIX) extends StrictLogging {

  private val driver = MongoDriver()


  private var dbconn: Future[DefaultDB] = null

  /**
    * Opens a database connection. Don't forget to call #close to free up all resources afterwards.
    *
    * MongoConnectionDebug demonstrates how to use it and can server as a starting point for debug tests.
    *
    * @return database connection
    */
  def db: Future[DefaultDB] = {
    if (dbconn != null) {
      if (dbconn.isInstanceOf[scala.util.Failure[reactivemongo.api.DefaultDB]]) {
        dbconn = reconectDb
      }
    }
    else
      dbconn = reconectDb
    dbconn
  }

  def reconectDb: Future[DefaultDB] = {
    val hostUris = MongoConfig.hosts(configPrefix)
    for {
      uri <- Future.fromTry(MongoConnection.parseURI(hostUris))
      dn <- Future(uri.db.getOrElse("noDbName"))
      con = driver.connection(uri)
      db <- con.database(dn)
    } yield db
  }

  /**
    * Connects us to a collection.
    *
    * @param name collection names
    * @return collection connection
    */
  def collection(name: String): Future[BSONCollection] = {

    db.map { db =>
      db.collection[BSONCollection](name)
    }

  }

  /**
    * Close the connection and other related connection related resources.
    */
  def close(): Unit = driver.close()

  /**
    * Check database connectivity by querying the given collection
    *
    * @param collectionName name of collection to query
    * @tparam T type of resulting objects
    * @return deep check response with _status:OK_ if ok; otherwise with _status:NOK_
    */
  def connectivityCheck[T <: Any](collectionName: String)
                                 (implicit writer: BSONDocumentWriter[T], reader: BSONDocumentReader[T])
  : Future[DeepCheckResponse] = {

    checkConnection() match {
      case true =>
        logger.debug("db connection exists")
        collection(collectionName) flatMap {
          _.find(document()).one[T] map (_ => DeepCheckResponse())
        } recover {

          case t: Throwable =>
            DeepCheckResponse(
              status = false,
              messages = Seq(t.getMessage)
            )

        }
      case false =>
        logger.debug("db connection does not exists")
        Future(DeepCheckResponse(
          status = false,
          messages = Seq("no mongo connection")
        ))
    }

  }

  /**
    *
    * @return
    */
  def checkConnection(): Boolean = {
    try {
      val dbc = Await.result(db, 2 seconds)
      if (dbc.connection.active) {
        val cols = Await.result(dbc.collectionNames, 2 seconds)
        if (cols.size > 0)
          true
        else
          false
      }
      else
        false
    }
    catch {
      case t: Throwable =>
        dbconn = null
        logger.error("no db connection", t)
        false
    }
  }

}
