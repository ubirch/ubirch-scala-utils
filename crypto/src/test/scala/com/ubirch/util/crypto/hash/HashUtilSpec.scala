package com.ubirch.util.crypto.hash

import org.joda.time.DateTime
import org.scalatest.{FeatureSpec, Matchers}

import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-08-01
  */
class HashUtilSpec extends FeatureSpec
  with Matchers {

  feature("HashUtil.sha256HexString") {

    scenario("ensure that SHA-256 is configured") {
      HashUtil.sha256HexString("ubirchChainService") should be("8faa746945edc3313ae873802616fe17e79f13623aac81e38cbde9e700c33b92")
    }

  }

  feature("HashUtil.hashToHex && HashUtil.hashToBytes") {

    scenario("calculate hexString, convert to byte array and convert byte array back to hexString") {

      val input = "ubirchChainService"
      val expected = HashUtil.sha256HexString(input)
      println(s"expected: $expected")

      // test part 1: hashToBytes
      val expectedBytes = HashUtil.hashToBytes(expected)
      expectedBytes.length should be(32)

      // test part 2: hashToHex
      val actual = HashUtil.hashToHex(expectedBytes)
      println(s"actual: $actual")
      actual should be(expected)

    }

  }

  feature("HashUtil.hashToHex && HashUtil.sha256ByteArray") {

    scenario("calculate hash as byte array and convert back to hexString") {

      val input = "ubirchChainService"
      val expected = HashUtil.sha256HexString(input)
      println(s"expected: $expected")

      // test part 1: sha256ByteArray
      val expectedBytes = HashUtil.sha256ByteArray(input)
      expectedBytes.length should be(32)

      // test part 2: hashToHex
      val actual = HashUtil.hashToHex(expectedBytes)
      println(s"actual: $actual")
      actual should be(expected)

    }

  }

  ignore("hashing performance") {

    scenario("1,000 hashes") {
      measureHashingPerformance(1000)
    }

    scenario("10,000 hashes") {
      measureHashingPerformance(10000)
    }

    scenario("25,000 hashes") {
      measureHashingPerformance(25000)
    }

    scenario("50,000 hashes") {
      measureHashingPerformance(50000)
    }

    scenario("75,000 hashes") {
      measureHashingPerformance(75000)
    }

    scenario("100,000 hashes") {
      measureHashingPerformance(100000)
    }

    scenario("500,000 hashes") {
      measureHashingPerformance(500000)
    }

  }

  def measureHashingPerformance(count: Int): Unit = {

    println(s"starting to generate random list with $count elements")
    val randomSeq: Seq[String] = for (i <- 1 to count) yield Random.nextLong.toString
    println(s"finished generating random list with $count elements")
    println(s"starting to calculate $count hashes")
    val before = DateTime.now
    randomSeq.map(HashUtil.sha256HexString)
    val after = DateTime.now

    val duration = after.getMillis - before.getMillis
    println(s"hashing $count hashes took $duration ms")

  }

}
