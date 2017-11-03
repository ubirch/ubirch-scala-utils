package com.ubirch.util.camel

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2017-10-09
  */
class CamelActorUtilSpec extends FeatureSpec
  with CamelActorUtil
  with Matchers {

  feature("sqsEndpointConsumer()") {

    scenario("without _maxMessagesPerPoll_, without _concurrentConsumers_") {

      // prepare
      val config = SqsConfig(
        queue = "queueName",
        region = "aws-region",
        queueOwnerId = "123456",
        accessKey = "myAccessKey",
        secretAccessKey = "mySecretKey"
      )

      // test
      val result = sqsEndpointConsumer(config)

      // verify
      val expected = s"aws-sqs://queueName?region=aws-region&queueOwnerAWSAccountId=123456&accessKey=myAccessKey&secretKey=mySecretKey&concurrentConsumers=2"
      result should be(expected)

    }

    scenario("without _maxMessagesPerPoll_, with _concurrentConsumers_ < default") {

      // prepare
      val config = SqsConfig(
        queue = "queueName",
        region = "aws-region",
        queueOwnerId = "123456",
        accessKey = "myAccessKey",
        secretAccessKey = "mySecretKey",
        concurrentConsumers = 1
      )

      // test
      val result = sqsEndpointConsumer(config)

      // verify
      val expected = s"aws-sqs://queueName?region=aws-region&queueOwnerAWSAccountId=123456&accessKey=myAccessKey&secretKey=mySecretKey&concurrentConsumers=1"
      result should be(expected)

    }

    scenario("without _maxMessagesPerPoll_, with _concurrentConsumers_ = default") {

      // prepare
      val config = SqsConfig(
        queue = "queueName",
        region = "aws-region",
        queueOwnerId = "123456",
        accessKey = "myAccessKey",
        secretAccessKey = "mySecretKey",
        concurrentConsumers = 2
      )

      // test
      val result = sqsEndpointConsumer(config)

      // verify
      val expected = s"aws-sqs://queueName?region=aws-region&queueOwnerAWSAccountId=123456&accessKey=myAccessKey&secretKey=mySecretKey&concurrentConsumers=2"
      result should be(expected)

    }

    scenario("without _maxMessagesPerPoll_, with _concurrentConsumers_ > default") {

      // prepare
      val config = SqsConfig(
        queue = "queueName",
        region = "aws-region",
        queueOwnerId = "123456",
        accessKey = "myAccessKey",
        secretAccessKey = "mySecretKey",
        concurrentConsumers = 3
      )

      // test
      val result = sqsEndpointConsumer(config)

      // verify
      val expected = s"aws-sqs://queueName?region=aws-region&queueOwnerAWSAccountId=123456&accessKey=myAccessKey&secretKey=mySecretKey&concurrentConsumers=3"
      result should be(expected)

    }

    scenario("with _maxMessagesPerPoll_, without _concurrentConsumers_") {

      // prepare
      val config = SqsConfig(
        queue = "queueName",
        region = "aws-region",
        queueOwnerId = "123456",
        accessKey = "myAccessKey",
        secretAccessKey = "mySecretKey",
        maxMessagesPerPoll = Some(100)
      )

      // test
      val result = sqsEndpointConsumer(config)

      // verify
      val expected = s"aws-sqs://queueName?region=aws-region&queueOwnerAWSAccountId=123456&accessKey=myAccessKey&secretKey=mySecretKey&concurrentConsumers=2&maxMessagesPerPoll=100"
      result should be(expected)

    }

  }

}
