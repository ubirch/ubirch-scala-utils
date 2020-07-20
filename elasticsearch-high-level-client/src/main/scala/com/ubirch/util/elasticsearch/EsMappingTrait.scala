package com.ubirch.util.elasticsearch

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.client.indices.{CreateIndexRequest, GetIndexRequest}
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.rest.RestStatus

trait EsMappingTrait extends StrictLogging {

  /**
    * All indexes and their mappings (<b>OVERWRITE!!!</b>).
    *
    * A Map of indexes and optional mappings. The data is structured as follows:
    * <code>
    * Map(
    * "INDEX_1_NAME" -> {
    * "properties" : {
    * "id" : {
    * "type" : "keyword"
    * },
    * "hello" : {
    * "type" : "keyword"
    * },
    * "value" : {
    * "type" : "integer"
    * }
    * }
    * },
    * "INDEX_2_NAME" -> {...}
    * )
    * </code>
    */
  val indexesAndMappings: Map[String, String]

  lazy final val indicesToDelete: Set[String] = indexesAndMappings.keys.toSet

  /**
    * Method to create all indexes and their mappings if not yet existing.
    */
  final def createElasticsearchMappings()(implicit esClient: RestHighLevelClient): Unit =
    indexesAndMappings foreach {
      case (index, indexMapping) => create(index, indexMapping)
    }

  /**
    * Method that creates an index with it's mapping, if it doesn't exist yet.
    */
  private def create(index: String, mapping: String)
                    (implicit esClient: RestHighLevelClient): Unit = {

    val request = new GetIndexRequest(index)

    if (esClient.indices().exists(request, RequestOptions.DEFAULT)) {

      logger.info(s"index already exists: '$index'")

    } else {

      logger.info(s"creating index $index with mapping : $mapping")
      val createRequest = new CreateIndexRequest(index).mapping(mapping, XContentType.JSON)
      val indexResponse = esClient.indices.create(createRequest, RequestOptions.DEFAULT)
      if (indexResponse.isAcknowledged) {
        logger.info(s"created index: '$index' and it's mapping: '$mapping'")
      } else {
        logger.error(s"failed to create index: '$index' and it's mapping: '$mapping'")
      }
    }

  }

  /**
    * Clean Elasticsearch instance by running the following operations:
    *
    * * delete indexes
    * * create mappings
    */
  final def cleanElasticsearch()(implicit esClient: RestHighLevelClient): Unit = {

    deleteIndices()
    Thread.sleep(200)

    createElasticsearchMappings()
    Thread.sleep(100)

  }

  /**
    * Delete all indexes.
    */
  final def deleteIndices()(implicit esClient: RestHighLevelClient): Unit = {

    for (index <- indicesToDelete) {

      try {

        val request = new DeleteIndexRequest(index)
        val response = esClient.indices().delete(request, RequestOptions.DEFAULT)

        if (response.isAcknowledged) {
          logger.info(s"deleted index: '$index'")
        } else {
          logger.error(s"failed to delete  index: '$index'")
        }

      } catch {

        case ex: ElasticsearchException =>
          if (ex.status() == RestStatus.NOT_FOUND) {
            logger.info(s"unable to delete non-existing index: $index")
          } else {
            logger.info(s"something else went wrong deleting the index: $index")
          }
      }
    }
  }


}
