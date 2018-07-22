package com.ubirch.locking

import java.util.Date

import org.redisson.config.Config

object LockTester extends App {

  // 2. Create Redisson instance
  import org.redisson.Redisson

  val useAzure = false

  private val configAzure = new Config()
  configAzure
    .useReplicatedServers()
    .setScanInterval(2000)
    .addNodeAddress("rediss://ubirch-dev-redis.redis.cache.windows.net:6380")
    .setPassword("lfDuSIKuSvIBnSA0SrXQhl51BAtQ6e9oJh4W5VSjiEM=")

  val redisson = if (useAzure)
    Redisson.create(configAzure)
  else
    Redisson.create()

  // 3. Get object you need

  val lock1 = redisson.getLock("myLock3")
  val lock2 = redisson.getLock("myLock3")
  println(s"lock1 locked: ${lock1.isLocked}")
  println(s"lock2 locked: ${lock2.isLocked}")

  lock1.lock()
  println(s"lock1 locked: ${lock1.isLocked}")
  println(s"lock2 locked: ${lock2.isLocked}")
  assert(lock2.isLocked)

  lock1.unlock()
  println(s"lock1 locked: ${lock1.isLocked}")
  println(s"lock2 locked: ${lock2.isLocked}")
  assert(!lock2.isLocked)

  speedTest

  private def speedTest = {
    (1 to 60 by 20).toList.foreach { f =>
      val start = new Date()
      val iterations = 1000 * f
      println(s"iterations: $iterations")
      (1 to iterations).toList.foreach { i =>
        lock1.lock()
        assert(lock2.isLocked)
        lock1.unlock()
        assert(!lock2.isLocked)
      }
      val end = new Date()
      val diff = (end.getTime - start.getTime)

      println(s"time used: $diff millis")
      println(s"time used per lock/unlock: ${diff.toDouble / iterations.toDouble} millis")
    }
  }

  redisson.shutdown()

}
