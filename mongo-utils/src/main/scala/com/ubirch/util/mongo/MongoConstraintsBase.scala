package com.ubirch.util.mongo

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.mongo.connection.MongoUtil

import reactivemongo.api.indexes.Index

import scala.concurrent.ExecutionContext

/**
  * author: cvandrei
  * since: 2017-07-12
  */
trait MongoConstraintsBase extends StrictLogging {

  val constraints: Map[String, Set[Index]]

  val collections: Set[String]

  def createMongoConstraints()(implicit mongo: MongoUtil, ec: ExecutionContext): Unit = {

    for (collectionName <- constraints.keys) {

      mongo.collection(collectionName) map { collection =>

        for (constraint <- constraints.getOrElse(collectionName, Set.empty)) {
          collection.indexesManager.create(constraint) map { result =>
            logger.info(s"createMongoConstraints() - collection=$collectionName, constraint=$constraint, result=$result")
          }
        }

      }

    }

    Thread.sleep(500)

  }

}
