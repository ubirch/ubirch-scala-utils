package com.ubirch.util.mongo.example

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.mongo.connection.MongoUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-04-03
  */
object MongoUtilDebug extends App
  with StrictLogging {

  val mongo = new MongoUtil()

  mongo.db() map { db =>

    logger.info(s"connected to database: ${db.name}")
    logger.info("listing collection names")
    db.collectionNames map println

  }

  Thread.sleep(10000)
  mongo.close()
  logger.info("closed Mongo connection")

}
