package com.ubirch.crypto.ecc

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.scalatest.{FeatureSpec, Matchers}

/**
  * Created by derMicha on 19/05/17.
  */
class EccUtilSpec
  extends FeatureSpec
    with StrictLogging
    with Matchers {

  val emptyPayload = ""
  val payload = "Hello World!"

  val (publicKeyValid1, privateKeyValid1) = EccUtil.generateEccKeyPairEncoded

  val (publicKeyValid2, privateKeyValid2) = EccUtil.generateEccKeyPairEncoded

  val publicKeyInvalid = "invalidPubKey"
  val privateKeyInvalid = "invalidPrivateKey"

  val signaturePayloadValid = EccUtil.signPayload(privateKeyValid1, payload)
  val signatureEmptyPayloadValid = EccUtil.signPayload(privateKeyValid1, emptyPayload)
  val signatureInvalid = "invalidSignature"

  feature("ECCUtil Tests") {

    scenario("validate with valid payload(empty)/pubKey/signature") {
      logger.info(s"publicKey: $publicKeyValid1")
      logger.info(s"privateKey: $privateKeyValid1")
      logger.info(s"signature: $signatureEmptyPayloadValid")
      logger.info(s"payload: >>$emptyPayload<<")

      EccUtil.validateSignature(publicKeyValid1, signatureEmptyPayloadValid, emptyPayload) shouldBe true
    }

    scenario("validate with valid payload/pubKey/signature") {
      logger.info(s"publicKey: $publicKeyValid1")
      logger.info(s"privateKey: $privateKeyValid1")
      logger.info(s"signature: $signaturePayloadValid")
      logger.info(s"payload: >>$payload<<")

      EccUtil.validateSignature(publicKeyValid1, signaturePayloadValid, payload) shouldBe true
    }

    scenario("validate with valid payload/signature and invalid pubKey") {
      logger.info(s"publicKey: $publicKeyInvalid")
      logger.info(s"privateKey: $privateKeyValid1")
      logger.info(s"signature: $signaturePayloadValid")
      logger.info(s"payload: >>$payload<<")

      assertThrows[java.lang.IllegalArgumentException] {
        EccUtil.validateSignature(publicKeyInvalid, signaturePayloadValid, payload)
      }
    }

    scenario("validate with valid payload/signature and wrong pubKey") {
      logger.info(s"publicKey: $publicKeyValid2")
      logger.info(s"privateKey: $privateKeyValid1")
      logger.info(s"signature: $signaturePayloadValid")
      logger.info(s"payload: >>$payload<<")


      EccUtil.validateSignature(publicKeyValid2, signaturePayloadValid, payload) shouldBe false

    }

  }

}
