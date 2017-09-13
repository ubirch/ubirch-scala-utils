package com.ubirch.crypto.codec

import java.util.Base64

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.apache.commons.codec.binary.Hex
import org.scalatest.{FeatureSpec, Matchers}
import sun.misc.BASE64Encoder

class CodecUtilTest extends FeatureSpec
  with StrictLogging
  with Matchers {

  feature("happy decoding") {


    scenario("invalid") {
      CodecUtil.multiDecoder("z").isEmpty shouldBe true
    }

    scenario("valid hex") {
      val testString = "Hallo Welt!"
      val hexString = Hex.encodeHexString(testString.getBytes("UTF-8"))
      val bytes = Hex.decodeHex(hexString.toCharArray)

      val bytesOpt = CodecUtil.multiDecoder(hexString)
      bytesOpt.isDefined shouldBe true

      bytesOpt.get shouldBe bytes
    }

    scenario("valid base64") {
      val testString = "Hallo Welt!"
      val b64String = Base64.getEncoder.encodeToString(testString.getBytes("UTF-8"))

      val bytesOpt = CodecUtil.multiDecoder(b64String)

      bytesOpt.isDefined shouldBe true
      bytesOpt.get shouldBe testString.getBytes
      testString shouldBe new String(bytesOpt.get, "UTF-8")
    }

  }

}
