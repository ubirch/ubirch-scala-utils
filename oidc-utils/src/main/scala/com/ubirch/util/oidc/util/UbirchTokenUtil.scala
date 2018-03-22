package com.ubirch.util.oidc.util

import com.ubirch.crypto.hash.HashUtil

/**
  * author: cvandrei
  * since: 2018-03-15
  */
object UbirchTokenUtil {

  /**
    * provider as used in auth- and user-service.
    */
  val providerId = "ubirchToken"

  /**
    * Delimiter used to separate the different parts of a ubirch token.
    */
  val delim = "::"

  /**
    * Default signature until we specify how the signature is being calculated.
    */
  val defaultSignature = "to-be-specified"

  /**
    * Calculates a ubirch token.
    *
    * NOTE: It's signature section has not been specified yet though and will always be set to a default value as it may
    * be empty.
    *
    * @param context    context a ubirch token is used in
    * @param email      email address of user the token belongs to
    * @param privateKey (currently ignored) used to calculate signature
    * @return
    */
  def toUbirchToken(context: String,
                    email: String,
                    privateKey: Option[String] = None
                   ): String = {

    val emailHash = hashEmail(email)

    val signature = if (privateKey.isEmpty) {
      defaultSignature
    } else {
      // TODO specify how we calculate the signature
      //EccUtil.signPayload(privateKey = privateKey.get, payload = email)
      defaultSignature
    }

    s"$context$delim$emailHash$delim$signature"

  }

  def hashEmail(email: String): String = HashUtil.sha256HexString(email)

}
