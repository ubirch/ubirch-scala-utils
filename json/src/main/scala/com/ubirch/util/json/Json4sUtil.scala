package com.ubirch.util.json

import java.io.InputStream

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{read, write}

/**
  * Created by derMicha on 30/07/16.
  */
object Json4sUtil {

  implicit val formats: Formats = JsonFormats.default

  def jvalue2String(jval: JValue): String = compact(render(jval))

  def any2String(obj: AnyRef): Option[String] = {
    try {
      Some(write[AnyRef](obj))
    }
    catch {
      case t: Throwable =>
        None
    }
  }

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

  def inputstream2jvalue(is: InputStream): Option[JValue] = {
    try {
      Some(read[JValue](is))
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

  def any2any[T<:Any](modelA: AnyRef)(implicit mf:Manifest[T]): T = {

    // TODO unit tests
    val json = write(modelA)
    read[T](json)

  }

  def string2any[T<:Any](jsonString: String)(implicit mf:Manifest[T]): T = {

    // TODO unit tests
    read[T](jsonString)

  }

}
