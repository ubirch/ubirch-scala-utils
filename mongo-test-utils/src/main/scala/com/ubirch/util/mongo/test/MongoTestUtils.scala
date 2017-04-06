package com.ubirch.util.mongo.test

import com.ubirch.util.mongo.connection.MongoUtil
import com.ubirch.util.mongo.format.MongoFormats

import reactivemongo.api.commands.bson.BSONCountCommand.Count
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2017-04-06
  */
class MongoTestUtils(implicit mongo: MongoUtil) {

  def countAll(collectionName: String): Future[Int] = {

    val command = Count(BSONDocument())
    for {
      collection <- mongo.collection(collectionName)
      cmdResult <- collection.runCommand(command)
    } yield {
      cmdResult.value
    }

  }

}
