package com.ubirch.locking.config

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.config.ConfigBase
import org.redisson.Redisson
import org.redisson.config.Config

object LockingConfig
  extends StrictLogging
    with ConfigBase {

  private val redisUrl = config.getString("ubirch.lockutil.redis.url")
  private val redisCluster = config.getBoolean("ubirch.lockutil.redis.usecluster")

  private val redisConfig = {
    val cnf = new Config()
    if (redisCluster) {
      cnf.useClusterServers().addNodeAddress(redisUrl)
    }
    else
      cnf.useSingleServer().setAddress(redisUrl)
    logger.debug("Redisson config setup done")
    cnf
  }

  val redisson = Redisson.create(redisConfig)

}
