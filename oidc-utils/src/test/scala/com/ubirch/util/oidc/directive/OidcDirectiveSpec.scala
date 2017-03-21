package com.ubirch.util.oidc.directive

import com.ubirch.util.redis.test.RedisCleanup

import org.scalatest.{BeforeAndAfterEach, FeatureSpec, Matchers}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest

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

  override protected def beforeEach(): Unit = {
    deleteAll(configPrefix = "")
    Thread.sleep(100)
  }

  private val oidcDirective = new OidcDirective()

  import oidcDirective._

  private val smallRoute: Route =
    get {
      pathSingleSlash {
        oidcToken2UserContext { userContext =>
          complete(s"context=${userContext.context}; userId=${userContext.userId}")
        }
      }
    }

  // TODO write tests (see http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/testkit.html)

}
