package com.ubirch.util.elasticsearch.client.binary

import java.net.InetAddress

import com.typesafe.scalalogging.slf4j.LazyLogging
import com.ubirch.util.json.Json4sUtil
import org.json4s._
import org.scalatest.{AsyncFeatureSpec, BeforeAndAfterAll, Matchers}
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress

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
  with LazyLogging {

  implicit val formats = DefaultFormats.lossless ++ org.json4s.ext.JodaTimeSerializers.all

  val docIndex = "tests"

  val docType = "test"

  case class TestDoc(id: String, hello: String)

  val testDoc = TestDoc("1", "World")

  val testDoc2 = TestDoc("1", "Galaxy")

  feature("simple CRUD tests") {

    scenario("store") {
      val jval = Json4sUtil.any2jvalue(testDoc).get

      DeviceStorage.storeDoc(
        docIndex = docIndex,
        docType = docType,
        docIdOpt = Some(testDoc.id),
        doc = jval).map { rjval =>
        val rTestDoc = rjval.extract[TestDoc]
        rTestDoc.hello shouldBe testDoc.hello
      }
    }

    scenario("failed get") {
      val f = Await.result(DeviceStorage.getDoc("", "", ""), 5 seconds)
      f.isDefined shouldBe true
    }

    scenario("get") {
      DeviceStorage.getDoc(docIndex, docType, testDoc.id).map {
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
      Await.ready(DeviceStorage.storeDoc(
        docIndex = docIndex,
        docType = docType,
        docIdOpt = Some(testDoc2.id),
        doc = jval), 2 seconds)
      DeviceStorage.getDoc(docIndex, docType, testDoc2.id).map {
        case Some(jValue) =>
          val rTestDoc = jValue.extract[TestDoc]
          rTestDoc.id shouldBe testDoc2.id
          rTestDoc.hello shouldBe testDoc2.hello
        case None => fail("could not read stored document")
      }
    }

    scenario("getAll") {
      Thread.sleep(1000)
      DeviceStorage.getDocs(docIndex, docType).map {
        case jvals: List[JValue] =>
          jvals.size shouldBe 1
        case _ => fail("could not read stored document")
      }
    }

    scenario("delete") {
      DeviceStorage.deleteDoc(docIndex, docType, testDoc.id).map { res =>
        res shouldBe true
      }
    }
  }

}

object DeviceStorage extends ElasticsearchStorage {

  private val address = new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300)

  override protected val esClient: TransportClient = TransportClient.builder()
    .build()
    .addTransportAddress(address)

}
