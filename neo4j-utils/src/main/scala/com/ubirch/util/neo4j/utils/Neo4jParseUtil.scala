package com.ubirch.util.neo4j.utils

import com.typesafe.scalalogging.slf4j.StrictLogging

import org.joda.time.DateTime
import org.neo4j.driver.v1.Value

/**
  * author: cvandrei
  * since: 2018-08-08
  */
object Neo4jParseUtil extends StrictLogging {

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

  /**
    * Converts a key-value map into a format that we can put into a Cypher query. For example `CREATE (record:Record $data) RETURN record` where $data is `{fieldA: foo, fieldB: 42}`.
    *
    * With different queries depending on different formats we can configure the opening and closing character and of
    * the result and also the key-value delimiter.
    *
    * @param keyValue          key-value map to convert into `{key1: value1, key2: value2, ...}` format
    * @param keyPrefix         sometimes a prefix is needed for values
    * @param keyValueDelimiter string separating keys from values
    * @param openingChar       string appended before the resulting total string
    * @param closingChar       string appended after the resulting total string
    * @return string representation of input key-value map: `{key1: value1, key2: value2, ...}`
    */
  def keyValueToString(keyValue: Map[String, Any],
                       keyPrefix: String = "",
                       keyValueDelimiter: String = ": ",
                       openingChar: String = "{",
                       closingChar: String = "}"
                      ): String = {

    val data: String = keyValue map {
      case (key, value: Int) => s"$keyPrefix$keyPrefix$key$keyValueDelimiter$value"
      case (key, value: Long) => s"$keyPrefix$keyPrefix$key$keyValueDelimiter$value"
      case (key, value: Boolean) => s"$keyPrefix$key$keyValueDelimiter$value"
      case (key, value: String) => s"""$keyPrefix$key$keyValueDelimiter"$value""""
      case (key, value) => s"""$keyPrefix$key$keyValueDelimiter"$value""""
    } mkString(openingChar, ", ", closingChar)
    logger.debug(s"keyValues.string -- $data")

    data

  }

  /**
    * A version of `keyValueToString()` with defaults appropriate for `SET` clauses.
    *
    * @param keyValue key-value map to convert into `n.key1 = value1, n.key2 = value2, ...` format
    * @param keyPrefix prefix of keys so Cypher know in what reference to do the update
    * @return string representation of input key-value map: `n.key1 = value1, n.key2 = value2, ...`
    */
  def keyValueToStringSET(keyValue: Map[String, Any], keyPrefix: String): String = {

    keyValueToString(
      keyValue = keyValue,
      keyPrefix = keyPrefix,
      keyValueDelimiter = " = ",
      openingChar = "",
      closingChar = ""
    )

  }

}
