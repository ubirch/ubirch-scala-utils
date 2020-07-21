package com.ubirch.util.elasticsearch


import java.io.IOException

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.deepCheck.model.ServiceCheckResponse
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.uuid.UUIDUtil
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.delete.{DeleteRequest, DeleteResponse}
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.search.{SearchRequest, SearchResponse}
import org.elasticsearch.action.{ActionListener, DocWriteResponse}
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}
import org.elasticsearch.search.aggregations.metrics.{Avg, AvgAggregationBuilder}
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.json4s.{Formats, JValue}

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * This is an abstraction for the elasticsearch Higher Level Client
  */
object EsSimpleClient extends StrictLogging {

  implicit val formats: Formats = JsonFormats.default
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val esClient: RestHighLevelClient = EsHighLevelClient.client

  /**
    * returns current ElasticSearch Transport Client instance
    *
    * @return esClient as TransportClient
    */
  def getCurrentEsClient: RestHighLevelClient = esClient

  /**
    * This method stores a document to the index.
    *
    * @param docIndex name of the index into which the current document should be stored
    * @param docIdOpt unique id which identifies current document uniquely inside the index
    * @param doc      document as a JValue which should be stored
    * @return Boolean indicating success
    */
  def storeDoc(docIndex: String,
               doc: JValue,
               docIdOpt: Option[String] = None
              ): Future[Boolean] = {

    val docId = docIdOpt.getOrElse(UUIDUtil.uuidStr)

    Json4sUtil.jvalue2String(doc) match {

      case docStr if docStr.nonEmpty =>

        val request = new IndexRequest(docIndex).id(docId).source(docStr, XContentType.JSON)

        val promise = Promise[IndexResponse]()
        esClient.indexAsync(request, RequestOptions.DEFAULT, createActionListener[IndexResponse](promise))
        promise.future.map { response: IndexResponse =>
          val result = response.getResult
          if (result == Result.CREATED || result == Result.UPDATED) {
            logger.debug(s"the document should have been created successfully with id $docId $doc")
            true
          }
          else throw new Exception(s"storing of document $doc failed $response")
        }

      case _ => throw new Exception(s"JValue parsing to string of ($doc) failed ")
    }

  }.recover {
    case ex: Throwable =>
      logger.error(s"ES error, storing of document $doc failed  ", ex)
      false
  }


  /**
    * This method returns a document by it's id.
    *
    * @param docIndex name of the ElasticSearch index
    * @param docId    unique Id per Document
    */
  def getDoc(docIndex: String,
             docId: String): Future[Option[JValue]] = {


    val search = new SearchSourceBuilder().query(QueryBuilders.idsQuery.addIds(docId))
    val request = new SearchRequest(docIndex).source(search)

    val promise = Promise[SearchResponse]()
    esClient.searchAsync(request, RequestOptions.DEFAULT, createActionListener[SearchResponse](promise))

    promise.future.map {

      case response if response.getHits.getTotalHits.value == 1 =>
        response.getHits.getHits.map { hit =>
          Json4sUtil.string2JValue(hit.getSourceAsString)
        }.filter(_.isDefined).map(_.get.extract[JValue]).headOption

      case response if response.getHits.getTotalHits.value > 0 =>
        logger.error(s"ES confusion, found more than one document for the id: $docId")
        None

      case response =>
        logger.error(s"no document was found for the id: $docId with response $response")
        None

    }
  }.recover {
    case ex: Throwable =>
      logger.error(s"Es error: retrieving document with id $docId from index=$docIndex", ex)
      None
  }


