package com.ubirch.util.oidc.model

/**
  * author: cvandrei
  * since: 2017-03-23
  */
case class UserContext(context: String,
                       providerId: String,
                       externalUserId: String,
                       userName: String,
                       locale: String,
                       email: Option[String] = None
                      )
