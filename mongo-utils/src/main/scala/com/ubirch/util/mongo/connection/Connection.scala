package com.ubirch.util.mongo.connection

import com.typesafe.scalalogging.slf4j.LazyLogging
import com.ubirch.util.mongo.config.{MongoConfig, MongoConfigKeys}
import com.ubirch.util.mongo.connection.Exceptions._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, FailoverStrategy, MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._
import scala.language.postfixOps

trait ConnectionBase {

  def driver: MongoDriver

  def hostUris: String

  def parsedUri: Try[MongoConnection.ParsedURI]

  def conn: Try[MongoConnection]

  def close(): Unit = driver.close()

  def closeLogical(): Unit = { conn.map(_.askClose()(2  seconds)) }

  def connIsActive: Boolean = conn.map(_.active).getOrElse(false)


}

class Connection private(configPrefix: String) extends ConnectionBase {

  val driver: MongoDriver = MongoDriver()
  val hostUris: String = MongoConfig.hosts(configPrefix)
  val parsedUri: Try[MongoConnection.ParsedURI] = MongoConnection.parseURI(hostUris)
  val conn: Try[MongoConnection] = parsedUri.map(driver.connection)

}

object Connection extends LazyLogging {

  private var connection: Option[Connection] = None

  def get(configPrefix: String = MongoConfigKeys.PREFIX): Connection = synchronized {

    connection.orElse {

      Try(new Connection(configPrefix)) match {
        case Success(conn) =>

          connection = Some(conn)
          connection

        case Failure(e) =>
          val errorMessage = "(1) Something went wrong when getting Connection: " + e.getMessage
          logger.error(errorMessage)
          throw GettingConnectionException(errorMessage)
      }

    }.getOrElse {
      val errorMessage = "(2) Something went wrong when getting Connection."
      logger.error(errorMessage)
      throw GettingConnectionException(errorMessage)
    }

  }


}

trait DBBase {

  val connection: Connection
  val failoverStrategy: FailoverStrategy

  def db: Future[DefaultDB]

  def collection(name: String): Future[BSONCollection]

  def getNameFromURI: Try[String] = {
    connection.parsedUri.map { x =>
      x.db.
        filter(_.nonEmpty)
        .getOrElse(throw NoDBNameFoundException("No DB Found in URI."))
    }

  }

}

class DB(val connection: Connection, val failoverStrategy: FailoverStrategy)
  extends DBBase
    with LazyLogging {

  import connection._

  def futureConnection: Future[MongoConnection] = Future.fromTry(conn)

  def db(name: String): Future[DefaultDB] = {
    val _db = for {
      conn <- futureConnection
      database <- conn.database(name, failoverStrategy)
    } yield {
      database
    }

    _db.recover {
      case e: Exception =>
        val errorMessage = s"Something went wrong when getting Database Connection (db($name)) {}"
        logger.error(errorMessage, e.getMessage)
        throw DatabaseConnectionException(e.getMessage)
    }
  }

  def db: Future[DefaultDB] = {

    Future.fromTry(getNameFromURI)
      .flatMap(db)
      .recover {
        case e: Exception =>
          val errorMessage = "Something went wrong when getting Database Connection (db) {}"
          logger.error(errorMessage, e.getMessage)
          throw DatabaseConnectionException(e.getMessage)
      }

  }


  def collection(name: String): Future[BSONCollection] = {

    db.map { db =>
      db.collection[BSONCollection](name)
    }.recover {
      case e: Exception =>
        val errorMessage = "Something went wrong when running Collection. Got this: {}"
        logger.error(errorMessage, e.getMessage)
        throw CollectionException(e.getMessage)
    }


  }

  def this(connection: Connection) = this(connection, FailoverStrategy.remote)

}