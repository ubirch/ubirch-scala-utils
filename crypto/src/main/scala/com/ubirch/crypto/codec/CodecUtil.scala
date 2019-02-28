package com.ubirch.crypto.codec

import java.util.Base64

import org.apache.commons.codec.binary.Hex

object CodecUtil {

  /**
    * could decode Base64 and Hex to Array[Byte]
    *
    * @param encoded String which could contain Base64 or Hex encoded data
    * @return array of bytes
    */
  def multiDecoder(encoded: String): Option[Array[Byte]] = {
    try {
      Some(Hex.decodeHex(encoded.toCharArray))
    }
    catch {
      case e: Exception =>
        try {
          Some(Base64.getDecoder.decode(encoded.getBytes("UTF-8")))
        }
        catch {
          case e: Exception =>
            None
        }
    }
  }

}
