package com.ubirch.util.elasticsearch.client.binary.storage.base

import java.util.concurrent.ExecutionException

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.deepCheck.model.DeepCheckResponse
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.uuid.UUIDUtil

import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.index.IndexNotFoundException
import org.elasticsearch.index.query.{QueryBuilder, QueryShardException}
import org.elasticsearch.search.SearchParseException
import org.elasticsearch.search.sort.SortBuilder
import org.json4s._

import scala.Predef._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Using the Elasticsearch TransportClient to access the database: https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html
  *
  * author: derMicha
  * since: 2016-10-06
  */
trait ESStorageBase extends StrictLogging {

  implicit val formats: Formats = JsonFormats.default
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  protected val esClient: TransportClient

  /**
    * returns current ElasticSearch Transport Client instance
    *
    * @return esClient as TransportClient
    */
  def getCurrentEsClient: TransportClient = esClient

  /**
    *
    * @param docIndex  name of the index into which the current document should be stored
    * @param docType   name of the current documents type
    * @param docIdOpt  unique id which identifies current document uniquely inside the index
    * @param timestamp name of timestamp attribute
    * @param ttl       sets the relative ttl value in milliseconds, a value of 0 means no ttl
    * @param doc       document as a JValue which should be stored
    * @return
    */
  def storeDoc(docIndex: String,
               docType: String,
               doc: JValue,
               ttl: Long = 0l,
               docIdOpt: Option[String] = None,
               timestamp: Option[String] = None
              ): Future[JValue] = Future {

    require(docIndex.nonEmpty && docType.nonEmpty && (docIdOpt.isEmpty || docIdOpt.get.nonEmpty), "json invalid arguments")

    val docId = docIdOpt.getOrElse(UUIDUtil.uuidStr)

    Json4sUtil.jvalue2String(doc) match {
      case docStr if docStr.nonEmpty =>
        val pIdx = esClient
          .prepareIndex(docIndex, docType, docId)
          .setSource(docStr)
        if (ttl > 0) {
          pIdx.setTTL(ttl)
        }
        if (timestamp.isDefined) {
          pIdx.setTimestamp(timestamp.get)
        }

        val res = pIdx.get()

        res.getId == docId match {
          case true => doc
          case _ => throw new Exception("store failed")
        }

      case _ => throw new Exception("json failed")
    }

  }

  /**
    *
    * @param docIndex name of the ElasticSearch index
    * @param docType  name of the type of document
    * @param docId    unique Id per Document
    * @return
    */
  def getDoc(docIndex: String, docType: String, docId: String): Future[Option[JValue]] = Future {

    require(docIndex.nonEmpty && docType.nonEmpty && docId.nonEmpty, "json invalid arguments")

    try {

      esClient.prepareGet(docIndex, docType, docId).get() match {
        case rs if rs.isExists => Json4sUtil.string2JValue(rs.getSourceAsString)
        case _ => None
      }

    } catch {

      case infExc: IndexNotFoundException =>
        logger.error(s"IndexNotFoundException: index=$docIndex", infExc)
        None

      case execExc: ExecutionException if execExc.getCause.getCause.getCause.isInstanceOf[SearchParseException] =>
        logger.info(s"SearchParseException: index=$docIndex", execExc)
        None

      case execExc: ExecutionException if execExc.getCause.getCause.getCause.getCause.isInstanceOf[QueryShardException] =>
        logger.info(s"QueryShardException (5.3.x): index=$docIndex", execExc)
        None

      case execExc: ExecutionException if execExc.getCause.getCause.getCause.isInstanceOf[QueryShardException] =>
        logger.info(s"QueryShardException (5.5.x): index=$docIndex", execExc)
        None

    }

  }

  /**
    * @param docIndex name of the ElasticSearch index
    * @param docType  name of the type of document
    * @param query    search query as created with [[org.elasticsearch.index.query.QueryBuilders]]
    * @param from     pagination from
    * @param size     maximum number of results
    * @param sort     optional result sort
    * @return
    */
  def getDocs(docIndex: String,
              docType: String,
              query: Option[QueryBuilder] = None,
              from: Option[Int] = None,
              size: Option[Int] = None,
              sort: Option[SortBuilder[_]] = None
             ): Future[List[JValue]] = {

    require(docIndex.nonEmpty && docType.nonEmpty, "json invalid arguments")

    Future {
      var requestBuilder = esClient.prepareSearch(docIndex)
        .setTypes(docType)

      if (query.isDefined) {
        requestBuilder = requestBuilder.setQuery(query.get)
      }

      if (from.isDefined) {
        require(from.get >= 0, "from may be zero or larger")
        requestBuilder = requestBuilder.setFrom(from.get)
      }

      if (size.isDefined) {
        require(size.get >= 0, "size may be zero or larger")
        requestBuilder = requestBuilder.setSize(size.get)
      }

      if (sort.isDefined) {
        requestBuilder = requestBuilder.addSort(sort.get)
      }
      try {

        requestBuilder.execute()
          .get() match {

          case srs if srs.getHits.getTotalHits > 0 =>
            srs.getHits.getHits.map { hit =>
              Json4sUtil.string2JValue(hit.getSourceAsString)
            }.filter(_.isDefined).map(_.get.extract[JValue]).toList
          case _ =>
            List()
        }

      } catch {

        case execExc: ExecutionException if execExc.getCause.getCause.isInstanceOf[IndexNotFoundException] =>
          logger.error(s"IndexNotFoundException: index=$docIndex", execExc)
          List()

        case execExc: ExecutionException if execExc.getCause.getCause.getCause.isInstanceOf[SearchParseException] =>
          logger.info(s"SearchParseException: index=$docIndex", execExc)
          List()

        case execExc: ExecutionException if execExc.getCause.getCause.getCause.getCause.isInstanceOf[QueryShardException] =>
          logger.info(s"QueryShardException (5.3.x): index=$docIndex", execExc)
          List()

        case execExc: ExecutionException if execExc.getCause.getCause.getCause.isInstanceOf[QueryShardException] =>
          logger.info(s"QueryShardException (5.5.x): index=$docIndex", execExc)
          List()

      }
    }

  }

  /**
    * removes a document from it's index
    *
    * @param docIndex name of the index
    * @param docType  name of the doc type
    * @param docId    unique id
    * @return
    */
  def deleteDoc(docIndex: String, docType: String, docId: String): Future[Boolean] = Future {

    require(docIndex.nonEmpty && docType.nonEmpty && docId.nonEmpty, "json invalid arguments")

    val res: DeleteResponse = esClient.prepareDelete(docIndex, docType, docId).get()
    res.getResult == Result.DELETED

  }

  /**
    * Query an index for a single record to test connectivity to Elasticsearch.
    *
    * @param docIndex index to query
    * @param docType  type to query
    * @return result of connectivity check
    */
  def connectivityCheck(docIndex: String = "foo", docType: String = "bar"): Future[DeepCheckResponse] = {

    getDocs(docIndex = docIndex, docType = docType, size = Some(1))
      .map(_ => DeepCheckResponse())
      .recover {

        case t: Throwable =>
          DeepCheckResponse(
            status = false,
            messages = Seq(t.getMessage)
          )

      }
  }

}
