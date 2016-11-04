package com.ubirch.util.json

import org.json4s.{Formats, jackson}

/**
  * author: cvandrei
  * since: 2016-07-27
  */
trait MyJsonProtocol {

  implicit val serialization = jackson.Serialization // or native.Serialization

  implicit def json4sJacksonFormats: Formats = JsonFormats.default

}
