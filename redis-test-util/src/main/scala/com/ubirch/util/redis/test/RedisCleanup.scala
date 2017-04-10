package com.ubirch.util.redis.test

import com.typesafe.scalalogging.slf4j.StrictLogging

import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * author: cvandrei
  * since: 2017-03-14
  */
trait RedisCleanup extends StrictLogging {

  final def deleteAll(redisPrefix: String = "",
                      configPrefix: String,
                      sleepAfter: Long = 500
                     )
                     (implicit redis: RedisClient): Unit = {

    val finalPrefix = if (redisPrefix == "") {
      "*"
    } else {
      s"$redisPrefix.*"
    }

    logger.info(s"====== delete: prefix=$finalPrefix")
    redis.keys(finalPrefix) map { keysList =>
      keysList foreach { key =>
        logger.info(s"delete: key=$key")
        redis.del(key)
      }
    }
    Thread.sleep(sleepAfter)

  }

}
