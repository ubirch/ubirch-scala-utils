package com.ubirch.util.mongo.test

import com.ubirch.util.mongo.connection.MongoUtil
import reactivemongo.api.ReadConcern
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-04-06
  */
class MongoTestUtils(implicit mongo: MongoUtil) {

  def countAll(collectionName: String): Future[Int] = {

    for {
      collection <- mongo.collection(collectionName)
      count <- collection.count(
        Some(BSONDocument()),
        None,
        0,
        None,
        ReadConcern.Available
      )
    } yield {
      count.toInt
    }

  }

}
