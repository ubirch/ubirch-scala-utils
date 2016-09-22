package com.ubirch.util.json

import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{read, write}

/**
  * Created by derMicha on 30/07/16.
  */
object Json4sUtil {

  implicit val formats = DefaultFormats.lossless ++ org.json4s.ext.JodaTimeSerializers.all

  def jvalue2String(jval: JValue) = compact(render(jval))

  def string2JValue(value: String): Option[JValue] = {
    try {
      Some(read[JValue](value))
    }
    catch {
      case t: Throwable =>
        None
    }
  }

  def any2jvalue(obj: AnyRef): Option[JValue] = {
    try {
      Some(read[JValue](write[AnyRef](obj)))
    }
    catch {
      case t: Throwable =>
        None
    }
  }

  def any2jobject(obj: AnyRef): Option[JObject] = {
    try {
      Some(read[JObject](write[AnyRef](obj)))
    }
    catch {
      case t: Throwable =>
        None
    }
  }


  //  TODO fix this methodt
  def string2Any[T: Manifest](value: String): Option[T] = {
    parse(value).extractOpt[T]
  }
}
