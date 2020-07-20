package com.ubirch.util.elasticsearch

import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.elasticsearch.config.EsHighLevelConfig
import com.ubirch.util.json.Json4sUtil
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.bulk.{BackoffPolicy, BulkProcessor, BulkRequest, BulkResponse}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.common.unit.{ByteSizeUnit, ByteSizeValue, TimeValue}
import org.elasticsearch.common.xcontent.XContentType
import org.json4s.JValue

object EsBulkClient extends StrictLogging {

  private val esClient: RestHighLevelClient = EsHighLevelClient.client

  /**
    * returns current ElasticSearch RestHighLevelClient instance
    *
    * @return esClient as RestHighLevelClient
    */
  def getCurrentEsClient: RestHighLevelClient = esClient

  /**
    * A listener to react after or before the collected bulk of documents is written to the elasticsearch.
    */
  private val listener: BulkProcessor.Listener = new BulkProcessor.Listener() {


    @Override
    def beforeBulk(executionId: Long, request: BulkRequest): Unit = {
      logger.debug(s"beforeBulk($executionId, number of actions: #${request.numberOfActions()}, ${request.estimatedSizeInBytes()})")
    }

    @Override
    def afterBulk(executionId: Long, request: BulkRequest, response: BulkResponse): Unit = {
      logger.debug(s"afterBulk($executionId, number of actions: #${request.numberOfActions()}, ${request.estimatedSizeInBytes()}) => ${response.getTook}")
    }

    @Override
    def afterBulk(executionId: Long, request: BulkRequest, failure: Throwable): Unit = {
      logger.error(s"afterBulk($executionId, number of actions: #${request.numberOfActions()}, ${request.estimatedSizeInBytes()})", failure)
    }

  }

  private val bulkAsyncAsJava: BiConsumer[BulkRequest, ActionListener[BulkResponse]] =
    new BiConsumer[BulkRequest, ActionListener[BulkResponse]] {
      override def accept(bulkRequest: BulkRequest, actionListener: ActionListener[BulkResponse]): Unit = {
        esClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, actionListener)
      }
    }

  /**
    * Bulkprocessor with it's different configurations regarding the time of storage.
    */
  private val bulkProcessor: BulkProcessor = BulkProcessor.builder(bulkAsyncAsJava, listener)
    .setBulkActions(EsHighLevelConfig.bulkActions)
    .setBulkSize(new ByteSizeValue(EsHighLevelConfig.bulkSize, ByteSizeUnit.MB))
    .setFlushInterval(TimeValue.timeValueSeconds(EsHighLevelConfig.flushInterval))
    .setConcurrentRequests(EsHighLevelConfig.concurrentRequests)
    .setBackoffPolicy(
      BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(50), 10))
    .build()

  /**
    * Method that stores a document to the bulkprocessor that by it's configuration
    * stores all received documents after a certain amount or time to the elasticsearch.
    *
    * @param docIndex index to store the document to
    * @param docId    id of the new document
    * @param doc      document itself
    */
  def storeDocBulk(docIndex: String,
                   docId: String,
                   doc: JValue
                  ): Unit = {
    try {
      bulkProcessor.add(
        new IndexRequest(docIndex, "_doc", docId)
          .source(Json4sUtil.jvalue2String(doc), XContentType.JSON)
      )
    } catch {
      case ex: Throwable =>
        logger.debug(s"storing document in elasticsearch bulkProcessor failed for $doc with id $docId ", ex)
    }
  }

  def closeConnection(): Unit = {
    bulkProcessor.close()
    bulkProcessor.awaitClose(30L, TimeUnit.SECONDS)
  }


}
