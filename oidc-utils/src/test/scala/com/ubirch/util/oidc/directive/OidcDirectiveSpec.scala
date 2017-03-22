package com.ubirch.util.oidc.directive

import com.ubirch.util.oidc.config.OidcUtilsConfigKeys
import com.ubirch.util.oidc.util.OidcHeaders
import com.ubirch.util.redis.RedisClientUtil
import com.ubirch.util.redis.test.RedisCleanup

import org.scalatest.{BeforeAndAfterEach, FeatureSpec, Matchers}

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken, RawHeader}
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import redis.RedisClient

import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-03-21
  */
class OidcDirectiveSpec extends FeatureSpec
  with ScalatestRouteTest
  with Matchers
  with BeforeAndAfterEach
  with RedisCleanup {

  private val configPrefix = OidcUtilsConfigKeys.PREFIX

  override protected def beforeEach(): Unit = {
    deleteAll(configPrefix = configPrefix)
    Thread.sleep(100)
  }

  private val redis: RedisClient = RedisClientUtil.newInstance(configPrefix)(system)
  private val oidcDirective = new OidcDirective(configPrefix = configPrefix, redis = redis)

  import oidcDirective._

  private val testRoute: Route =
    get {
      pathSingleSlash {
        oidcToken2UserContext { userContext =>
          complete(s"context=${userContext.context}; userId=${userContext.userId}")
        }
      }
    }

  // NOTE http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/testkit.html

  feature("oidcToken2UserContext") {

    scenario("without any headers") {

      Get() ~> Route.seal(testRoute) ~> check {
        status === StatusCodes.BadRequest
        responseAs[String] shouldEqual "Request is missing required HTTP header 'X-UBIRCH-CONTEXT'"
      }

    }

    scenario("with all headers but token does not exist") {

      val contextHeader: HttpHeader = RawHeader(OidcHeaders.CONTEXT, "some-context")
      val providerHeader: HttpHeader = RawHeader(OidcHeaders.PROVIDER, "some-provider")
      val authorizationHeader: HttpHeader = Authorization(OAuth2BearerToken("some-token"))

      Get().withHeaders(contextHeader, providerHeader, authorizationHeader) ~>
        testRoute ~> check {

        handled shouldBe false
        rejection shouldEqual AuthorizationFailedRejection

      }

    }

    ignore("with all headers and token exists") {

      val context = "some-context"
      val provider = "some-provider"
      val token = "some-token"
      val userId = "some-user-id"

      // TODO persist: sha256(provider:token) = userId

      val contextHeader: HttpHeader = RawHeader(OidcHeaders.CONTEXT, context)
      val providerHeader: HttpHeader = RawHeader(OidcHeaders.PROVIDER, provider)
      val authorizationHeader: HttpHeader = Authorization(OAuth2BearerToken(token))

      Get().withHeaders(contextHeader, providerHeader, authorizationHeader) ~>
        Route.seal(testRoute) ~> check {

        status === StatusCodes.OK
        responseAs[String] shouldEqual s"context=$context; userId=$userId"

      }

    }

    scenario(s"test case: with all headers (except ${OidcHeaders.CONTEXT})") {

      val providerHeader: HttpHeader = RawHeader(OidcHeaders.PROVIDER, "some-provider")
      val authorizationHeader: HttpHeader = Authorization(OAuth2BearerToken("some-token"))

      Get().withHeaders(providerHeader, authorizationHeader) ~>
        Route.seal(testRoute) ~> check {

        status === StatusCodes.BadRequest
        responseAs[String] shouldEqual s"Request is missing required HTTP header '${OidcHeaders.CONTEXT}'"

      }

    }

    scenario(s"test case: with all headers (except ${OidcHeaders.PROVIDER})") {

      val contextHeader: HttpHeader = RawHeader(OidcHeaders.CONTEXT, "some-context")
      val authorizationHeader: HttpHeader = Authorization(OAuth2BearerToken("some-token"))

      Get().withHeaders(contextHeader, authorizationHeader) ~>
        Route.seal(testRoute) ~> check {

        status === StatusCodes.BadRequest
        responseAs[String] shouldEqual s"Request is missing required HTTP header '${OidcHeaders.PROVIDER}'"

      }

    }

    scenario("test case: with all headers (except Authorization)") {

      val contextHeader: HttpHeader = RawHeader(OidcHeaders.CONTEXT, "some-context")
      val providerHeader: HttpHeader = RawHeader(OidcHeaders.PROVIDER, "some-provider")

      Get().withHeaders(contextHeader, providerHeader) ~>
        Route.seal(testRoute) ~> check {

        status === StatusCodes.BadRequest
        responseAs[String] shouldEqual "The supplied authentication is not authorized to access this resource"

      }

    }

  }

}
