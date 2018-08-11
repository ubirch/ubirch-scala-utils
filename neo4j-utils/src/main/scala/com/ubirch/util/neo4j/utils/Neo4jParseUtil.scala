package com.ubirch.util.neo4j.utils

import org.joda.time.DateTime
import org.neo4j.driver.v1.Value

/**
  * author: cvandrei
  * since: 2018-08-08
  */
object Neo4jParseUtil {

  def asType[T](value: Value, field: String): T = {

    val valueMap = value.asMap(new java.util.HashMap[String, Object]())
    valueMap.get(field) match {
      case t: T => t
    }

  }

  def asTypeOption[T](value: Value, field: String): Option[T] = {

    val valueMap = value.asMap(new java.util.HashMap[String, Object]())
    valueMap.getOrDefault(field, "--UNDEFINED--") match {
      case "--UNDEFINED--" => None
      case t: T => Some(t)
    }

  }

  def asTypeOrDefault[T](value: Value, field: String, default: AnyRef): T = {

    val valueMap = value.asMap(new java.util.HashMap[String, Object]())
    valueMap.getOrDefault(field, default) match {
      case t: T => t
    }

  }

  def asDateTime(value: Value, field: String): DateTime = {

    val valueMap = value.asMap(new java.util.HashMap[String, Object]())
    valueMap.get(field) match {
      case s: String => DateTime.parse(s)
    }

  }

  def asDateTimeOption(value: Value, field: String): Option[DateTime] = {

    val valueMap = value.asMap(new java.util.HashMap[String, Object]())
    valueMap.getOrDefault(field, "--UNDEFINED--") match {
      case "--UNDEFINED--" => None
      case s: String => Some(DateTime.parse(s))
    }

  }

}
