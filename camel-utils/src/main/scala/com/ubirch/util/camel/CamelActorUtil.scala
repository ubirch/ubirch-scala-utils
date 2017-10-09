package com.ubirch.util.camel

/**
  * author: cvandrei
  * since: 2017-10-09
  */
object CamelActorUtil {

  def sqsEndpointConsumer(config: SqsConfig): String = {

    val endpoint = s"aws-sqs://${config.queue}?" +
      s"region=${config.region}" +
      s"&queueOwnerAWSAccountId=${config.queueOwnerId}" +
      s"&accessKey=${config.accessKey}" +
      s"&secretKey=${config.secretAccessKey}" +
      s"&concurrentConsumers=${config.concurrentConsumers}"

    config.maxMessagesPerPoll match {
      case None => endpoint
      case Some(maxMessagesPerPoll) => s"$endpoint&maxMessagesPerPoll=$maxMessagesPerPoll"
    }

  }

}
