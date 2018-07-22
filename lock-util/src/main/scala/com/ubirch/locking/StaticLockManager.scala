package com.ubirch.locking

trait StaticLockManager extends RLockManager {

  val lockId: String

  def lock: Boolean = rLock(lockId)

  def unlock: Boolean = rUnlock(lockId)

}
