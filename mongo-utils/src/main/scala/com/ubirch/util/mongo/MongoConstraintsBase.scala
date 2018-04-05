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

  val constraintsToCreate: Map[String, Set[Index]]

  val constraintsToDrop: Map[String, Set[String]]

  val collections: Set[String]

  def createMongoConstraints()(implicit mongo: MongoUtil, ec: ExecutionContext): Unit = {

    for (collectionName <- constraintsToCreate.keys) {

      mongo.collection(collectionName) map { collection =>

        for (constraint <- constraintsToCreate.getOrElse(collectionName, Set.empty)) {
          collection.indexesManager.create(constraint) map { result =>
            logger.info(s"createMongoConstraints() - collection=$collectionName, constraint=$constraint, result=$result")
          }
        }

      }

    }

    Thread.sleep(500)

  }

  def dropMongoConstraints()(implicit mongo: MongoUtil, ec: ExecutionContext): Unit = {

    for (collectionName <- constraintsToDrop.keys) {

      mongo.collection(collectionName) map { collection =>

        for (constraintName <- constraintsToDrop.getOrElse(collectionName, Set.empty)) {
          collection.indexesManager.drop(constraintName) map { result =>
            logger.info(s"dropMongoConstraints() - collection=$collectionName, constraintName=$constraintName, result=$result")
          }
        }

      }

    }

    Thread.sleep(500)

  }

  def prepareMongoConstraints()(implicit mongo: MongoUtil, ec: ExecutionContext): Unit = {

    createMongoConstraints()
    dropMongoConstraints()

  }

}
