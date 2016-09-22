package com.ubirch.util.date

import org.joda.time.{DateTime, DateTimeZone}

/**
  * author: cvandrei
  * since: 2016-09-05
  */
object DateUtil {

  def nowUTC = DateTime.now(DateTimeZone.UTC)

}
