package com.ubirch.util.deepCheck.model

import com.ubirch.util.json.JsonFormats

import org.json4s.Formats
import org.json4s.native.Serialization.write

/**
  * author: cvandrei
  * since: 2017-06-06
  */
case class DeepCheckResponse(status: Boolean = true,
                             messages: Seq[String] = Seq.empty
                          ) {

  implicit val formats: Formats = JsonFormats.default

  def toJsonString: String = write(this)

}
