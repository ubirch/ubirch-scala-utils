package com.ubirch.util.http.response

import com.ubirch.util.json.MyJsonProtocol
import com.ubirch.util.model.{JsonErrorResponse, JsonResponse}

import org.json4s.native.Serialization.write

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCode}

/**
  * Created by derMicha on 28/10/16.
  * This util sh
  */
trait ResponseUtil extends MyJsonProtocol {

  /**
    * Creates a response with the given http status and the "responseObject" converted to JSON.
    *
    * @param responseObject any object that can be converted to JSON
    * @param status         HTTP status code of the response (defaults to 200)
    * @return
    */
  def response(responseObject: AnyRef, status: StatusCode = OK): HttpResponse = {

    HttpResponse(
      status = status,
      entity = HttpEntity(
        ContentTypes.`application/json`,
        write(responseObject)
      )
    )

  }

  def response(message: String): HttpResponse = {
    response(JsonResponse(message = message))
  }

  def requestErrorResponse(errorType: String, errorMessage: String): HttpResponse = {
    requestErrorResponse(JsonErrorResponse(errorType = errorType, errorMessage = errorMessage))
  }

  /**
    * Creates response with the given status and the "responseObject" converted to JSON.
    *
    * @param responseObject any object that can be converted to JSON
    * @param status         HTTP status code of the response (defaults to 400)
    * @return
    */
  def requestErrorResponse(responseObject: AnyRef, status: StatusCode = BadRequest): HttpResponse = {
    response(responseObject, status)
  }

  def serverErrorResponse(errorType: String, errorMessage: String): HttpResponse = {
    serverErrorResponse(JsonErrorResponse(errorType = errorType, errorMessage = errorMessage))
  }

  /**
    * Creates response with the given status and the "responseObject" converted to JSON.
    *
    * @param responseObject any object that can be converted to JSON
    * @param status         HTTP status code of the response (defaults to 500)
    * @return
    */
  def serverErrorResponse(responseObject: AnyRef, status: StatusCode = InternalServerError): HttpResponse = {
    response(responseObject, status)
  }

}
