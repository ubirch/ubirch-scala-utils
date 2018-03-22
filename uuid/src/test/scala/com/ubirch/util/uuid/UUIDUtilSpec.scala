package com.ubirch.util.uuid

import org.apache.commons.codec.binary.Hex
import org.scalatest.{FeatureSpec, Matchers}

class UUIDUtilSpec extends FeatureSpec
  with Matchers {

  feature("uuid util tests") {

    val testUuid = "50e505c1-fbc6-580a-4d31-63f7c2a69e20"

    val rawTestUuid = Hex.decodeHex("50e505c1fbc6580a4d3163f7c2a69e20")

    scenario("parse from String") {
      val u = UUIDUtil.fromString(testUuid)

      val uStr = u.toString

      uStr shouldBe testUuid
    }

    scenario("parse from Array") {
      val u1 = UUIDUtil.fromByteArray(rawTestUuid)

      val uStr = u1.toString

      uStr shouldBe testUuid
    }

    scenario("parse from UUID to Array") {
      val u1 = UUIDUtil.toByteArray(UUIDUtil.fromString(testUuid))

      u1 shouldBe rawTestUuid
    }

    scenario("parse from Array/String") {
      val u1 = UUIDUtil.fromByteArray(rawTestUuid)

      val u2 = UUIDUtil.fromString(testUuid)

      u1 shouldBe u2
    }

    scenario("parse created Uuid") {

      val u = UUIDUtil.uuid

      var rawU = UUIDUtil.toByteArray(u)

      val u2 = UUIDUtil.fromByteArray(rawU)

      u shouldBe u2

    }


  }


}
