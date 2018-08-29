package com.ubirch.util.elasticsearch.client.binary

import java.net.InetAddress

import com.typesafe.scalalogging.slf4j.StrictLogging

import com.ubirch.util.elasticsearch.client.binary.storage.base.ESStorageBase
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.uuid.UUIDUtil

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.json4s._
import org.scalatest.{AsyncFeatureSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: derMicha
  * since: 2016-10-06
  */
class ElasticsearchStorageSpec extends AsyncFeatureSpec
  with Matchers
  with BeforeAndAfterAll
  with StrictLogging {

  implicit private val formats: Formats = JsonFormats.default

  val docIndex = "tests"

  val docType = "test"

  case class TestDoc(id: String, hello: String, value: Int)

  val testDoc = TestDoc("1", "World", 10)

  val testDoc2 = TestDoc("1", "Galaxy", 20)

  feature("simple CRUD tests") {

    scenario("store") {
      val jval = Json4sUtil.any2jvalue(testDoc).get

      TestStorage.storeDoc(
        docIndex = docIndex,
        docType = docType,
        docIdOpt = Some(testDoc.id),
        doc = jval).map { rjval =>
        val rTestDoc = rjval.extract[TestDoc]
        rTestDoc.hello shouldBe testDoc.hello
      }
    }

    scenario("failed get") {
      val f = Await.result(TestStorage.getDoc(docIndex, docType, UUIDUtil.uuidStr), 5 seconds)
      f.isDefined shouldBe false
    }

    scenario("get") {
      TestStorage.getDoc(docIndex, docType, testDoc.id).map {
        case Some(jval) =>
          val rTestDoc = jval.extract[TestDoc]
          rTestDoc.id shouldBe testDoc.id
          rTestDoc.hello shouldBe testDoc.hello
        case None =>
          fail("could not fetch document")
      }
    }

    scenario("update") {
      val jval = Json4sUtil.any2jvalue(testDoc2).get
      Await.ready(TestStorage.storeDoc(
        docIndex = docIndex,
        docType = docType,
        docIdOpt = Some(testDoc2.id),
        doc = jval), 2 seconds)
      TestStorage.getDoc(docIndex, docType, testDoc2.id).map {
        case Some(jValue) =>
          val rTestDoc = jValue.extract[TestDoc]
          rTestDoc.id shouldBe testDoc2.id
          rTestDoc.hello shouldBe testDoc2.hello
        case None => fail("could not read stored document")
      }
    }

    scenario("getDocs") {
      Thread.sleep(1000)
      TestStorage.getDocs(docIndex, docType).map {
        case jvals: List[JValue] =>
          jvals.size shouldBe 1
        case _ => fail("could not read stored document")
      }
    }

    scenario("getAverage() of existing field --> Some") {

      val aggregation: AvgAggregationBuilder =
        AggregationBuilders
          .avg("average")
          .field("value")

      TestStorage.getAverage(
        docIndex = docIndex,
        docType = docType,
        agg = aggregation
      ) map { result =>

        result shouldBe Some(20d)

      }

    }

    scenario("getAverage() of non-existing field --> None") {

      val aggregation: AvgAggregationBuilder =
        AggregationBuilders
          .avg("average")
          .field("value2")

      TestStorage.getAverage(
        docIndex = docIndex,
        docType = docType,
        agg = aggregation
      ) map { result =>

        result shouldBe 'empty

      }

    }

    scenario("delete") {
      TestStorage.deleteDoc(docIndex, docType, testDoc.id).map { res =>
        res shouldBe true
      }
    }
  }

}

object TestStorage extends ESStorageBase {

  private val address = new TransportAddress(InetAddress.getByName("localhost"), 9300)

  override protected val esClient: TransportClient = new PreBuiltTransportClient(Settings.EMPTY)
    .addTransportAddress(address)

}
