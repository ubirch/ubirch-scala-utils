package com.ubirch.util.json

import org.json4s.ext.{JavaTypesSerializers, JodaTimeSerializers}
import org.json4s.{DefaultFormats, Formats}

/**
  * author: cvandrei
  * since: 2016-11-04
  */
object JsonFormats {

  def default: Formats = DefaultFormats.lossless ++ JavaTypesSerializers.all ++ JodaTimeSerializers.all

}
