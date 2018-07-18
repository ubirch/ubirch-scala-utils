package com.ubirch.util.camel

/**
  * author: cvandrei
  * since: 2017-10-09
  */
trait SqsConfig {

  private def toStringStringMap(mapWithOption: Map[String, Option[Any]]): Map[String, String] = {

    mapWithOption.filter(_._2.isDefined).map {
      case(key, optValue) => key -> optValue.get.toString
    }

  }

  protected def parameterMap(): Map[String, Option[Any]]

  final def parameterMapAsString(): String = {

    toStringStringMap(parameterMap())
      .map(_.productIterator.mkString("="))
      .mkString("&")

  }

  protected final def uri(queue: String): String = {

    val baseUrl = s"aws-sqs://$queue"
    if (parameterMap().isEmpty) {

      baseUrl

    } else {

      val paramString = parameterMapAsString()
      s"$baseUrl?$paramString"

    }

  }

  protected def endpointUri(): String

}

case class SqsConfigConsumer(queue: String,
                             region: String,
                             queueOwnerId: String,
                             accessKey: String,
                             secretAccessKey: String,
                             concurrentConsumers: Int = 2,
                             maxMessagesPerPoll: Option[Int] = None,
                             additionalParameters: Map[String, Any] = Map.empty
                            ) extends SqsConfig {

  def parameterMap(): Map[String, Option[Any]] = {

    // keys need to be exactly as used in SQS URIs
    Map(
      "region" -> Some(region),
      "queueOwnerAWSAccountId" -> Some(queueOwnerId),
      "accessKey" -> Some(accessKey),
      "secretKey" -> Some(secretAccessKey),
      "concurrentConsumers" -> Some(concurrentConsumers),
      "maxMessagesPerPoll" -> maxMessagesPerPoll
    ) ++ additionalParameters.map {
      case (key, value) => key -> Some(value)
    }

  }

  def endpointUri(): String = uri(queue)

}

case class SqsConfigProducer(queue: String,
                             region: String,
                             queueOwnerId: String,
                             accessKey: String,
                             secretAccessKey: String,
                             waitTimeSeconds: Option[Int] = None,
                             delaySeconds: Option[Int] = None,
                             additionalParameters: Map[String, Any] = Map.empty
                            ) extends SqsConfig {

  def parameterMap(): Map[String, Option[Any]] = {

    // keys need to be exactly as used in SQS URIs
    Map(
      "region" -> Some(region),
      "queueOwnerAWSAccountId" -> Some(queueOwnerId),
      "accessKey" -> Some(accessKey),
      "secretKey" -> Some(secretAccessKey),
      "waitTimeSeconds" -> waitTimeSeconds,
      "delaySeconds" -> delaySeconds
    ) ++ additionalParameters.map {
      case (key, value) => key -> Some(value)
    }

  }

  def endpointUri(): String = uri(queue)

}
