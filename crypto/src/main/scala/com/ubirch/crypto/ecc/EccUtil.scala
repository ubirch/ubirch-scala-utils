package com.ubirch.crypto.ecc

import java.security._
import java.util.Base64

import com.ubirch.crypto.codec.CodecUtil
import net.i2p.crypto.eddsa.spec.{EdDSANamedCurveTable, EdDSAParameterSpec, EdDSAPrivateKeySpec, EdDSAPublicKeySpec}
import net.i2p.crypto.eddsa.{EdDSAEngine, EdDSAPrivateKey, EdDSAPublicKey, KeyPairGenerator}

/**
  * Created by derMicha on 19/05/17.
  */
object EccUtil {

  final private lazy val DEFAULTHASHALGORITHM = "SHA-512"

  final private lazy val DEFAULTECCCURVE = EdDSANamedCurveTable.CURVE_ED25519_SHA512

  final private lazy val EDDSASPEC: EdDSAParameterSpec = EdDSANamedCurveTable.getByName(DEFAULTECCCURVE)

  /**
    *
    * @param publicKey Base64 encoded ECC public key
    * @param signature
    * @param payload
    */
  def validateSignature(publicKey: String, signature: String, payload: String): Boolean = {
    validateSignature(publicKey, signature, payload.getBytes)
  }

  /**
    *
    * @param publicKey Base64 encoded ECC public key
    * @param signature
    * @param payload
    */
  def validateSignature(publicKey: String, signature: String, payload: Array[Byte]): Boolean = {

    val edsaPubKey = decodePublicKey(publicKey)

    val signatureBytes: Array[Byte] = Base64.getDecoder.decode(signature)
    val eddsaSignature: EdDSAEngine = new EdDSAEngine(MessageDigest.getInstance(DEFAULTHASHALGORITHM))

    eddsaSignature.initVerify(edsaPubKey)
    eddsaSignature.update(payload)
    eddsaSignature.verify(signatureBytes) match {
      case true =>
        true
      case _ =>
        false
    }
  }

  /**
    *
    * @param privateKey base64 encoded ECC private key
    * @param payload    data as a String
    * @return
    */
  def signPayload(privateKey: String, payload: String): String = {

    val sgr: Signature = new EdDSAEngine(MessageDigest.getInstance(DEFAULTHASHALGORITHM))

    val eddsaPrivateKey = decodePrivateKey(privateKey)

    sgr.initSign(eddsaPrivateKey)
    sgr.update(payload.getBytes)
    val signature: Array[Byte] = sgr.sign

    Base64.getEncoder.encodeToString(signature)
  }

  def generateEccKeyPair: (PublicKey, PrivateKey) = {
    val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName(DEFAULTECCCURVE)
    val kpg: KeyPairGenerator = new KeyPairGenerator

    kpg.initialize(spec, new SecureRandom(java.util.UUID.randomUUID.toString.getBytes))

    val kp: KeyPair = kpg.generateKeyPair

    val sKey: PrivateKey = kp.getPrivate
    val pKey: PublicKey = kp.getPublic

    (pKey, sKey)
  }

  /**
    *
    * @return base 64 encoded (PublicKey, PrivateKey)
    */
  def generateEccKeyPairEncoded: (String, String) = {
    val (pKey, sKey) = generateEccKeyPair
    val encodedPublicKey = encodePublicKey(pKey)
    val encodedPrivateKey = encodePrivateKey(sKey)

    (encodedPublicKey, encodedPrivateKey)
  }

  /**
    *
    * @param publicKey EdDSA PublicKey
    * @return Base64 encoded ECC PrivateKey
    */
  def encodePublicKey(publicKey: PublicKey): String = {
    val publicKeyBytes = publicKey.getEncoded
    Base64.getEncoder.encodeToString(publicKeyBytes)
  }

  /**
    *
    * @param publicKey Base64 encoded ECC PublicKey
    * @return EdDSA PublicKey
    */
  def decodePublicKey(publicKey: String): PublicKey = {
    //    val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName("ed25519-sha-512")

    CodecUtil.multiDecoder(publicKey) match {
      case Some(decoded) =>
        val pubKeyBytes: Array[Byte] = decoded.length match {
          case 32 => decoded
          case _ => EdDSAPublicKey.decode(decoded)
        }
        val pubKey: EdDSAPublicKeySpec = new EdDSAPublicKeySpec(pubKeyBytes, EDDSASPEC)
        new EdDSAPublicKey(pubKey)
      case None =>
        throw new IllegalArgumentException(s"invalid pubkey: $publicKey")
    }
  }

  /**
    *
    * @param privateKey EdDSA PublicKey
    * @return Base64 encoded ECC PrivateKey
    */
  def encodePrivateKey(privateKey: PrivateKey): String = {
    val privateKeyBytes = privateKey.getEncoded
    Base64.getEncoder.encodeToString(privateKeyBytes)
  }

  /**
    *
    * @param privateKey Base64 encoded ECC PrivateKey
    * @return EdDSA PrivateKey
    */
  def decodePrivateKey(privateKey: String): PrivateKey = {

    val decoded = Base64.getDecoder.decode(privateKey)
    val privKeyBytes: Array[Byte] = decoded.length match {
      case 34 => decoded
      case _ => EdDSAPrivateKey.decode(decoded)
    }

    val privKeySpec: EdDSAPrivateKeySpec = new EdDSAPrivateKeySpec(privKeyBytes, EDDSASPEC)
    new EdDSAPrivateKey(privKeySpec)
  }
}
