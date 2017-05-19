package com.ubirch.util.rest.akka.test.cors

import org.scalatest.{FeatureSpec, Matchers}

import akka.http.scaladsl.model.headers.{HttpOriginRange, `Access-Control-Allow-Credentials`}
import akka.http.scaladsl.testkit.ScalatestRouteTest

/**
  * author: cvandrei
  * since: 2016-11-17
  */
trait CORSUtil extends FeatureSpec
  with Matchers
  with ScalatestRouteTest {

  def verifyCORSHeader(exist: Boolean = true, originRange: HttpOriginRange = HttpOriginRange.*): Unit = {

    if (exist) {

      header("Access-Control-Allow-Origin") should be('isDefined)
      header("Access-Control-Allow-Credentials") should be(Some(`Access-Control-Allow-Credentials`(true)))

    } else {

      header("Access-Control-Allow-Origin") should be(None)
      header("Access-Control-Allow-Methods") should be(None)
      header("Access-Control-Allow-Headers") should be(None)
      header("Access-Control-Allow-Credentials") should be(None)

    }

  }

}
