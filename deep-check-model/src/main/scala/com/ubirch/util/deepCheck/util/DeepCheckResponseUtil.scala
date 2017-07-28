package com.ubirch.util.deepCheck.util

import com.ubirch.util.deepCheck.model.DeepCheckResponse

/**
  * author: cvandrei
  * since: 2017-07-28
  */
object DeepCheckResponseUtil {

  /**
    * Accept a [[DeepCheckResponse]], take all it's messages and add a service specific prefix to each.
    *
    * @param servicePrefix e.g. "user-service"
    * @param res           the [[DeepCheckResponse]] whose message will get a service specific prefix
    * @return converted [[DeepCheckResponse]]
    */
  def addServicePrefix(servicePrefix: String, res: DeepCheckResponse): DeepCheckResponse = {

    // TODO unit tests
    val m = res.messages map (s"[$servicePrefix] " + _)
    res.copy(messages = m)

  }

  /**
    * Merges a number of [[DeepCheckResponse]]s into one.
    *
    * @param responses sequence of [[DeepCheckResponse]]s to merge
    * @return all responses merged into one
    */
  def merge(responses: Seq[DeepCheckResponse]): DeepCheckResponse = {

    // TODO unit tests
    val resultingStatus: Boolean = responses.forall(n => n.status)
    val resultingMessages: Seq[String] = responses.foldLeft(Nil: Seq[String]) { (m: Seq[String], n: DeepCheckResponse) =>
      m ++ n.messages
    }

    DeepCheckResponse(
      status = resultingStatus,
      messages = resultingMessages
    )

  }

}
