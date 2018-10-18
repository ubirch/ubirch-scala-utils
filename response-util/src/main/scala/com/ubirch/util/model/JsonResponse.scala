package com.ubirch.util.model

import com.ubirch.util.json.JsonFormats

import org.json4s.Formats
import org.json4s.native.Serialization.write

/**
  * author: cvandrei
  * since: 2016-09-20
  */
case class JsonResponse(version: String = "1.0",
                        status: String = "OK",
                        message: String
                       ) {

  implicit val formats: Formats = JsonFormats.default

  def toJsonString: String = write(this)

}
