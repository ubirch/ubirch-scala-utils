package com.ubirch.util.elasticsearch

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.scalatest.{AsyncFeatureSpec, BeforeAndAfterAll, Matchers}

class EsMappingSpec extends AsyncFeatureSpec with EsMappingTrait
  with Matchers
  with BeforeAndAfterAll
  with StrictLogging {

  implicit val esClient: RestHighLevelClient = EsSimpleClient.getCurrentEsClient

  private val testIndex = "test-index"

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
  override val indexesAndMappings: Map[String, String] =
    Map(testIndex ->
      s"""{
         |  "properties" : {
         |    "id" : {
         |       "type" : "keyword"
         |    },
         |    "hello" : {
         |      "type" : "keyword"
         |    },
         |    "value" : {
         |      "type" : "integer"
         |     }
         |  }
         |}""".stripMargin
    )


  feature("create index with mapping") {

    scenario("testIndex") {
      createElasticsearchMappings()
      val request = new GetIndexRequest(testIndex)
      assert(esClient.indices().exists(request, RequestOptions.DEFAULT), true)
    }
  }

}
