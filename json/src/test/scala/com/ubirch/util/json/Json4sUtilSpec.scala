package com.ubirch.util.json

import java.io.ByteArrayInputStream

import org.joda.time._
import org.json4s.Formats
import org.scalatest.{FeatureSpec, Matchers}

/**
  * Created by derMicha on 01/08/16.
  */
class Json4sUtilSpec extends FeatureSpec
  with Matchers {

  case class TestModel(name: String, age: Int, created: DateTime)

  case class Simple(a: String)

  implicit val formats: Formats = JsonFormats.default

  feature("any2String()") {

    scenario("transformation successful") {

      // prepare
      val testModel = Simple("heinz")

      // test
      val stringOpt = Json4sUtil.any2String(testModel)

      // verify
      stringOpt.isDefined shouldBe true

      val s = stringOpt.get
      val expected = s"""{"a":"${testModel.a}"}"""
      s should be(expected)

    }

  }

  feature("any2jvalue()") {

    scenario("test any2value") {

      // prepare
      val name = "heinz"
      val age = Int.MaxValue
      val created = DateTime.now(DateTimeZone.UTC)

      val testModel = TestModel(name = name, age = age, created = created)

      // test
      val jvalOpt = Json4sUtil.any2jvalue(testModel)

      // verify
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

  feature("inputstream2jvalue") {

    scenario("test inputstream") {

      // prepare
      val doc = Simple(a = "B")
      val jval = Json4sUtil.any2jvalue(doc).get
      val str = Json4sUtil.jvalue2String(jval)
      val is = new ByteArrayInputStream(str.getBytes())

      // test
      val jval2 = Json4sUtil.inputstream2jvalue(is).get

      // verify
      val doc2 = jval2.extractOpt[Simple]
      doc2.isDefined shouldBe true
      doc2.get.a shouldBe doc.a
      doc2.get.a shouldBe "B"

    }

  }

  feature("any2any()") {

    scenario("ModelA to ModelA") {

      // prepare
      val in = ModelA(id = 1L, someField = "foo")

      // test
      val result = Json4sUtil.any2any[ModelA](in)

      // verify
      result should be(in)

    }

    scenario("ModelA to ModelB (covers: optional fields && B has a field that A does not have)") {

      // prepare
      val in = ModelA(id = 1L, someField = "foo")

      // test
      val result = Json4sUtil.any2any[ModelB](in)

      // verify
      result.id.get shouldBe in.id
      result.someField shouldBe in.someField
      result.anotherField should be('isEmpty)

    }

    scenario("ModelB to ModelA (covers: optional fields && B has a field that A does not have)") {

      // prepare
      val in = ModelB(
        id = Some(1L),
        someField = "some",
        anotherField = Some("another")
      )

      // test
      val result = Json4sUtil.any2any[ModelA](in)

      // verify
      result.id shouldBe in.id.get
      result.someField shouldBe in.someField

    }

  }

}

case class ModelA(id: Long,
                  someField: String
                 )

case class ModelB(id: Option[Long],
                  someField: String,
                  anotherField: Option[String]
                 )
