package com.ubirch.util.mongo

import com.ubirch.util.mongo.connection.Connection
import com.ubirch.util.mongo.connection.Exceptions.GettingConnectionException
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FeatureSpec, Matchers}

class ConnectionSpec extends FeatureSpec with Matchers with MockitoSugar {

  feature("A ConnectionSpec") {

    scenario("Connection.get fails when prefix key is empty or non-existent") {

      assertThrows[GettingConnectionException](Connection.get(""))

      assertThrows[GettingConnectionException](Connection.get("this.is.my.imaginary.path"))


    }



  }

}
