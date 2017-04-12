package com.ubirch.util.elasticsearch.util

import com.typesafe.scalalogging.slf4j.StrictLogging

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.client.transport.TransportClient

/**
  * This a util helping us to created Elasticsearch indexes and mappings. To use it overwrite only the fields marked
  * below.
  *
  * author: cvandrei
  * since: 2017-01-10
  */
trait ElasticsearchMappingsBase extends StrictLogging {

  /**
    * All indexes and their mappings (<b>OVERWRITE!!!</b>).
    *
    * A Map of indexes and optional mappings. The data is structured as follows:
    * <code>
    * Map(
    *   "INDEX_1_NAME" -> Map(
    *     "TYPE_1_NAME" -> "MAPPING_TYPE_1",
    *     "TYPE_2_NAME" -> "MAPPING_TYPE_2"
    *   ),
    *   "INDEX_2_NAME" -> Map.empty
    * )
    * </code>
    */
  val indexesAndMappings: Map[String, Map[String, String]]

  lazy final val indicesToDelete: Set[String] = indexesAndMappings.keys.toSet

  final def createElasticsearchMappings()(implicit esClient: TransportClient): Unit = indexesAndMappings foreach {
    case (index, indexMappings) => create(index, indexMappings)
  }

  private def create(index: String, mappings: Map[String, String])(implicit esClient: TransportClient) = {

    val indicesClient = esClient.admin.indices()
    val existsRequest = new IndicesExistsRequest(index)
    if (indicesClient.exists(existsRequest).get().isExists) {

      logger.info(s"index already exists: '$index'")

    } else {

      var requestBuilder = indicesClient.prepareCreate(index)
      mappings foreach {
        case (typeName, typeMapping) => requestBuilder = requestBuilder.addMapping(typeName, typeMapping)
      }

      if (requestBuilder.get().isAcknowledged) {
        logger.info(s"created index: '$index'")
      } else {
        logger.error(s"failed to created index: '$index'")
      }

    }

  }

}
