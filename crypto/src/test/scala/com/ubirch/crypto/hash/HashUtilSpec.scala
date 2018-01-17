package com.ubirch.crypto.hash

import java.util.UUID

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

  feature("HashUtil.sha512HexString") {

    scenario("ensure that SHA-512 is configured") {
      HashUtil.sha512HexString("ubirchChainService") should be("167154b1038c90a065b0ef738341a7dcee7d69928a633a8af1e501f758b541a5a2eb08d87eef1037ebf9f408a22e6bfdbecdf495fb114b3b320bb6056e149f46")
    }

  }

  feature("HashUtil.pbkdf2Base64") {

    scenario("ensure PBKDF2 works") {

      // prepare
      val data = "ubirchChainService"
      val salt = UUID.randomUUID().toString
      val iterations = 10000

      // test
      val base64_1 = HashUtil.pbkdf2Base64(data, salt, iterations)
      val base64_2 = HashUtil.pbkdf2Base64(data, salt, iterations)

      // verify
      base64_1 should be(base64_2)

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

  ignore("hashing performance (SHA256)") {

    scenario("1,000 hashes") {
      measureHashingPerformanceSHA256(1000)
    }

    scenario("10,000 hashes") {
      measureHashingPerformanceSHA256(10000)
    }

    scenario("25,000 hashes") {
      measureHashingPerformanceSHA256(25000)
    }

    scenario("50,000 hashes") {
      measureHashingPerformanceSHA256(50000)
    }

    scenario("75,000 hashes") {
      measureHashingPerformanceSHA256(75000)
    }

    scenario("100,000 hashes") {
      measureHashingPerformanceSHA256(100000)
    }

    scenario("500,000 hashes") {
      measureHashingPerformanceSHA256(500000)
    }

  }

  ignore("hashing performance (PBKDF2)") {

    scenario("10,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 10000)
    }

    scenario(s"50,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 50000)
    }

    scenario(s"100,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 100000)
    }

    scenario(s"250,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 250000)
    }

    scenario(s"500,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 500000)
    }

    scenario(s"1,000,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 1000000)
    }

    scenario(s"2,000,000 iterations") {
      measureHashingPerformancePBKDF2(UUID.randomUUID().toString, 2000000)
    }

  }

  def measureHashingPerformanceSHA256(count: Int): Unit = {

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

  def measureHashingPerformancePBKDF2(salt: String, iterations: Int): Unit = {

    val count = 10
    println(s"starting to generate random list with $count elements (iterations=$iterations)")
    val randomSeq: Seq[String] = for (i <- 1 to count) yield Random.nextLong.toString
    println(s"finished generating random list with $count elements")
    println(s"starting to calculate $count hashes")
    val before = DateTime.now
    randomSeq.map(HashUtil.pbkdf2HexString(_, salt, iterations))
    val after = DateTime.now

    val duration = after.getMillis - before.getMillis
    val average = duration / count
    println(s"hashing $count hashes took $duration ms (average = $average ms)")

  }

}
