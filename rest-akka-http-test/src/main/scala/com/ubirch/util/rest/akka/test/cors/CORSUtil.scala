package com.ubirch.util.rest.akka.test.cors

import org.scalatest.{FeatureSpec, Matchers}

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{HttpOriginRange, `Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.testkit.ScalatestRouteTest

/**
  * author: cvandrei
  * since: 2016-11-17
  */
trait CORSUtil extends FeatureSpec
  with Matchers
  with ScalatestRouteTest {

  def verifyCORSHeader(exist: Boolean = true, originRange: HttpOriginRange = HttpOriginRange.*): Unit = {

    exist match {

      case true =>
        header("Access-Control-Allow-Origin") should be(Some(`Access-Control-Allow-Origin`(originRange)))
        header("Access-Control-Allow-Methods") should be(Some(`Access-Control-Allow-Methods`(GET, POST, PUT, DELETE, OPTIONS)))
        header("Access-Control-Allow-Headers") should be(Some(`Access-Control-Allow-Headers`("Authorization, Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent")))
        header("Access-Control-Allow-Credentials") should be(Some(`Access-Control-Allow-Credentials`(true)))

      case false =>
        header("Access-Control-Allow-Origin") should be(None)
        header("Access-Control-Allow-Methods") should be(None)
        header("Access-Control-Allow-Headers") should be(None)
        header("Access-Control-Allow-Credentials") should be(None)

    }

  }

}
