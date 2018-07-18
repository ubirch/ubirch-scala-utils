package com.ubirch.util.camel

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2018-07-18
  */
class SqsConfigConsumerSpec extends FeatureSpec
  with Matchers {

  feature("parameterMap()") {

    scenario("only mandatory and defaults set --> minimum viable parameter map") {

      // prepare
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret"
      )

      // test
      val result = consumerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(consumerConfig.region),
        "queueOwnerAWSAccountId" -> Some(consumerConfig.queueOwnerId),
        "accessKey" -> Some(consumerConfig.accessKey),
        "secretKey" -> Some(consumerConfig.secretAccessKey),
        "concurrentConsumers" -> Some(consumerConfig.concurrentConsumers),
        "maxMessagesPerPoll" -> None
      )
      result shouldBe expected

    }

    scenario("mandatory values and `concurrentConsumers` != default --> minimum viable map with concurrentConsumers != default") {

      // prepare
      val consumerConfigWithDefaults = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret"
      )
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        concurrentConsumers = consumerConfigWithDefaults.concurrentConsumers + 1
      )

      // test
      val result = consumerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(consumerConfig.region),
        "queueOwnerAWSAccountId" -> Some(consumerConfig.queueOwnerId),
        "accessKey" -> Some(consumerConfig.accessKey),
        "secretKey" -> Some(consumerConfig.secretAccessKey),
        "concurrentConsumers" -> Some(consumerConfig.concurrentConsumers),
        "maxMessagesPerPoll" -> None
      )
      result shouldBe expected

    }

    scenario("mandatory values, defaults and `maxMessagesPerPoll` = Some --> minimum viable parameter map with maxMessagesPerPoll = Some") {

      // prepare
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        maxMessagesPerPoll = Some(3)
      )
      consumerConfig.maxMessagesPerPoll shouldBe defined

      // test
      val result = consumerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(consumerConfig.region),
        "queueOwnerAWSAccountId" -> Some(consumerConfig.queueOwnerId),
        "accessKey" -> Some(consumerConfig.accessKey),
        "secretKey" -> Some(consumerConfig.secretAccessKey),
        "concurrentConsumers" -> Some(consumerConfig.concurrentConsumers),
        "maxMessagesPerPoll" -> consumerConfig.maxMessagesPerPoll
      )
      result shouldBe expected

    }

    scenario("mandatory values, defaults and `additionalParameters` != empty--> minimum viable parameter map plus additional parameters") {

      // prepare
      val extraParams = Map(
        "foo" -> "a",
        "bar" -> -27
      )
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        additionalParameters = extraParams
      )
      consumerConfig.additionalParameters should not be empty

      // test
      val result = consumerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(consumerConfig.region),
        "queueOwnerAWSAccountId" -> Some(consumerConfig.queueOwnerId),
        "accessKey" -> Some(consumerConfig.accessKey),
        "secretKey" -> Some(consumerConfig.secretAccessKey),
        "concurrentConsumers" -> Some(consumerConfig.concurrentConsumers),
        "maxMessagesPerPoll" -> consumerConfig.maxMessagesPerPoll,
        "foo" -> Some("a"),
        "bar" -> Some(-27)
      )
      result shouldBe expected

    }

  }

  feature("parameterMapAsString()") {

    scenario("only mandatory and defaults set --> minimum viable parameter map") {

      // prepare
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret"
      )

      // test
      val result = consumerConfig.parameterMapAsString()

      // verify
      val expected = s"accessKey=${consumerConfig.accessKey}" +
        s"&region=${consumerConfig.region}" +
        s"&secretKey=${consumerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${consumerConfig.queueOwnerId}" +
        s"&concurrentConsumers=${consumerConfig.concurrentConsumers}"
      result shouldBe expected

    }

    scenario("mandatory values and `concurrentConsumers` != default --> minimum viable map with concurrentConsumers != default") {

      // prepare
      val consumerConfigWithDefaults = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret"
      )
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        concurrentConsumers = consumerConfigWithDefaults.concurrentConsumers + 1
      )

      // test
      val result = consumerConfig.parameterMapAsString()

      // verify
      val expected = s"accessKey=${consumerConfig.accessKey}" +
        s"&region=${consumerConfig.region}" +
        s"&secretKey=${consumerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${consumerConfig.queueOwnerId}" +
        s"&concurrentConsumers=${consumerConfig.concurrentConsumers}"
      result shouldBe expected

    }

    scenario("mandatory values, defaults and `maxMessagesPerPoll` = Some --> minimum viable parameter map with maxMessagesPerPoll = Some") {

      // prepare
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        maxMessagesPerPoll = Some(3)
      )
      consumerConfig.maxMessagesPerPoll shouldBe defined

      // test
      val result = consumerConfig.parameterMapAsString()

      // verify
      val expected = s"maxMessagesPerPoll=${consumerConfig.maxMessagesPerPoll.get}" +
        s"&accessKey=${consumerConfig.accessKey}" +
        s"&region=${consumerConfig.region}" +
        s"&secretKey=${consumerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${consumerConfig.queueOwnerId}" +
        s"&concurrentConsumers=${consumerConfig.concurrentConsumers}"
      result shouldBe expected

    }

    scenario("mandatory values, defaults and `additionalParameters` != empty--> minimum viable parameter map plus additional parameters") {

      // prepare
      val extraParams = Map(
        "foo" -> "a",
        "bar" -> -27
      )
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        additionalParameters = extraParams
      )
      consumerConfig.additionalParameters should not be empty

      // test
      val result = consumerConfig.parameterMapAsString()

      // verify
      val expected = s"accessKey=${consumerConfig.accessKey}" +
        "&bar=-27" +
        s"&region=${consumerConfig.region}" +
        s"&secretKey=${consumerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${consumerConfig.queueOwnerId}" +
        s"&concurrentConsumers=${consumerConfig.concurrentConsumers}" +
        "&foo=a"
      result shouldBe expected

    }

  }

  feature("endpointUri()") {

    scenario("all fields set --> endpointUri with all fields") {

      // prepare
      val extraParams = Map(
        "foo" -> "a",
        "bar" -> -27
      )
      val consumerConfig = SqsConfigConsumer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        maxMessagesPerPoll = Some(5),
        additionalParameters = extraParams
      )

      consumerConfig.maxMessagesPerPoll shouldBe defined
      consumerConfig.additionalParameters should not be empty

      // test && verify
      consumerConfig.endpointUri() shouldBe s"aws-sqs://${consumerConfig.queue}?${consumerConfig.parameterMapAsString()}"

    }

  }

}
