package com.ubirch.util.http.auth

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader

import scala.collection.immutable

/**
  * author: cvandrei
  * since: 2018-10-18
  */
object AuthUtil {

  /**
    * @param oidcToken   OpenID Connect token (preferred authorization method)
    * @param ubirchToken ubirch token (ignored if oidcToken is set)
    * @return empty if none of the tokens is defined; otherwise an `Authorization` header with either the oidcToken (preferred) or ubirch token
    */
  def authHeaders(oidcToken: Option[String] = None,
                  ubirchToken: Option[String] = None
                 ): immutable.Seq[HttpHeader] = {

    if (oidcToken.isDefined) {
      RawHeader("Authorization", s"Bearer ${oidcToken.get}") :: Nil
    } else if (ubirchToken.isDefined) {
      RawHeader("Authorization", s"Bearer ${ubirchToken.get}") :: Nil
    } else {
      immutable.Seq.empty
    }

  }

}
