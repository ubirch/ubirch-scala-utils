package com.ubirch.util.mongo.connection

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.deepCheck.model.DeepCheckResponse
import com.ubirch.util.mongo.config.MongoConfigKeys
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, TimeoutException}
import scala.language.postfixOps
import scala.util.Try

/**
  * author: cvandrei
  * since: 2017-03-31
  */
class MongoUtil(configPrefix: String = MongoConfigKeys.PREFIX) extends StrictLogging {

  def conn: Connection = Connection.get(configPrefix)

  def dbStub: DB = new DB(conn)


  /**
    * Opens a database connection. Don't forget to call #close to free up all resources afterwards.
    *
    * MongoConnectionDebug demonstrates how to use it and can server as a starting point for debug tests.
    *
    * @return database connection
    */
  def db: Future[DefaultDB] = dbStub.db

  /**
    * Connects us to a collection.
    *
    * @param name collection names
    * @return collection connection
    */
  def collection(name: String): Future[BSONCollection] = dbStub.collection(name)

  /**
    * Close the connection and other related connection related resources.
    */
  def close(): Unit = conn.close()

  /**
    * Check database connectivity by querying the given collection
    *
    * @param collectionName name of collection to query
    * @tparam T type of resulting objects
    * @return deep check response with _status:OK_ if ok; otherwise with _status:NOK_
    */

  def connectivityCheck[T <: Any](collectionName: String)(implicit writer: BSONDocumentWriter[T], reader: BSONDocumentReader[T]): Future[DeepCheckResponse] = {

    if (checkConnection()) {

      logger.debug("DB Connection exists.")

      collection(collectionName)
        .flatMap {
          _.find(document(), None)
            .one[T]
            .map(_ => DeepCheckResponse())
        }.recover {

        case e: Exception =>
          DeepCheckResponse(
            status = false,
            messages = Seq(e.getMessage)
          )

      }
    } else {

      logger.debug("DB Connection does not exist.")

      Future.successful(
        DeepCheckResponse(
          status = false,
          messages = Seq("No Mongo Connection")
        )
      )

    }

  }

  def checkConnection(atMost: FiniteDuration = 2 seconds): Boolean = {

    val futureIsConnectionActive: Future[Boolean] = db.map { db =>
      db.connection.active
    }

    val futureHasCollectionNames: Future[Boolean] = db.flatMap { db =>
      db.collectionNames.map(_.nonEmpty)
    }

    val futureChecks = {
      val checks = for {
        isConnectionActive <- futureIsConnectionActive if isConnectionActive
        hasCollectionNames <- futureHasCollectionNames if hasCollectionNames
      } yield {
        true
      }

      checks.recover {
        case e: Exception =>
          logger.error("No DB Connection: {} ", e.getMessage)
          false
      }

    }

    val checks = Try(Await.result(futureChecks, atMost)).recover {
      case e: TimeoutException =>

        logger.error("(1) It is taking more than {} to retrieve checks. Got this error {} ", atMost.toString(), e.getMessage)

        false

      case e =>

        logger.error("Something went wrong when running checks. Got this: {} ", e.getMessage)

        false

    }.getOrElse {
      false
    }

    checks

  }

}
