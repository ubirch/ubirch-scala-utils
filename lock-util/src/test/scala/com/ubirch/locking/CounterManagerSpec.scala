package com.ubirch.locking

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{FeatureSpec, Matchers}

class CounterManagerSpec extends FeatureSpec
  with CounterManager
  with StrictLogging
  with Matchers {

  feature("basic test") {

    scenario("inc/get/dec counter") {
      val counterName = s"myLock-${UUIDUtil.uuidStr}"
      inc(counterName) shouldBe 1l
      inc(counterName) shouldBe 2l
      inc(counterName) shouldBe 3l

      get(counterName) shouldBe 3l

      dec(counterName) shouldBe 2l
      dec(counterName) shouldBe 1l
      dec(counterName) shouldBe 0l

      get(counterName) shouldBe 0l
    }

    scenario("inc/reset counter") {
      val counterName = s"myLock-${UUIDUtil.uuidStr}"
      inc(counterName) shouldBe 1l
      inc(counterName) shouldBe 2l
      inc(counterName) shouldBe 3l
      inc(counterName) shouldBe 4l
      inc(counterName) shouldBe 5l

      reset(counterName) shouldBe 0l

      get(counterName) shouldBe 0l
    }


    scenario("inc/get/dec counters") {
      val counterNames = List(
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}"
      )

      counterNames.foreach { counterName =>
        inc(counterName) shouldBe 1l
        inc(counterName) shouldBe 2l
        inc(counterName) shouldBe 3l

        get(counterName) shouldBe 3l

        dec(counterName) shouldBe 2l
        dec(counterName) shouldBe 1l
        dec(counterName) shouldBe 0l

        get(counterName) shouldBe 0l
      }
    }


    scenario("inc/get/dec counters 2") {
      val counterNames = List(
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}"
      )

      counterNames.foreach(inc(_) shouldBe 1l)
      counterNames.foreach(inc(_) shouldBe 2l)
      counterNames.foreach(inc(_) shouldBe 3l)
      counterNames.foreach(inc(_) shouldBe 4l)

      counterNames.foreach(get(_) shouldBe 4l)

      counterNames.foreach(dec(_) shouldBe 3l)
      counterNames.foreach(dec(_) shouldBe 2l)
      counterNames.foreach(dec(_) shouldBe 1l)
      counterNames.foreach(dec(_) shouldBe 0l)

      counterNames.foreach(get(_) shouldBe 0l)
    }
  }
}
