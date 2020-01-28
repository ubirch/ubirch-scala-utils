package com.ubirch.util.elasticsearch.client.binary.storage.base

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.elasticsearch.client.binary.config.ESConfig
import com.ubirch.util.json.Json4sUtil

import org.elasticsearch.action.bulk.{BackoffPolicy, BulkProcessor, BulkRequest, BulkResponse}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.unit.{ByteSizeUnit, ByteSizeValue, TimeValue}
import org.elasticsearch.common.xcontent.XContentType
import org.json4s.JsonAST.JValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

/**
  * Using the Elasticsearch TransportClient to access the database: https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html
  *
  * author: derMicha
  * since: 2016-10-02
  */
trait ESBulkStorageBase extends StrictLogging {

  protected val esClient: TransportClient

  lazy private val bulkProcessor = BulkProcessor.builder(esClient, new BulkProcessor.Listener() {

    /**
      * returns current ElasticSearch Transport Client instance
      *
      * @return esClient as TransportClient
      */
    def getCurrentEsClient: TransportClient = esClient


    @Override
    def beforeBulk(executionId: Long, request: BulkRequest): Unit = {
      logger.debug(s"beforeBulk($executionId, #${request.numberOfActions()}, ${request.estimatedSizeInBytes()})")
    }

    @Override
    def afterBulk(executionId: Long, request: BulkRequest, response: BulkResponse): Unit = {
      logger.debug(s"afterBulk($executionId, #${request.numberOfActions()}, ${request.estimatedSizeInBytes()}) => ${response.getTook}")
    }

    @Override
    def afterBulk(executionId: Long, request: BulkRequest, failure: Throwable): Unit = {
      logger.error(s"afterBulk($executionId, #${request.numberOfActions()}, ${request.estimatedSizeInBytes()})", failure)
    }
  }
  )
    .setBulkActions(ESConfig.bulkActions)
    .setBulkSize(new ByteSizeValue(ESConfig.bulkSize, ByteSizeUnit.MB))
    .setFlushInterval(TimeValue.timeValueSeconds(ESConfig.flushInterval))
    .setConcurrentRequests(ESConfig.concurrentRequests)
    .setBackoffPolicy(
      BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(50), 10))
    .build()

  def storeDocBulk(docIndex: String,
                   docType: String,
                   docId: String,
                   doc: JValue
                  ): Future[JValue] = {
    Future {
      blocking {
        bulkProcessor.add(
          new IndexRequest(docIndex, docType, docId)
            .source(Json4sUtil.jvalue2String(doc), XContentType.JSON)
        )
      }

      doc
    }
  }

  def closeConnection(): Unit = {
    bulkProcessor.close()
  }

}