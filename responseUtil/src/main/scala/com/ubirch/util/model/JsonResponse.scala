package com.ubirch.util.model

/**
  * author: cvandrei
  * since: 2016-09-20
  */
case class JsonResponse(version: String = "1.0",
                        status: String = "OK",
                        message: String
                       ) {

  def toJsonString: String = {
    s"""{
       |  "version" : "$version",
       |  "status" : "$status",
       |  "message": "$message"
       |}""".stripMargin
  }

}
