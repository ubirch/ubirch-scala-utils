package com.ubirch.locking.config

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.util.config.ConfigBase
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

object LockingConfig
  extends StrictLogging
    with ConfigBase {

  private val redisHost = config.getString("ubirch.lockutil.redis.host")
  private val redisPort = config.getString("ubirch.lockutil.redis.port")
  private val redisPassword = config.getString("ubirch.lockutil.redis.password")
  private val nettyThreads = config.getInt("ubirch.lockutil.redis.nettyThreads")
  private val connectionPoolSize = config.getInt("ubirch.lockutil.redis.connectionPoolSize")
  private val timeout = config.getInt("ubirch.lockutil.redis.timeoutMilliSeconds")
  private val retryInterval = config.getInt("ubirch.lockutil.redis.retryIntervalMilliseconds")
  private val retryAttempts = config.getInt("ubirch.lockutil.redis.retryAttempts")

  private val redisUrl = s"redis://$redisHost:$redisPort"
  private val redisCluster = config.getBoolean("ubirch.lockutil.redis.usecluster")

  private val redisConfig = {
    val cnf = new Config()
    if (redisCluster) {
      cnf.useClusterServers().addNodeAddress(redisUrl)
      if (redisPassword.nonEmpty) cnf.useClusterServers().setPassword(redisPassword)
    } else {
      cnf.useSingleServer().setAddress(redisUrl)
      if (redisPassword.nonEmpty) cnf.useSingleServer().setPassword(redisPassword)
      cnf.useSingleServer().setConnectionPoolSize(connectionPoolSize)
      cnf.useSingleServer().setTimeout(timeout)
      cnf.useSingleServer().setRetryInterval(retryInterval)
      cnf.useSingleServer().setRetryAttempts(retryAttempts)
    }
    cnf.setNettyThreads(nettyThreads)

    logger.debug("Redisson config setup done")
    cnf
  }

  val redisson: RedissonClient = Redisson.create(redisConfig)

}
