package com.ubirch.util.mongo.connection

object Exceptions {

  abstract class ConnectionException(message: String) extends Exception(message) {
    val name: String = this.getClass.getCanonicalName
  }

  case class GettingConnectionException(message: String) extends ConnectionException(message)

  case class DatabaseConnectionException(message: String) extends ConnectionException(message)

  case class CollectionException(message: String) extends ConnectionException(message)


}
