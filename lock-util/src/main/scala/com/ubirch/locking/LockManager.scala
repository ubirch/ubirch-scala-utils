package com.ubirch.locking

trait LockManager extends RLockManager {

  val lockId: String

  def lock: Boolean = rLock(lockId)

  def unlock: Boolean = rUnlock(lockId)

}
