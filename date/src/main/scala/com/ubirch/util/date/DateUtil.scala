package com.ubirch.util.date

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone, LocalTime}

/**
  * author: cvandrei
  * since: 2016-09-05
  */
object DateUtil {

  def nowUTC: DateTime = DateTime.now(DateTimeZone.UTC)

  def todayAtMidnight: DateTime = nowUTC.withTime(LocalTime.MIDNIGHT)

  def parseDateToUTC(dateString: String): DateTime = {

    ISODateTimeFormat.dateTime()
      .parseDateTime(dateString + "T00:00:00.000Z")
      .withZone(DateTimeZone.UTC)

  }

  /**
    *
    * @param from start date
    * @param to end date
    * @param stepSize milliseconds between dates in range
    * @return resulting DateTime range
    */
  def dateRange(from: DateTime, to: DateTime, stepSize: Int): Seq[DateTime] = {

    if (stepSize < 1) {

      Seq.empty

    } else {

      if (from.isBefore(to)) {
        Iterator.iterate(from)(_.plus(stepSize)).takeWhile(!_.isAfter(to)).toSeq
      } else {
        Iterator.iterate(from)(_.minus(stepSize)).takeWhile(!_.isBefore(to)).toSeq
      }

    }

  }

}
