package com.ubirch.util.http.response

import com.ubirch.util.model.{JsonErrorResponse, JsonResponse}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCode}

/**
  * Created by derMicha on 28/10/16.
  * This util sh
  */
trait ResponseUtil {

  def response(message: String): HttpResponse = {
    response(JsonResponse(message = message))
  }

  def response(response: JsonResponse): HttpResponse = {
    HttpResponse(status = OK, entity = HttpEntity(ContentTypes.`application/json`, response.toJsonString))
  }

  def requestErrorResponse(errorType: String, errorMessage: String): HttpResponse = {
    requestErrorResponse(JsonErrorResponse(errorType = errorType, errorMessage = errorMessage))
  }

  def requestErrorResponse(response: JsonErrorResponse, status: StatusCode = BadRequest): HttpResponse = {
    HttpResponse(status = status, entity = HttpEntity(ContentTypes.`application/json`, response.toJsonString))
  }

  def serverErrorResponse(errorType: String, errorMessage: String): HttpResponse = {
    serverErrorResponse(JsonErrorResponse(errorType = errorType, errorMessage = errorMessage))
  }

  def serverErrorResponse(response: JsonErrorResponse, status: StatusCode = InternalServerError): HttpResponse = {
    HttpResponse(status = status, entity = HttpEntity(ContentTypes.`application/json`, response.toJsonString))
  }

}
