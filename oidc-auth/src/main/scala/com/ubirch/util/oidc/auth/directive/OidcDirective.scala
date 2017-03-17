package com.ubirch.util.oidc.auth.directive

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}

/**
  * author: cvandrei
  * since: 2017-03-17
  */
trait OidcDirective extends Directives {

  def verifyToken(routes: => Route): Route = {

    val token: String = "" // TODO extract from "Authorization" header (use authenticateOAuth2Async directive?)
    // TODO extract context header?
    // TODO extract provider header (needed to check existence of token in Redis)
    // TODO check if token exists in Redis
    // TODO update token's expiry date

    complete(OK)

  }

}
