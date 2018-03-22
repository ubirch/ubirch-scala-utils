package com.ubirch.util.uuid

import io.jvm.uuid._

/**
  * Created by derMicha on 01/08/16.
  */
object UUIDUtil {

  def uuid: UUID = java.util.UUID.randomUUID()

  def uuidStr: String = uuid.toString

  def fromString(uuidStr: String): UUID = UUID.fromString(uuidStr)

  def fromByteArray(binUuid: Array[Byte], offset: Int = 0): java.util.UUID = io.jvm.uuid.UUID.fromByteArray(binUuid, offset = offset)

  def toByteArray(uuid: java.util.UUID): Array[Byte] = uuid.byteArray

}
