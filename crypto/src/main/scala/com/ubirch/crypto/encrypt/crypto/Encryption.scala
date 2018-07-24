/**
  * this peace of code was inspired based on https://gist.github.com/mumoshu/1587327
  * many thanks to KUOKA Yusuke (https://gist.github.com/mumoshu)
  */

package com.ubirch.crypto.encrypt.crypto

import java.util.Base64

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Hex

trait Encryption {

  def encryptStr2Hex(data: String, secret: String): String

  def encryptStr2B64(data: String, secret: String): String

  def encryptStr(data: String, secret: String): Array[Byte]

  def encryptHex(data: String, secret: String): Array[Byte]

  def encryptB64(data: String, secret: String): Array[Byte]

  def encrypt(dataBytes: Array[Byte], secret: String): Array[Byte]

  def decrypHex2Str(data: String, secret: String): String

  def decrypB642Str(data: String, secret: String): String

  def decryptHex(data: String, secret: String): Array[Byte]

  def decryptB64(data: String, secret: String): Array[Byte]

  def decrypt(codeBytes: Array[Byte], secret: String): Array[Byte]

}

/**
  *
  * @param algorithmName may be DES or AES
  * @param mode          ECB
  * @param padding       PKCS5Padding
  * @param encoding      UTF-8
  */
class JavaCryptoEncryption(
                            algorithmName: String = "AES",
                            mode: String = "ECB",
                            padding: String = "PKCS5Padding",
                            encoding: String = "UTF-8")
  extends Encryption {


  /**
    *
    * @param data   data as a String that have to be encrypted
    * @param secret secret encryption secret
    * @return encrypted input as a Hex String
    */
  def encryptStr2Hex(data: String, secret: String): String = {
    Hex.encodeHexString(encryptStr(data, secret))
  }

  /**
    *
    * @param data   data as a String that have to be encrypted
    * @param secret secret encryption secret
    * @return encrypted input as a Base64 String
    */
  def encryptStr2B64(data: String, secret: String): String = {
    Base64.getEncoder.encodeToString(encryptStr(data, secret))
  }

  /**
    *
    * @param data   data as a String that have to be encrypted
    * @param secret encryption secret
    * @return encrypted data as array of bytes
    */
  def encryptStr(data: String, secret: String): Array[Byte] = {
    val bytes = data.getBytes(encoding)
    encrypt(bytes, secret)
  }

  /**
    *
    * @param data   data as a String that have to be encrypted
    * @param secret encryption secret
    * @return encrypted data as array of bytes
    */
  def encryptHex(data: String, secret: String): Array[Byte] = {
    val bytes = Hex.decodeHex(data)
    encrypt(bytes, secret)
  }

  /**
    *
    * @param data   data as a String that have to be encrypted
    * @param secret encryption secret
    * @return encrypted data as array of bytes
    */
  def encryptB64(data: String, secret: String): Array[Byte] = {
    val bytes = Base64.getDecoder.decode(data)
    encrypt(bytes, secret)
  }

  def encrypt(bytes: Array[Byte], secret: String): Array[Byte] = {

    assert(bytes.length >= 0, message = "given data may not be zero")
    if (algorithmName.equals("AES"))
      assert(secret.length >= 16, message = "AES secret must have at least length of 15")
    else if (algorithmName.equals("DES"))
      assert(secret.length == 8, message = "DES secret must have a length of 8")

    val secretKey = new SecretKeySpec(secret.getBytes(encoding), algorithmName)
    val encipher = Cipher.getInstance(s"$algorithmName/$mode/$padding")
    encipher.init(Cipher.ENCRYPT_MODE, secretKey)
    encipher.doFinal(bytes)
  }

  /**
    *
    * @param data   data that have to be decrypted as a Base64 encoded String
    * @param secret encryption secret
    * @return decrypted data as array of bytes
    */
  def decrypHex2Str(data: String, secret: String): String = {
    val bytes = decryptHex(data, secret)
    new String(bytes, 0, bytes.length, "UTF-8")
  }

  /**
    *
    * @param data   data that have to be decrypted as a Base64 encoded String
    * @param secret encryption secret
    * @return decrypted data as array of bytes
    */
  def decrypB642Str(data: String, secret: String): String = {
    val bytes = decryptB64(data, secret)
    new String(bytes, 0, bytes.length, "UTF-8")
  }

  /**
    *
    * @param data   data that have to be decrypted as a Base64 encoded String
    * @param secret encryption secret
    * @return decrypted data as array of bytes
    */
  def decryptHex(data: String, secret: String): Array[Byte] = {
    val bytes = Hex.decodeHex(data)
    decrypt(bytes, secret)
  }

  /**
    *
    * @param data   data that have to be decrypted as a Base64 encoded String
    * @param secret encryption secret
    * @return decrypted data as array of bytes
    */
  def decryptB64(data: String, secret: String): Array[Byte] = {
    val bytes = Base64.getDecoder.decode(data)
    decrypt(bytes, secret)
  }

  /**
    *
    * @param bytes  data that have to be decrypted as a array of bytes
    * @param secret encryption secret
    * @return decrypted data as array of bytes
    */
  def decrypt(bytes: Array[Byte], secret: String): Array[Byte] = {
    val secretKey = new SecretKeySpec(secret.getBytes(encoding), algorithmName)
    val encipher = Cipher.getInstance(s"$algorithmName/$mode/$padding")
    encipher.init(Cipher.DECRYPT_MODE, secretKey)
    encipher.doFinal(bytes)
  }
}
