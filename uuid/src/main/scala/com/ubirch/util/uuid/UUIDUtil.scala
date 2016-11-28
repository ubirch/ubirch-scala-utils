package com.ubirch.util.uuid

import java.util.UUID

/**
  * Created by derMicha on 01/08/16.
  */
object UUIDUtil {

  def uuid: UUID = java.util.UUID.randomUUID()

  def uuidStr: String = uuid.toString

  def fromString(uuidStr: String): UUID = UUID.fromString(uuidStr)

}
