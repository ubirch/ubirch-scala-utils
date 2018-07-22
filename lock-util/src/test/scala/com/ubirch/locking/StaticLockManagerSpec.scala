package com.ubirch.locking

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.locking.config.LockingConfig
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{FeatureSpec, Matchers}

class StaticLockManagerSpec extends FeatureSpec
  with StrictLogging
  with Matchers
  with StaticLockManager {

  val lockId = s"myLock-${UUIDUtil.uuidStr}"

  feature("basic test") {

    scenario("create a lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      val lock1 = redisson.getLock(lockName)
      lock shouldBe true
      unlock shouldBe true
    }

    scenario("no lock") {
      unlock shouldBe false
    }

  }
}
