package com.ubirch.util.crypto.hash

import com.roundeights.hasher.Implicits._
import com.roundeights.hasher.{Digest, Hash}

import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-07-28
  */
object HashUtil {

  def sha256Digest(data: String): Digest = data.sha256

  def sha256HexString(data: String): String = sha256Digest(data).hex

  def sha256ByteArray(data: String): Array[Byte] = sha256Digest(data).bytes

  def hashAsBytes(buf: String): Array[Byte] = Hash(buf).bytes

  def hashAsHex(buf: Array[Byte]): String = Hash(buf).hex

  def randomSha256Hashes(count: Int = Random.nextInt(30000)): Seq[String] = {

    val randomSeq: Seq[String] = for (i <- 1 to count) yield Random.nextLong.toString

    randomSeq.map(HashUtil.sha256HexString)

  }

}
