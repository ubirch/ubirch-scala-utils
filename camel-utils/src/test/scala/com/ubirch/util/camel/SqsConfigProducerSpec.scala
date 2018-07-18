package com.ubirch.util.camel

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2018-07-18
  */
class SqsConfigProducerSpec extends FeatureSpec
  with Matchers {

  feature("parameterMap()") {

    scenario("only mandatory and defaults set --> minimum viable parameter map") {

      // prepare
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret"
      )

      // test
      val result = producerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(producerConfig.region),
        "queueOwnerAWSAccountId" -> Some(producerConfig.queueOwnerId),
        "accessKey" -> Some(producerConfig.accessKey),
        "secretKey" -> Some(producerConfig.secretAccessKey),
        "waitTimeSeconds" -> None,
        "delaySeconds" -> None
      )
      result shouldBe expected

    }

    scenario("mandatory values and `waitTimeSeconds` = Some --> minimum viable map plus `waitTimeSeconds`") {

      // prepare
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        waitTimeSeconds = Some(15)
      )

      // test
      val result = producerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(producerConfig.region),
        "queueOwnerAWSAccountId" -> Some(producerConfig.queueOwnerId),
        "accessKey" -> Some(producerConfig.accessKey),
        "secretKey" -> Some(producerConfig.secretAccessKey),
        "waitTimeSeconds" -> producerConfig.waitTimeSeconds,
        "delaySeconds" -> None
      )
      result shouldBe expected

    }

    scenario("mandatory values and `delaySeconds` = Some --> minimum viable map plus `delaySeconds`") {

      // prepare
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        delaySeconds = Some(5)
      )

      // test
      val result = producerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(producerConfig.region),
        "queueOwnerAWSAccountId" -> Some(producerConfig.queueOwnerId),
        "accessKey" -> Some(producerConfig.accessKey),
        "secretKey" -> Some(producerConfig.secretAccessKey),
        "waitTimeSeconds" -> None,
        "delaySeconds" -> producerConfig.delaySeconds
      )
      result shouldBe expected

    }

    scenario("mandatory values, defaults and `additionalParameters` != empty--> minimum viable parameter map plus additional parameters") {

      // prepare
      val extraParams = Map(
        "foo" -> "a",
        "bar" -> -27
      )
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        additionalParameters = extraParams
      )
      producerConfig.additionalParameters should not be empty

      // test
      val result = producerConfig.parameterMap()

      // verify
      val expected = Map(
        "region" -> Some(producerConfig.region),
        "queueOwnerAWSAccountId" -> Some(producerConfig.queueOwnerId),
        "accessKey" -> Some(producerConfig.accessKey),
        "secretKey" -> Some(producerConfig.secretAccessKey),
        "waitTimeSeconds" -> None,
        "delaySeconds" -> None,
        "foo" -> Some("a"),
        "bar" -> Some(-27)
      )
      result shouldBe expected

    }

  }

  feature("parameterMapAsString()") {

    scenario("only mandatory and defaults set --> minimum viable parameter map") {

      // prepare
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret"
      )

      // test
      val result = producerConfig.parameterMapAsString()

      // verify
      val expected = s"accessKey=${producerConfig.accessKey}" +
        s"&region=${producerConfig.region}" +
        s"&secretKey=${producerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${producerConfig.queueOwnerId}"
      result shouldBe expected

    }

    scenario("mandatory values and `waitTimeSeconds` = Some --> minimum viable map plus `waitTimeSeconds`") {

      // prepare
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        waitTimeSeconds = Some(15)
      )

      // test
      val result = producerConfig.parameterMapAsString()

      // verify
      val expected = s"waitTimeSeconds=${producerConfig.waitTimeSeconds.get}" +
        s"&accessKey=${producerConfig.accessKey}" +
        s"&region=${producerConfig.region}" +
        s"&secretKey=${producerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${producerConfig.queueOwnerId}"
      result shouldBe expected

    }

    scenario("mandatory values and `delaySeconds` = Some --> minimum viable map plus `delaySeconds`") {

      // prepare
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        delaySeconds = Some(5)
      )

      // test
      val result = producerConfig.parameterMapAsString()

      // verify
      val expected = s"accessKey=${producerConfig.accessKey}" +
        s"&region=${producerConfig.region}" +
        s"&secretKey=${producerConfig.secretAccessKey}" +
        s"&delaySeconds=${producerConfig.delaySeconds.get}" +
        s"&queueOwnerAWSAccountId=${producerConfig.queueOwnerId}"
      result shouldBe expected

    }

    scenario("mandatory values, defaults and `additionalParameters` != empty--> minimum viable parameter map plus additional parameters") {

      // prepare
      val extraParams = Map(
        "foo" -> "a",
        "bar" -> -27
      )
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        additionalParameters = extraParams
      )
      producerConfig.additionalParameters should not be empty

      // test
      val result = producerConfig.parameterMapAsString()

      // verify
      val expected = s"accessKey=${producerConfig.accessKey}" +
        "&bar=-27" +
        s"&region=${producerConfig.region}" +
        s"&secretKey=${producerConfig.secretAccessKey}" +
        s"&queueOwnerAWSAccountId=${producerConfig.queueOwnerId}" +
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
      val producerConfig = SqsConfigProducer(
        queue = "queue_name",
        region = "EU_WEST_1",
        queueOwnerId = "12341234",
        accessKey = "1234-access-0987",
        secretAccessKey = "super-top-secret",
        waitTimeSeconds = Some(15),
        delaySeconds = Some(5),
        additionalParameters = extraParams
      )

      producerConfig.waitTimeSeconds shouldBe defined
      producerConfig.delaySeconds shouldBe defined
      producerConfig.additionalParameters should not be empty

      // test && verify
      producerConfig.endpointUri() shouldBe s"aws-sqs://${producerConfig.queue}?${producerConfig.parameterMapAsString()}"

    }

  }

}