  /**
    * This method returns all documents queried and sorted if wished for.
    *
    * @param docIndex name of the ElasticSearch index
    * @param query    search query
    * @param from     pagination from (may be 0 or larger)
    * @param size     maximum number of results (may be 0 or larger)
    * @param sort     optional result sort
    * @return
    */
  def getDocs(docIndex: String,
              query: Option[QueryBuilder] = None,
              from: Option[Int] = None,
              size: Option[Int] = None,
              sort: Option[SortBuilder[_]] = None
             ): Future[List[JValue]] = {


    val search = new SearchSourceBuilder()
    if (query.isDefined) search.query(query.get)
    if (from.isDefined) search.from(from.get)
    if (size.isDefined) search.size(size.get)
    if (sort.isDefined) search.sort(sort.get)

    val request = new SearchRequest(docIndex).source(search)

    val promise = Promise[SearchResponse]()
    esClient.searchAsync(request, RequestOptions.DEFAULT, createActionListener[SearchResponse](promise))

    promise.future.map {

      case response if response.getHits.getTotalHits.value > 0 =>
        response.getHits.getHits.map { hit =>
          Json4sUtil.string2JValue(hit.getSourceAsString)
        }.filter(_.isDefined).map(_.get.extract[JValue]).toList

      case _ =>
        List()

    }
  }.recover {
    case ex: Throwable =>
      logger.error(s"ES error: index=$docIndex", ex)
      List()
  }


  /**
    * This method queries an average aggregation and returns a double.
    *
    * @param docIndex name of the ElasticSearch index
    * @param query    search query
    * @param avgAgg   average function
    * @return Option[Double]
    */
  def getAverage(docIndex: String,
                 query: Option[QueryBuilder] = None,
                 avgAgg: AvgAggregationBuilder
                ): Future[Option[Double]] = {

    val search = new SearchSourceBuilder().aggregation(avgAgg)
    if (query.isDefined) search.query(query.get)
    val request = new SearchRequest(docIndex).source(search)

    val promise = Promise[SearchResponse]
    esClient.searchAsync(request, RequestOptions.DEFAULT, createActionListener[SearchResponse](promise))

    promise.future.map {

      case response if response.getHits.getTotalHits.value > 0 =>
        val agg = response.getAggregations
        val avg: Avg = agg.get(avgAgg.getName)
        avg.getValue match {

          case avgValue if avgValue.isInfinity =>
            None
          case avgValue if !avgValue.equals(Double.NaN) =>
            Some(avgValue)
          case _ =>
            None
        }
      case _ =>
        None
    }

  }.recover {
    case ex: Throwable =>
      logger.error(s"Es error: retrieving average from index=$docIndex", ex)
      None
  }

  /**
    * This method removes a document by it's id from the index.
    *
    * @param docIndex name of the index
    * @param docId    unique id
    * @return
    */
  def deleteDoc(docIndex: String, docId: String): Future[Boolean] = {

    val request = new DeleteRequest(docIndex, docId)

    val promise = Promise[DeleteResponse]()
    esClient.deleteAsync(request, RequestOptions.DEFAULT, createActionListener[DeleteResponse](promise))
    promise.future.map {
      case response if response.getResult == DocWriteResponse.Result.NOT_FOUND =>
        false
      case response if response.getResult == DocWriteResponse.Result.DELETED =>
        true
      case response =>
        throw new IOException(s"ES error, unexpected response $response")

    }
  }.recover {
    case ex: Throwable =>
      logger.error(s"ES error when deleting document $docId from index $docIndex ", ex)
      false
  }

  /**
    * Query an index for a single record to test connectivity to Elasticsearch.
    *
    * @param docIndex index to query
    * @return result of connectivity check
    */
  def connectivityCheck(docIndex: String = "foo"): Future[ServiceCheckResponse] = {

    getDocs(docIndex = docIndex, size = Some(1))
      .map(_ => ServiceCheckResponse())
      .recover {

        case t: Throwable =>
          logger.error("ES error, deepcheck failing", t)
          ServiceCheckResponse(
            status = false,
            messages = Seq(t.getMessage)
          )

      }
  }


  /**
    * Helper method to create an actionListnener.
    */
  private def createActionListener[T](promise: Promise[T]): ActionListener[T] = {

    new ActionListener[T] {

      override def onResponse(response: T): Unit = promise.success(response)

      override def onFailure(e: Exception): Unit = promise.failure(e)
    }
  }

  def closeConnection(): Unit = {
    esClient.close()
  }


}
