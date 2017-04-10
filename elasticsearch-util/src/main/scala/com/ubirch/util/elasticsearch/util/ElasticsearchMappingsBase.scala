package com.ubirch.util.elasticsearch.util

import java.net.URL

import com.typesafe.scalalogging.slf4j.StrictLogging
import uk.co.bigbeeconsultants.http.header.MediaType._
import uk.co.bigbeeconsultants.http.request.RequestBody
import uk.co.bigbeeconsultants.http.response.Status._
import uk.co.bigbeeconsultants.http.{Config, HttpClient}

/**
  * This a util helping us to created Elasticsearch indexes and mappings. To use it overwrite only the fields marked
  * below.
  *
  * author: cvandrei
  * since: 2017-01-10
  */
trait ElasticsearchMappingsBase extends StrictLogging {

  /**
    * All indexes (OVERWRITE!!!).
    */
  val indexInfos: Seq[IndexInfo]

  /**
    * All mappings (OVERWRITE!!!).
    */
  val mappings: Seq[Mapping]

  /**
    * Create all mappings.
    */
  final def createElasticsearchMappings(): Unit = mappings foreach create

  private def create(mapping: Mapping) = {

    val config = Config(
      connectTimeout = 10000,
      readTimeout = 10000,
      followRedirects = false
    )

    val httpClient = new HttpClient(config)

    val body = Some(RequestBody(mapping.mappings, APPLICATION_JSON))
    val res = httpClient.post(mapping.url, body)

    res.status match {
      case S200_OK => logger.info(s"Elasticsearch index and mapping created: ${mapping.url}")
      case S400_BadRequest => logger.info(s"Elasticsearch index and mapping already exists: ${mapping.url}")
      case _ => logger.error(s"failed to create Elasticsearch index and mapping: ${mapping.url} (statusCode=${res.status})")
    }

  }

}

case class IndexInfo(host: String, port: Int, index: String) {
  def url: URL = new URL(s"http://$host:$port/$index")
}

case class Mapping(url: URL, mappings: String)
