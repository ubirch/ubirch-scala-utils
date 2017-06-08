package com.ubirch.util.mongo.connection

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.model.DeepCheckResponse
import com.ubirch.util.mongo.config.{MongoConfig, MongoConfigKeys}

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, document}

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
  val db: Future[DefaultDB] = {

    val hostUris = MongoConfig.hosts(configPrefix)

    for {
      uri <- Future.fromTry(MongoConnection.parseURI(hostUris))
      con = driver.connection(uri)
      dn <- Future(uri.db.get)
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

    collection(collectionName) flatMap {
      _.find(document()).one[T] map (_ => DeepCheckResponse())
    } recover {

      case t: Throwable =>
        DeepCheckResponse(
          status = "NOK",
          messages = Seq(t.getMessage)
        )

    }

  }

}
