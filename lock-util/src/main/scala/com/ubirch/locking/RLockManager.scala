package com.ubirch.locking

import java.util.concurrent.TimeUnit

import com.ubirch.locking.config.LockingConfig

trait RLockManager {

  val redisson = LockingConfig.redisson

  def rLock(lockId: String): Boolean = {
    val lock = getLock(lockId)
    lock.tryLock(100, TimeUnit.MILLISECONDS)
  }

  def rUnlock(lockId: String): Boolean = {
    val lock = getLock(lockId)
    if (lock.isLocked && lock.isHeldByCurrentThread) {
      lock.unlock()
      true
    }
    else
      false
  }

  private def getLock(lockId: String) = redisson.getLock(lockId)

}
