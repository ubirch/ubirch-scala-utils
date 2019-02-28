package com.ubirch.locking

import com.ubirch.locking.config.LockingConfig

trait RCounterManager {

  val redisson = LockingConfig.redisson

  def rInc(counterId: String): Long = {
    redisson.getAtomicLong(counterId).incrementAndGet()
  }

  def rDec(counterId: String): Long = {
    redisson.getAtomicLong(counterId).decrementAndGet()
  }

  def rGet(counterId: String): Long = {
    redisson.getAtomicLong(counterId).get()
  }

  def rReset(counterId: String): Long = {
    redisson.getAtomicLong(counterId).set(0)
    rGet(counterId)
  }

}
