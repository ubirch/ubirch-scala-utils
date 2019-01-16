package com.ubirch.util.mongo

import com.ubirch.util.mongo.connection.Connection
import com.ubirch.util.mongo.connection.Exceptions.GettingConnectionException
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FeatureSpec, Matchers}

import scala.language.postfixOps

class ConnectionSpec
  extends FeatureSpec
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Matchers
    with MockitoSugar {

  feature("A ConnectionSpec") {

    scenario("Connection.get fails when prefix key is empty or non-existent") {

      assertThrows[GettingConnectionException](Connection.get(""))

      assertThrows[GettingConnectionException](Connection.get("this.is.my.imaginary.path"))


    }

    scenario("Connection.connIsActive checks if the logical connection has been created (POOL)") {

      val connection = Connection.get()

      Thread.sleep(3000)

      assert(connection.connIsActive)

      Thread.sleep(3000)

      connection.closeLogical()

      Thread.sleep(3000)


    }


  }

}
