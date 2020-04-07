package com.ubirch.util.oidc.directive

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import akka.stream.Materializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.crypto.ecc.EccUtil
import com.ubirch.key.model.rest.PublicKey
import com.ubirch.keyservice.client.rest.cache.redis.KeyServiceClientRestCacheRedis
import com.ubirch.user.client.rest.UserServiceClientRest
import com.ubirch.user.model.rest.User
import com.ubirch.util.config.ConfigBase
import com.ubirch.util.http.response.ResponseUtil
import com.ubirch.util.json.{Json4sUtil, JsonFormats}
import com.ubirch.util.model.JsonErrorResponse
import com.ubirch.util.oidc.config.OidcUtilsConfig
import com.ubirch.util.oidc.model.UserContext
import com.ubirch.util.oidc.util.{OidcUtil, UbirchTokenUtil}
import com.ubirch.util.redis.RedisClientUtil
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.Formats
import org.json4s.native.Serialization.read
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-03-17
  */
class OidcDirective()(implicit system: ActorSystem, httpClient: HttpExt, materializer: Materializer) extends ResponseUtil
  with StrictLogging
  with ConfigBase {

  implicit private val formatter: Formats = JsonFormats.default

  private val envid = config.getString("ubirch.envid").toLowerCase
  private val eccUtil = new EccUtil()
  private val redis = RedisClientUtil.getRedisClient
  private val refreshIntervalSeconds = OidcUtilsConfig.redisUpdateExpirySeconds()
  private val skipEnvChecking = OidcUtilsConfig.skipEnvChecking()
  private val allowInvalidSignature = OidcUtilsConfig.allowInvalidSignature()
  private val maxTokenAge = OidcUtilsConfig.maxTokenAge()

  val bearerToken: Directive1[Option[String]] = optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken)

  val ubirchToken: Directive1[Option[String]] = optionalHeaderValueByName("Authorization")

  val oidcToken2UserContext: Directive1[UserContext] = {

    bearerToken flatMap {

      case None =>

        ubirchToken flatMap {

          case None =>
            val errorRsp = JsonErrorResponse(errorType = "01", errorMessage = "Authorization header is missing.")
            complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized))

          case Some(ubToken: String) =>

            //Todo: Metriken und Vereinfachung der Exception Struktur/ des Exceptionhandlings
            onComplete(ubTokenToUserContext(ubToken = ubToken)) flatMap { u =>

              u.map(provide).recover {

                case ex: AuthTokenFormatError =>
                  val errorRsp = JsonErrorResponse(errorType = "02", errorMessage = s"auth token format error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: AuthTokenContextError =>
                  val errorRsp = JsonErrorResponse(errorType = "03", errorMessage = s"auth token context error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: TokenTimeoutError =>
                  val errorRsp = JsonErrorResponse(errorType = "04", errorMessage = s"token timeout error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: ParsingError =>
                  val errorRsp = JsonErrorResponse(errorType = "05", errorMessage = s"parsing error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: UserNotFoundError =>
                  val errorRsp = JsonErrorResponse(errorType = "06", errorMessage = s"user not found error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: SignatureInvalidError =>
                  val errorRsp = JsonErrorResponse(errorType = "07", errorMessage = s"signature invalid error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: PublicKeyMissingError =>
                  val errorRsp = JsonErrorResponse(errorType = "08", errorMessage = s"public key missing error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: KeyServiceError =>
                  val errorRsp = JsonErrorResponse(errorType = "09", errorMessage = s"key service error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

                case ex: UnknownValidationException =>
                  val errorRsp = JsonErrorResponse(errorType = "10", errorMessage = s"validation error: ${ex.getMessage}")
                  complete(requestErrorResponse(errorRsp, StatusCodes.Unauthorized)).toDirective[Tuple1[UserContext]]

              }.get

            }

        }

      case Some(token: String) =>

        onComplete(tokenToUserContext(token = token)) flatMap { u =>

          u.map(provide).recover {

            case _: VerificationException =>
              logger.error(s"Unable to log in with provided token: $token")
              reject(AuthorizationFailedRejection).toDirective[Tuple1[UserContext]]

          }.get

        }
    }

  }

  private def getRedisKey(externalId: String): String = {
    // TODO ??? redis keys may be hashed but since ubirch tokens are typically stored long-term this would have to include a migration of existing keys (consider adding hash iterations, too)
    //OidcUtil.tokenToHashedKey(s"$envid--$ubToken")
    s"$envid--$externalId"
  }

  /**
    * Method that splits the Ubirch Authorization Token into it's different parts and validates that:
    * 1) a correct context is provided
    *
    * @param ubToken the authorization token to become validated
    * @return userContext if valid auth token was found
    */
  @throws(classOf[AuthTokenContextError])
  @throws(classOf[AuthTokenFormatError])
  @throws(classOf[PublicKeyMissingError])
  @throws(classOf[SignatureInvalidError])
  @throws(classOf[ParsingError])
  @throws(classOf[TokenTimeoutError])
  @throws(classOf[UserNotFoundError])
  private def ubTokenToUserContext(ubToken: String)(implicit httpClient: HttpExt, materializer: Materializer): Future[UserContext] = {

    val (context, token, externalId, signature) = try {
      splitTokenAndVerifyContextAndTimestamp(ubToken)
    } catch {
      case ex: Exception => return Future.failed(ex)
    }


    checkSignature(externalId, token, signature, ubToken).flatMap { valid =>

      redis.get[String](getRedisKey(externalId)) flatMap {

        case None =>
          UserServiceClientRest.userGET(providerId = UbirchTokenUtil.providerId, externalUserId = externalId).map {

            case Some(user) if user.id.isDefined && user.activeUser =>
              createAndCacheUserContext(context, externalId, user, valid, ubToken, redis)

            case _ =>
              val msg = s"no user could be found by the user service for the externalId: $externalId"
              logger.info(msg)
              throw new UserNotFoundError(msg)
          }

        case Some(json) =>
          try {
            logger.debug(s"userContext is valid...will update it's TTL now (redisKey = >>$ubToken<<)")
            val uc = read[UserContext](json)
            updateExpiry(redis, getRedisKey(externalId))
            Future.successful(uc)
          } catch {
            case ex: Throwable =>
              val msg = s"error when parsing userContext for externalId $externalId from cache: $ex"
              logger.error(msg)
              Future.failed(new ParsingError(msg))
          }
      }
    }
  }

  /**
    * Method that splits the Ubirch auth token into it's different parts
    * and checks if a valid context is provided.
    */
  @throws(classOf[AuthTokenFormatError])
  @throws(classOf[AuthTokenContextError])
  @throws(classOf[TokenTimeoutError])
  @throws(classOf[ParsingError])
  private def splitTokenAndVerifyContextAndTimestamp(ubToken: String) = {
    val split = splitToken(ubToken)
    val (externalId, timestamp) = splitSubToken(split(1))
    val context = verifyContext(split(0), externalId)
    verifyAuthTimeLimit(timestamp, externalId)
    val signature = split(2)
    (context, split(1), externalId, signature)
  }

  /**
    * Method that retrieves three parts of the Ubirch auth token.
    */
  @throws(classOf[AuthTokenFormatError])
  private def splitToken(token: String) = {
    val split = token.split("::")
    if (split.size != 3) {
      val msg = s"auth token validation has wrong format - wrong number of separators like '::' : $token"
      logger.info(msg)
      throw new AuthTokenFormatError(msg)
    }
    split
  }

  /**
    * Method that retrieves the externalId and timestamp from the middle part
    * of the auth token.
    */
  @throws(classOf[AuthTokenFormatError])
  private def splitSubToken(token: String) = {
    val tokenParts = token.split("##")
    if (tokenParts.size != 2) {
      val msg = s"auth token validation has wrong format - wrong number of separators like '##': >>$token<<"
      logger.info(msg)
      throw new AuthTokenFormatError(msg)
    }
    val externalId = tokenParts(0).toLowerCase.trim
    (externalId, tokenParts.last)
  }

  /**
    * Method that trims context part of authToken from whitespaces and "bearer"
    * and checks if valid context is provided.
    */
  @throws(classOf[AuthTokenContextError])
  private def verifyContext(tokenSplit: String, externalId: String) = {
    val context = tokenSplit.toLowerCase.replace("bearer", "").trim
    if (!skipEnvChecking && !envid.equals(context)) {
      val msg = s"environment of authToken of externalId $externalId is invalid: $context"
      logger.info(msg)
      throw new AuthTokenContextError(msg)
    }
    context
  }

  /**
    * This method parses the timestamp string and checks if it still is valid.
    */
  @throws(classOf[TokenTimeoutError])
  @throws(classOf[ParsingError])
  private def verifyAuthTimeLimit(tsStr: String, externalId: String) = {

    val now = new DateTime(DateTimeZone.UTC)

    val timestamp = try {
      new DateTime(tsStr.toLong, DateTimeZone.UTC)
    } catch {
      case ex: Throwable =>
        val msg = s"the timestamp $tsStr of the auth token of externalId $externalId couldn't become parsed: $ex"
        logger.error(msg)
        throw new ParsingError(msg)
    }

    if (now.minusMinutes(maxTokenAge).isBefore(timestamp)) true
    else throw new TokenTimeoutError(s" the timestamp of the auth token for $externalId was timed out")
  }

  private def createAndCacheUserContext(context: String, externalId: String, user: User, hasKey: Int, ubToken: String, redis: RedisClient) = {
    val uc: UserContext = UserContext(
      context = context,
      providerId = UbirchTokenUtil.providerId,
      externalUserId = externalId,
      userId = user.id.get.toString,
      userName = user.displayName,
      locale = user.locale,
      hasPubKey = hasKey
    )
    redis.set[String](getRedisKey(externalId), Json4sUtil.any2String(uc).get, exSeconds = Some(refreshIntervalSeconds)) map {
      case true => logger.debug(s"added successfully userContext to redis cache for externalId $externalId")
      case _ => logger.info(s"adding userContext to redis cache for externalId $externalId failed")
    }
    uc
  }

  /**
    * This method validates the signature added to the authentication token.
    *
    * @param externalId the id of the user.
    * @param token      the payload that has been signed.
    * @param signature  the signature created with help of the user's private key.
    * @param ubToken    only used for logging purposes
    * @return validation return code if invalidSignatureAllowed
    *         1 => a public key exists and the authToken was validated correctly
    *         0 => no public key exists
    *         -1 => a public key exists, but validation failed
    *         -2 => something else went wrong
    */
  @throws(classOf[PublicKeyMissingError])
  @throws(classOf[SignatureInvalidError])
  @throws(classOf[KeyServiceError])
  private def checkSignature(externalId: String, token: String, signature: String, ubToken: String): Future[Int] = {

    KeyServiceClientRestCacheRedis.currentlyValidPubKeysCached(externalId)
      .map {

        case Some(pubKeys) if pubKeys.isEmpty =>
          //Todo: Implement user request to check if user exists at all
          val msg = s"no public key was returned by key service for externalId: $externalId - empty response"
          logger.info(msg)
          if (allowInvalidSignature) 0 else throw new PublicKeyMissingError(msg)

        case None =>
          val msg = s"no public key was returned by key service for externalId: $externalId - unkown error"
          logger.error(msg)
          if (allowInvalidSignature) -2 else throw new KeyServiceError(msg)

        case Some(pubKeys) =>
          logger.debug(s"received number of pubKeys ${pubKeys.size}")

          validateSignature(pubKeys, signature, token.getBytes()) match {

            case valid if valid.contains(1) =>
              logger.debug(s"successfully validated signature of auth token for user $externalId")
              1

            case invalid if invalid.contains(-2) =>
              val msg = s"something unexpected went wrong when validating the signature of authToken $ubToken"
              if (allowInvalidSignature) -2 else throw new UnknownValidationException(msg)

            case _ =>
              val msg = s"failed validating signature of auth token $ubToken"
              logger.warn(msg)
              if (allowInvalidSignature) -1 else throw new SignatureInvalidError(msg)
          }
      }
  }


  /**
    * Method to encapsulate validation call with error handling.
    */
  private def validateSignature(pubKeys: Set[PublicKey], signature: String, payload: Array[Byte]) = {
    try {
      pubKeys.map(pubkey => eccUtil.validateSignature(pubkey.pubKeyInfo.pubKey, signature, payload))
        .map(result => if (result) 1 else 0)
    } catch {
      case ex: Throwable =>
        if (!allowInvalidSignature) logger.error(s"something unexpected went wrong validating the signature of a authorization token: $ex")
        Set(-2)
    }
  }


  private def tokenToUserContext(token: String): Future[UserContext] = {

    val redis = RedisClientUtil.getRedisClient
    val tokenKey = OidcUtil.tokenToHashedKey(token)
    redis.get[String](tokenKey) map {

      case None =>
        val msg = s"token does not exist: redisKey=$tokenKey"
        logger.info(msg)
        throw new VerificationException()

      case Some(json) =>
        logger.debug(s"token is valid...will update it's TTL now (tokenKey=$tokenKey)")
        updateExpiry(redis, tokenKey)
        read[UserContext](json)
    }
  }

  private def updateExpiry(redis: RedisClient, redisKey: String): Unit = {

    redis.expire(redisKey, seconds = refreshIntervalSeconds) map {
      case true => logger.debug(s"refreshed token expiry ($refreshIntervalSeconds seconds): tokenKey: $redisKey")
      case _ => logger.info(s"failed to refresh token expiry ($refreshIntervalSeconds seconds): tokenKey: $redisKey")
    }
  }

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }

}

class VerificationException() extends Exception

sealed class ValidationException(msg: String) extends Exception(msg)

class AuthTokenFormatError(msg: String) extends ValidationException(msg)

class AuthTokenContextError(msg: String) extends ValidationException(msg)

class TokenTimeoutError(msg: String) extends ValidationException(msg)

class ParsingError(msg: String) extends ValidationException(msg)

class UserNotFoundError(msg: String) extends ValidationException(msg)

class SignatureInvalidError(msg: String) extends ValidationException(msg)

class PublicKeyMissingError(msg: String) extends ValidationException(msg)

class KeyServiceError(msg: String) extends ValidationException(msg)

class UnknownValidationException(msg: String) extends ValidationException(msg)

class ParsingException(msg: String) extends ValidationException(msg)
