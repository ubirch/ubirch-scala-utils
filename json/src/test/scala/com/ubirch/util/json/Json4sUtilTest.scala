package com.ubirch.util.json

import java.io.ByteArrayInputStream

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

  case class Simple(a: String)

  implicit val formats = JsonFormats.default

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

    scenario("test inputstream") {

      val doc = Simple(a = "B")
      val jval = Json4sUtil.any2jvalue(doc).get
      val str = Json4sUtil.jvalue2String(jval)
      val is = new ByteArrayInputStream(str.getBytes())

      val jval2 = Json4sUtil.inputstream2jvalue(is).get
      val doc2 = jval2.extractOpt[Simple]
      doc2.isDefined shouldBe true
      doc2.get.a shouldBe doc.a
      doc2.get.a shouldBe "B"
    }
  }
}
