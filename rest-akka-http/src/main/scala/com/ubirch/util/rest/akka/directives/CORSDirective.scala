package com.ubirch.util.rest.akka.directives

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{HttpOriginRange, `Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.server.{Directives, Route}

/**
  * author: cvandrei
  * since: 2016-09-06
  */
trait CORSDirective extends Directives {

  private val CORSHeaders = List(
    `Access-Control-Allow-Methods`(GET, POST, PUT, DELETE, OPTIONS),
    `Access-Control-Allow-Headers`("Authorization, Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent"),
    `Access-Control-Allow-Credentials`(true)
  )

  def respondWithCORS(routes: => Route) = {
    val originHeader = `Access-Control-Allow-Origin`(HttpOriginRange.*)
    respondWithHeaders(originHeader :: CORSHeaders) {
      routes ~ options {
        complete(OK)
      }
    }
  }

}
