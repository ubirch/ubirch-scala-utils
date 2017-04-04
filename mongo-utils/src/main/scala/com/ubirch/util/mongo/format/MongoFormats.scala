package com.ubirch.util.mongo.format

import java.util.UUID

import org.joda.time.DateTime

import reactivemongo.bson.{BSONDateTime, BSONHandler, BSONString}

/**
  * author: cvandrei
  * since: 2017-04-04
  */
trait MongoFormats {

  implicit object UUIDWriter extends BSONHandler[BSONString, UUID] {
    def read(uuid: BSONString): UUID = UUID.fromString(uuid.toString)
    def write(id: UUID): BSONString = BSONString(id.toString)
  }

  implicit object BSONDateTimeHandler extends BSONHandler[BSONDateTime, DateTime] {
    def read(jodaTime: BSONDateTime): DateTime = new DateTime(jodaTime.value)
    def write(jodaTime: DateTime) = BSONDateTime(jodaTime.getMillis)
  }

}
