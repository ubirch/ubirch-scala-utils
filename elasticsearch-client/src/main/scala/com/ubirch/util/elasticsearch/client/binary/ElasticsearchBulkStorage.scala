package com.ubirch.util.elasticsearch.client.binary

import com.typesafe.scalalogging.slf4j.LazyLogging

import com.ubirch.util.json.Json4sUtil

import org.elasticsearch.action.bulk.{BackoffPolicy, BulkProcessor, BulkRequest, BulkResponse}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.unit.{ByteSizeUnit, ByteSizeValue, TimeValue}
import org.json4s.JValue

/**
  * Using the Elasticsearch TransportClient to access the database: https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html
  *
  * author: derMicha
  * since: 2016-10-02
  */
trait ElasticsearchBulkStorage extends LazyLogging {

  protected val esClient: TransportClient

  private val bulkProcessor = BulkProcessor.builder(esClient, new BulkProcessor.Listener() {

    @Override
    def beforeBulk(executionId: Long, request: BulkRequest): Unit = {
      logger.info("beforeBulk")
    }

    @Override
    def afterBulk(executionId: Long, request: BulkRequest, response: BulkResponse): Unit = {
      logger.info("afterBulk")
    }

    @Override
    def afterBulk(executionId: Long, request: BulkRequest, failure: Throwable): Unit = {
      logger.error("afterBulk", failure)
    }
  }
  )
    .setBulkActions(2000)
    .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB))
    .setFlushInterval(TimeValue.timeValueSeconds(5))
    .setConcurrentRequests(2)
    .setBackoffPolicy(
      BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
    .build()

  def storeBulkData(index: String, datatype: String, primaryKey: String, data: JValue, timestamp: Long): JValue = {

    bulkProcessor.add(
      new IndexRequest(index, datatype, primaryKey)
        .source(Json4sUtil.jvalue2String(data))
        .timestamp(timestamp.toString)
    )
    data
  }

  def closeConnection(): Unit = {
    bulkProcessor.close()
  }

}