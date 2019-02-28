package com.ubirch.crypto.crypto

import java.util.Base64

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.crypto.encrypt.AES
import org.apache.commons.codec.binary.Hex
import org.scalatest.{FeatureSpec, Matchers}

class AesUtilsSpec extends FeatureSpec
  with StrictLogging
  with Matchers {

  feature("basic tests") {

    scenario("encrypt bytes to bytes") {
      val data = "Hallo Welt öäüÖÄÜß".getBytes("UTF-8")

      val secret = "ABCDE-FGHIJ-KLMNO-PQRST-UVWXY"

      val encData = AES.encrypt(data, secret)

      val decData1 = AES.decrypt(encData, secret)
      data shouldBe decData1

      val decData2 = AES.decryptB64(Base64.getEncoder.encodeToString(encData), secret)
      data shouldBe decData2
    }

    scenario("encrypt String to bytes") {
      val data = "Hallo Welt öäüÖÄÜß"

      val secret = "ABCDE-FGHIJ-KLMNO-PQRST-UVWXY"

      val encData = AES.encryptStr(data, secret)

      val decData = AES.decrypt(encData, secret)

      data.getBytes("UTF-8") shouldBe decData
    }

    scenario("encrypt Base64 to bytes") {
      val data = Base64.getEncoder.encodeToString("Hallo Welt öäüÖÄÜß".getBytes("UTF-8"))

      val secret = "ABCDE-FGHIJ-KLMNO-PQRST-UVWXY"

      val encData = AES.encryptB64(data, secret)

      val decData = Base64.getEncoder.encodeToString(AES.decrypt(encData, secret))

      data shouldBe decData
    }

    scenario("encrypt Hex to bytes") {
      val data = "Hallo Welt öäüÖÄÜß"
      val dataBytes = data.getBytes("UTF-8")
      val dataHex = Hex.encodeHexString(dataBytes)

      val secret = "ABCDE-FGHIJ-KLMNO-PQRST-UVWXY"

      val encData = AES.encryptHex(dataHex, secret)

      val encDataHex = AES.encryptStr2Hex(data, secret)

      val decData = AES.decrypt(encData, secret)

      val decDataStr = AES.decrypHex2Str(encDataHex, secret)

      dataBytes shouldBe decData
      data shouldBe decDataStr

    }

    scenario("encrypt String to Base64") {
      val data = "Hallo Welt öäüÖÄÜß"
      val dataBytes = "Hallo Welt öäüÖÄÜß".getBytes("UTF-8")

      val secret = "ABCDE-FGHIJ-KLMNO-PQRST-UVWXY"

      val encData = AES.encryptStr2B64(data, secret)

      val decData = AES.decryptB64(encData, secret)

      val decDataStr = AES.decrypB642Str(encData, secret)

      dataBytes shouldBe decData

      data shouldBe decDataStr
    }

    scenario("encrypt String to Hex") {
      val data = "Hallo Welt öäüÖÄÜß"
      val dataBytes = "Hallo Welt öäüÖÄÜß".getBytes("UTF-8")

      val secret = "ABCDE-FGHIJ-KLMNO-PQRST-UVWXY"

      val encData = AES.encryptStr2Hex(data, secret)

      val decData = AES.decryptHex(encData, secret)

      dataBytes shouldBe decData
    }

  }

}
