package com.ubirch.util.json

import org.joda.time._
import org.json4s.DefaultFormats
import org.json4s.ext.JodaTimeSerializers
import org.scalatest.{FeatureSpec, Matchers}

/**
  * Created by derMicha on 01/08/16.
  */
class Json4sUtilTest extends FeatureSpec
  with Matchers {

  case class TestWumms(name: String, age: Int, created: DateTime)

  implicit val formats = DefaultFormats.lossless ++ JodaTimeSerializers.all

  feature("JsonUtil") {

    scenario("any2value") {
      val name = "heinz"
      val age = Int.MaxValue
      val created = DateTime.now(DateTimeZone.UTC)

      val testWumms = TestWumms(name = name, age = age, created = created)

      val jvalOpt = Json4sUtil.any2jvalue(testWumms)

      jvalOpt.isDefined shouldBe true

      val jval = jvalOpt.get

      val nameOpt = (jval \ "name").extractOpt[String]
      nameOpt.isDefined shouldBe true
      val nameVal = nameOpt.get
      nameVal shouldBe name

      val ageOpt = (jval \ "age").extractOpt[Int]
      ageOpt.isDefined shouldBe true
      val ageVal = ageOpt.get
      ageVal shouldBe Int.MaxValue


      val createdOpt = (jval \ "created").extractOpt[DateTime]
      createdOpt.isDefined shouldBe true
      val createdVal = createdOpt.get.withZone(DateTimeZone.UTC)

      createdVal shouldBe created
    }
  }
}
