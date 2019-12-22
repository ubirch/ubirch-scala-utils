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
  private val eccUtil = new EccUtil()

  private val emptyPayload = ""
  private val payload = "Hello World!"
  private val binPayload = payload.getBytes

  private val (publicKeyValid_1, privateKeyValid_1) = eccUtil.generateEccKeyPair
  private val publicKeyValidB64_1 = eccUtil.encodePublicKey(publicKeyValid_1)
  private val privateKeyValidB64_1 = eccUtil.encodePrivateKey(privateKeyValid_1)

  private val (publicKeyValid_2, privateKeyValid_2) = eccUtil.generateEccKeyPair
  private val publicKeyValidB64_2 = eccUtil.encodePublicKey(publicKeyValid_2)
  private val privateKeyValidB64_2 = eccUtil.encodePrivateKey(privateKeyValid_2)

  private val publicKeyInvalid = "invalidPubKey"
  private val privateKeyInvalid = "invalidPrivateKey"

  private val signaturePayloadValid = eccUtil.signPayload(privateKeyValidB64_1, payload)
  private val signatureEmptyPayloadValid = eccUtil.signPayload(privateKeyValidB64_1, emptyPayload)
  private val signatureInvalid = "invalidSignature"

  feature("ECCUtil Tests") {

    scenario("validate with valid payload(empty)/pubKey/signature") {
      logger.info(s"publicKey: $publicKeyValidB64_1")
      logger.info(s"privateKey: $privateKeyValidB64_1")
      logger.info(s"signature: $signatureEmptyPayloadValid")
      logger.info(s"payload: >>$emptyPayload<<")

      eccUtil.validateSignature(publicKeyValidB64_1, signatureEmptyPayloadValid, emptyPayload) shouldBe true
    }

    scenario("validate with valid payload/pubKey/signature") {
      logger.info(s"publicKey: $publicKeyValidB64_1")
      logger.info(s"privateKey: $privateKeyValidB64_1")
      logger.info(s"signature: $signaturePayloadValid")
      logger.info(s"payload: >>$payload<<")

      eccUtil.validateSignature(publicKeyValidB64_1, signaturePayloadValid, payload) shouldBe true
    }

    scenario("validate with valid payload/signature and invalid pubKey") {
      logger.info(s"publicKey: $publicKeyInvalid")
      logger.info(s"signature: $signaturePayloadValid")
      logger.info(s"payload: >>$payload<<")

      assertThrows[java.lang.IllegalArgumentException] {
        eccUtil.validateSignature(publicKeyInvalid, signaturePayloadValid, payload)
      }
    }

    scenario("validate with valid payload/signature and wrong pubKey") {
      logger.info(s"publicKey: $publicKeyValidB64_2")
      logger.info(s"signature: $signaturePayloadValid")
      logger.info(s"payload: >>$payload<<")


      eccUtil.validateSignature(publicKeyValidB64_2, signaturePayloadValid, payload) shouldBe false

    }

    scenario("sign haseh bin data") {
      logger.info(s"publicKey: $publicKeyValidB64_1")
      logger.info(s"privateKey: $privateKeyValidB64_1")
      logger.info(s"payload: >>$payload<<")

      val signature = eccUtil.signPayloadSha512(eddsaPrivateKey = privateKeyValid_1,
        payload = binPayload
      )

      val validation = eccUtil.validateSignatureSha512(publicKey = publicKeyValidB64_1,
        signature = signature,
        payload = binPayload
      )

      validation shouldBe true

    }

  }

}
