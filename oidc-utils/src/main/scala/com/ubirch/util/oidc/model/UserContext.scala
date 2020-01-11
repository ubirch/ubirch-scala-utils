package com.ubirch.util.oidc.model

/**
  * author: cvandrei
  * since: 2017-03-23
  */
case class UserContext(context: String,
                       providerId: String,
                       externalUserId: String,
                       userId: String,
                       userName: String,
                       locale: String,
                       email: Option[String] = None,
                       hasPubKey: Int
                      )
