package com.ubirch.util.date

import org.joda.time.format.ISODateTimeFormat
import org.scalatest.{FeatureSpec, Matchers}

/**
  * author: cvandrei
  * since: 2017-09-26
  */
class DateUtilSpec extends FeatureSpec
  with Matchers {

  feature("parseDateToUTC()") {

    scenario("invalid date") {
      an [IllegalArgumentException] shouldBe thrownBy(DateUtil.parseDateToUTC("2017-Sep-26"))
    }

    scenario("valid date") {
      DateUtil.parseDateToUTC("2017-09-26").toString(ISODateTimeFormat.dateTime()) should be("2017-09-26T00:00:00.000Z")
    }

  }

}
