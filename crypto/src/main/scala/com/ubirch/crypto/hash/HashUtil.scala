package com.ubirch.crypto.hash

import java.util.Base64

import com.roundeights.hasher.Implicits._
import com.roundeights.hasher.{Digest, Hash}
import org.apache.commons.codec.binary.Hex

import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-07-28
  */
object HashUtil {

  /**
    * Gives us the Digest of a hash based on which call all sorts of methods (including conversion to hexString or
    * byteArray).
    *
    * @param data data to hash
    * @return digest with hash of input data
    */
  def sha256Digest(data: String): Digest = data.sha256

  /**
    * Convenience method that gives us the hash of the input as hexString.
    *
    * @param data data to hash
    * @return hex string representation: SHA-256 hash of input data
    */
  def sha256HexString(data: String): String = sha256Digest(data).hex

  /**
    * Convenience method that gives us the hash of the input as byte array.
    *
    * @param data data to hash
    * @return byte array representation: SHA-256 hash of input data
    */
  def sha256ByteArray(data: String): Array[Byte] = sha256Digest(data).bytes

  /**
    * Convenience method that gives us the hash of the input as Base64 encoded string.
    *
    * @param data data to hash
    * @return Base64 encoded string representation: SHA-256 hash of input data
    */
  def sha256Base64(data: String): String = {
    val bytes = sha256Digest(data).bytes
    hashToBase64(bytes)
  }

  /**
    *
    * @param binData
    * @return sha256 hash as array of bytes
    */
  def sha256(binData: Array[Byte]): Array[Byte] = {
    binData.sha256.bytes
  }

  /**
    *
    * @param binData
    * @return sha256 hash as hex encoded string
    */
  def sha256Hex(binData: Array[Byte]): String = {
    Hex.encodeHexString(sha256(binData))
  }

  /**
    *
    * @param binData
    * @return sha256 hash as base64 encoded string
    */
  def sha256Base64(binData: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(sha256(binData))
  }

  /**
    * Gives us the Digest of a hash based on which call all sorts of methods (including conversion to hexString or
    * byteArray).
    *
    * @param data data to hash
    * @return digest with hash of input data
    */
  def sha512Digest(data: String): Digest = data.sha512

  /**
    * Convenience method that gives us the hash of the input as hexString.
    *
    * @param data data to hash
    * @return hex string representation: SHA-512 hash of input data
    */
  def sha512HexString(data: String): String = sha512Digest(data).hex

  /**
    * Convenience method that gives us the hash of the input as byte array.
    *
    * @param data data to hash
    * @return byte array representation: SHA-512 hash of input data
    */
  def sha512ByteArray(data: String): Array[Byte] = sha512Digest(data).bytes

  /**
    * Convenience method that gives us the hash of the input as Base64 encoded string.
    *
    * @param data data to hash
    * @return Base64 encoded string representation: SHA-512 hash of input data
    */
  def sha512Base64(data: String): String = {
    val bytes = sha512Digest(data).bytes
    hashToBase64(bytes)
  }

  /**
    *
    * @param binData
    * @return sha512 hash as array of bytes
    */
  def sha512(binData: Array[Byte]): Array[Byte] = {
    binData.sha512.bytes
  }

  /**
    *
    * @param binData
    * @return sha512 hash as hex encoded string
    */
  def sha512Hex(binData: Array[Byte]): String = {
    Hex.encodeHexString(sha512(binData))
  }

  /**
    *
    * @param binData
    * @return sha512 hash as base64 encoded string
    */
  def sha512Base64(binData: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(sha512(binData))
  }

  final val pbkdf2IterationsDefault: Int = 10000

  /**
    * Gives us the Digest of a PBKDF2 hash.
    *
    * @param data       data to hash
    * @param salt       salt to use with hash function
    * @param iterations bcrypt iterations count
    * @return digest with hashed input data
    */
  def pbkdf2Digest(data: String, salt: String, iterations: Int = pbkdf2IterationsDefault): Digest = data.pbkdf2(salt, iterations, 128)

  /**
    * Convenience method to get PBKDF2 hash as hexString.
    *
    * @param data       data to hash
    * @param salt       salt to use with hash function
    * @param iterations bcrypt iterations count
    * @return hash as hex string
    */
  def pbkdf2HexString(data: String, salt: String, iterations: Int = pbkdf2IterationsDefault): String = pbkdf2Digest(data, salt, iterations).hex

  /**
    * Convenience method to get PBKDF2 hash as byte array.
    *
    * @param data       data to hash
    * @param salt       salt to use with hash function
    * @param iterations bcrypt iterations count
    * @return hash as byte array
    */
  def pbkdf2ByteArray(data: String, salt: String, iterations: Int = pbkdf2IterationsDefault): Array[Byte] = pbkdf2Digest(data, salt, iterations).bytes

  /**
    * Convenience method to get PBKDF2 hash as Base64 encoded string.
    *
    * @param data       data to hash
    * @param salt       salt to use with hash function
    * @param iterations bcrypt iterations count
    * @return base64(hash)
    */
  def pbkdf2Base64(data: String, salt: String, iterations: Int = pbkdf2IterationsDefault): String = {
    val bytes = pbkdf2Digest(data, salt, iterations).bytes
    hashToBase64(bytes)
  }

  /**
    * Convenience method correctly converting a hash from hex string to byte array.
    *
    * @param hexString hash represented as hex string
    * @return input converted to byte array
    */
  def hashToBytes(hexString: String): Array[Byte] = Hash(hexString).bytes

  /**
    * Convenience method correctly converting a hash from byte array to hex string.
    *
    * @param byteArray hash represented as byte array
    * @return input converted to hex string
    */
  def hashToHex(byteArray: Array[Byte]): String = Hash(byteArray).hex


  /**
    * Convenience method correctly converting a hash from byte array to a Base64 encoded string.
    *
    * @param byteArray hash represented as byte array
    * @return input converted to Base64 encoded string
    */
  def hashToBase64(byteArray: Array[Byte]): String = new String(Base64.getEncoder.encode(byteArray))

  /**
    * Convenience method generating random hashes (useful for creating a genesis block or in tests).
    *
    * @param elementCount number of randomly generated hashes
    * @return sequence of random hashes
    */
  def randomSha256Hashes(elementCount: Int = Random.nextInt(30000)): Seq[String] = {

    val randomSeq: Seq[String] = for (i <- 1 to elementCount) yield Random.nextLong.toString
    randomSeq.map(HashUtil.sha256HexString)

  }

}
