package com.ubirch.util.config

import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2018-03-06
  */
class EnvironmentUtilSpec extends FeatureSpec
  with Matchers {

  feature("POSTFIX_PROD") {

    scenario("value is _-prod_") {
      EnvironmentUtil.POSTFIX_PROD shouldBe "-prod"
    }

  }

  feature("POSTFIX_DEMO") {

    scenario("value is _-demo_") {
      EnvironmentUtil.POSTFIX_DEMO shouldBe "-demo"
    }

  }

  feature("POSTFIX_DEV") {

    scenario("value is _-dev_") {
      EnvironmentUtil.POSTFIX_DEV shouldBe "-dev"
    }

  }

  feature("POSTFIX_LOCAL") {

    scenario("value is _-local_") {
      EnvironmentUtil.POSTFIX_LOCAL shouldBe "-local"
    }

  }

  feature("isProd()") {

    scenario("empty string --> false") {
      EnvironmentUtil.isProd("") shouldBe false
    }

    scenario("postfix begins with wrong delimiter --> false") {
      EnvironmentUtil.isProd(s"ubirch${EnvironmentUtil.POSTFIX_PROD.replace("-", "_")}") shouldBe false
    }

    scenario("postfix is _-prod_ --> true") {
      EnvironmentUtil.isProd(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe true
    }

    scenario("postfix is _-demo_ --> false") {
      EnvironmentUtil.isProd(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe false
    }

    scenario("postfix is _-dev_ --> false") {
      EnvironmentUtil.isProd(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe false
    }

    scenario("postfix is _-local_ --> false") {
      EnvironmentUtil.isProd(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe false
    }

  }

  feature("isNotProd()") {

    scenario("empty string --> true") {
    EnvironmentUtil.isNotProd("") shouldBe true
  }

    scenario("postfix begins with wrong delimiter --> true") {
      EnvironmentUtil.isNotProd(s"ubirch${EnvironmentUtil.POSTFIX_PROD.replace("-", "_")}") shouldBe true
    }

    scenario("postfix is _-prod_ --> false") {
      EnvironmentUtil.isNotProd(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe false
    }

    scenario("postfix is _-demo_ --> true") {
      EnvironmentUtil.isNotProd(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe true
    }

    scenario("postfix is _-dev_ --> true") {
      EnvironmentUtil.isNotProd(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe true
    }

    scenario("postfix is _-local_ --> true") {
      EnvironmentUtil.isNotProd(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe true
    }

  }

  feature("isDemo()") {

    scenario("empty string --> false") {
      EnvironmentUtil.isDemo("") shouldBe false
    }

    scenario("postfix begins with wrong delimiter --> false") {
      EnvironmentUtil.isDemo(s"ubirch${EnvironmentUtil.POSTFIX_DEMO.replace("-", "_")}") shouldBe false
    }

    scenario("postfix is _-prod_ --> false") {
      EnvironmentUtil.isDemo(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe false
    }

    scenario("postfix is _-demo_ --> true") {
      EnvironmentUtil.isDemo(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe true
    }

    scenario("postfix is _-dev_ --> false") {
      EnvironmentUtil.isDemo(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe false
    }

    scenario("postfix is _-local_ --> false") {
      EnvironmentUtil.isDemo(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe false
    }

  }

  feature("isNotDemo()") {

    scenario("empty string --> true") {
    EnvironmentUtil.isNotDemo("") shouldBe true
  }

    scenario("postfix begins with wrong delimiter --> true") {
      EnvironmentUtil.isNotDemo(s"ubirch${EnvironmentUtil.POSTFIX_DEMO.replace("-", "_")}") shouldBe true
    }

    scenario("postfix is _-prod_ --> true") {
      EnvironmentUtil.isNotDemo(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe true
    }

    scenario("postfix is _-demo_ --> false") {
      EnvironmentUtil.isNotDemo(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe false
    }

    scenario("postfix is _-dev_ --> true") {
      EnvironmentUtil.isNotDemo(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe true
    }

    scenario("postfix is _-local_ --> true") {
      EnvironmentUtil.isNotDemo(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe true
    }

  }

  feature("isDev()") {

    scenario("empty string --> false") {
      EnvironmentUtil.isDev("") shouldBe false
    }

    scenario("postfix begins with wrong delimiter --> false") {
      EnvironmentUtil.isDev(s"ubirch${EnvironmentUtil.POSTFIX_DEV.replace("-", "_")}") shouldBe false
    }

    scenario("postfix is _-prod_ --> false") {
      EnvironmentUtil.isDev(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe false
    }

    scenario("postfix is _-demo_ --> false") {
      EnvironmentUtil.isDev(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe false
    }

    scenario("postfix is _-dev_ --> true") {
      EnvironmentUtil.isDev(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe true
    }

    scenario("postfix is _-local_ --> false") {
      EnvironmentUtil.isDev(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe false
    }

  }

  feature("isNotDev()") {

    scenario("empty string --> true") {
    EnvironmentUtil.isNotDev("") shouldBe true
  }

    scenario("postfix begins with wrong delimiter --> true") {
      EnvironmentUtil.isNotDev(s"ubirch${EnvironmentUtil.POSTFIX_DEV.replace("-", "_")}") shouldBe true
    }

    scenario("postfix is _-prod_ --> true") {
      EnvironmentUtil.isNotDev(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe true
    }

    scenario("postfix is _-demo_ --> true") {
      EnvironmentUtil.isNotDev(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe true
    }

    scenario("postfix is _-dev_ --> false") {
      EnvironmentUtil.isNotDev(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe false
    }

    scenario("postfix is _-local_ --> true") {
      EnvironmentUtil.isNotDev(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe true
    }

  }

  feature("isLocalDev()") {

    scenario("empty string --> false") {
      EnvironmentUtil.isLocal("") shouldBe false
    }

    scenario("postfix begins with wrong delimiter --> false") {
      EnvironmentUtil.isLocal(s"ubirch${EnvironmentUtil.POSTFIX_DEV.replace("-", "_")}") shouldBe false
    }

    scenario("postfix is _-prod_ --> false") {
      EnvironmentUtil.isLocal(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe false
    }

    scenario("postfix is _-demo_ --> false") {
      EnvironmentUtil.isLocal(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe false
    }

    scenario("postfix is _-dev_ --> false") {
      EnvironmentUtil.isLocal(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe false
    }

    scenario("postfix is _-local_ --> true") {
      EnvironmentUtil.isLocal(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe true
    }

  }

  feature("isNotLocal()") {

    scenario("empty string --> true") {
    EnvironmentUtil.isNotLocal("") shouldBe true
  }

    scenario("postfix begins with wrong delimiter --> true") {
      EnvironmentUtil.isNotLocal(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL.replace("-", "_")}") shouldBe true
    }

    scenario("postfix is _-prod_ --> true") {
      EnvironmentUtil.isNotLocal(s"ubirch${EnvironmentUtil.POSTFIX_PROD}") shouldBe true
    }

    scenario("postfix is _-demo_ --> true") {
      EnvironmentUtil.isNotLocal(s"ubirch${EnvironmentUtil.POSTFIX_DEMO}") shouldBe true
    }

    scenario("postfix is _-dev_ --> true") {
      EnvironmentUtil.isNotLocal(s"ubirch${EnvironmentUtil.POSTFIX_DEV}") shouldBe true
    }

    scenario("postfix is _-local_ --> false") {
      EnvironmentUtil.isNotLocal(s"ubirch${EnvironmentUtil.POSTFIX_LOCAL}") shouldBe false
    }

  }

}
