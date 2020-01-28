package com.ubirch.locking

trait CounterManager extends RCounterManager {

  def inc(lockId: String): Long = rInc(lockId)

  def dec(lockId: String): Long = rDec(lockId)

  def get(lockId: String): Long = rGet(lockId)

  def reset(lockId: String): Long = rReset(lockId)

}
