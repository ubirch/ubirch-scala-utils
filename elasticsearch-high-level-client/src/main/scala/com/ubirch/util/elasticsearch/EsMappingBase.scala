package com.ubirch.util.elasticsearch

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.elasticsearch.client.indices.{CreateIndexRequest, GetIndexRequest}
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}

import scala.collection.JavaConverters._

trait EsMappingBase extends StrictLogging {

  /**
    * All indexes and their mappings (<b>OVERWRITE!!!</b>).
    *
    * A Map of indexes and optional mappings. The data is structured as follows:
    * <code>
    * Map(
    * "INDEX_1_NAME" -> Map(
    * "TYPE_1_NAME" -> "MAPPING_TYPE_1",
    * "TYPE_2_NAME" -> "MAPPING_TYPE_2"
    * ),
    * "INDEX_2_NAME" -> Map.empty
    * )
    * </code>
    */
  val indexesAndMappings: Map[String, Map[String, String]]

  lazy final val indicesToDelete: Set[String] = indexesAndMappings.keys.toSet

  /**
    * Method to create all indexes and their mappings if not yet existing.
    */
  final def createElasticsearchMappings()(implicit esClient: RestHighLevelClient): Unit =
    indexesAndMappings foreach {
      case (index, indexMappings) => create(index, indexMappings)
    }

  /**
    * Method that creates an index with it's mapping, if it doesn't exist yet.
    */
  private def create(index: String, mappings: Map[String, String])
                    (implicit esClient: RestHighLevelClient): Unit = {

    val request = new GetIndexRequest(index)

    if (esClient.indices().exists(request, RequestOptions.DEFAULT)) {

      logger.info(s"index already exists: '$index'")

    } else {

      val request = new CreateIndexRequest(index).mapping(mappings.asJava)
      val indexResponse = esClient.indices.create(request, RequestOptions.DEFAULT)
      if (indexResponse.isAcknowledged) {
        logger.info(s"created index: '$index' and it's mapping: '$mappings'")
      } else {
        logger.error(s"failed to create index: '$index' and it's mapping: '$mappings'")
      }

    }

  }

}
