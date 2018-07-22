package com.ubirch.locking

trait LockManager extends RLockManager {

  def lock(lockId: String): Boolean = rLock(lockId)

  def unlock(lockId: String): Boolean = rUnlock(lockId)

}
