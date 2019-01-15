package com.ubirch.util.mongo.connection

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.deepCheck.model.DeepCheckResponse
import com.ubirch.util.mongo.config.MongoConfigKeys
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, document}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

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

  //TODO Validate with new refactoring
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

  //TODO Validate with new refactoring
  def checkConnection(): Boolean = true


//  {
//    try {
//      val dbc = Await.result(db, 2 seconds)
//      if (dbc.connection.active) {
//        val cols = Await.result(dbc.collectionNames, 2 seconds)
//        if (cols.size > 0)
//          true
//        else
//          false
//      }
//      else
//        false
//    }
//    catch {
//      case t: Throwable =>
//        dbconn = null
//        logger.error("no db connection", t)
//        false
//    }
//  }

}
