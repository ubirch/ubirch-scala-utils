package com.ubirch.crypto.ecc

import java.security._
import java.util.Base64

import com.ubirch.crypto.codec.CodecUtil
import net.i2p.crypto.eddsa.spec.{EdDSANamedCurveTable, EdDSAParameterSpec, EdDSAPrivateKeySpec, EdDSAPublicKeySpec}
import net.i2p.crypto.eddsa.{EdDSAEngine, EdDSAPrivateKey, EdDSAPublicKey, KeyPairGenerator}
import org.apache.commons.codec.binary.Hex

/**
  * Created by derMicha on 19/05/17.
  */
object EccUtil {

  final private lazy val DEFAULTHASHALGORITHM = "SHA-512"

  final private lazy val DEFAULTECCCURVE = EdDSANamedCurveTable.CURVE_ED25519_SHA512

  final private lazy val EDDSASPEC: EdDSAParameterSpec = EdDSANamedCurveTable.getByName(DEFAULTECCCURVE)

  final val encHex = "hex"

  final val encB64 = "b64"

  final private val edDsaEng: EdDSAEngine = new EdDSAEngine(MessageDigest.getInstance(DEFAULTHASHALGORITHM))

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

    edDsaEng.initVerify(edsaPubKey)
    edDsaEng.update(payload)
    edDsaEng.verify(signatureBytes) match {
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
    * @return Base64 encoded signature
    */
  def signPayload(privateKey: String, payload: String): String = {
    signPayload(privateKey, payload.getBytes)
  }

  /**
    *
    * @param privateKey base64 encoded ECC private key
    * @param payload    data as Array[Byte]
    * @return Base64 encoded signature
    */

  def signPayload(privateKey: String, payload: Array[Byte]): String = {
    val eddsaPrivateKey = decodePrivateKey(privateKey)
    signPayload(eddsaPrivateKey, payload)
  }

  /**
    *
    * @param eddsaPrivateKey ECC private key
    * @param payload         as Array[Byte]
    * @param encoding        EccUtil.encHex | EccUtil.encB64 -> encode signature hex/base64
    * @return string encoded signature
    */
  def signPayload(eddsaPrivateKey: PrivateKey, payload: Array[Byte], encoding: String = encB64): String = {

    edDsaEng.initSign(eddsaPrivateKey)
    edDsaEng.update(payload)
    val signature: Array[Byte] = edDsaEng.sign

    if (encoding.equals(encHex))
      Hex.encodeHexString(signature)
    else
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
  def decodePublicKey(publicKey: String): EdDSAPublicKey = {
    //    val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName("ed25519-sha-512")

    CodecUtil.multiDecoder(publicKey) match {
      case Some(decoded) =>
        decodePublicKey(decoded)
      case None =>
        throw new IllegalArgumentException(s"invalid pubkey: $publicKey")
    }
  }

  def decodePublicKey(publicKey: Array[Byte]): EdDSAPublicKey = {
    //    val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName("ed25519-sha-512")

    val pubKeyBytes: Array[Byte] = publicKey.length match {
      case 32 => publicKey
      case _ => EdDSAPublicKey.decode(publicKey)
    }
    val pubKey: EdDSAPublicKeySpec = new EdDSAPublicKeySpec(pubKeyBytes, EDDSASPEC)
    new EdDSAPublicKey(pubKey)
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
  def decodePrivateKey(privateKey: String): EdDSAPrivateKey = {

    CodecUtil.multiDecoder(privateKey) match {
      case Some(decoded) =>
        decodePrivateKey(decoded)
      case None =>
        throw new IllegalArgumentException(s"invalid privateKey: $privateKey")
    }
  }

  def decodePrivateKey(privateKey: Array[Byte]): EdDSAPrivateKey = {
    val privKeyBytes: Array[Byte] = privateKey.length match {
      case 32 => privateKey
      case 64 => privateKey.take(32)
      case _ => EdDSAPrivateKey.decode(privateKey)
    }
    val pubKey: EdDSAPrivateKeySpec = new EdDSAPrivateKeySpec(privKeyBytes, EDDSASPEC)
    new EdDSAPrivateKey(pubKey)
  }

}
