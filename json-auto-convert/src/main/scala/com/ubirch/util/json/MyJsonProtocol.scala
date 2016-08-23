package com.ubirch.util.json

import org.json4s.ext.{JavaTypesSerializers, JodaTimeSerializers}
import org.json4s.{DefaultFormats, Formats, jackson}

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait MyJsonProtocol {

  implicit val serialization = jackson.Serialization // or native.Serialization

  implicit def json4sJacksonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all ++ JodaTimeSerializers.all

}
