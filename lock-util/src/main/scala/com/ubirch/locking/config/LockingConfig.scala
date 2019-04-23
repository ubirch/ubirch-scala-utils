package com.ubirch.locking.config

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.config.ConfigBase
import org.redisson.Redisson
import org.redisson.config.Config

object LockingConfig
  extends StrictLogging
    with ConfigBase {


  private val redisHost = config.getString("ubirch.lockutil.redis.host")
  private val redisPort = config.getString("ubirch.lockutil.redis.port")
  private val redisPassword = config.getString("ubirch.lockutil.redis.password")

  private val redisUrl = s"redis://$redisHost:$redisPort"
  private val redisCluster = config.getBoolean("ubirch.lockutil.redis.usecluster")

  private val redisConfig = {
    val cnf = new Config()
    if (redisCluster) {
      cnf.useClusterServers().addNodeAddress(redisUrl)
      if (redisPassword.nonEmpty)
        cnf.useClusterServers().setPassword(redisPassword)
    }
    else {
      cnf.useSingleServer().setAddress(redisUrl)
      if (redisPassword.nonEmpty)
        cnf.useSingleServer().setPassword(redisPassword)
    }
    logger.debug("Redisson config setup done")
    cnf
  }

  val redisson = Redisson.create(redisConfig)

}
