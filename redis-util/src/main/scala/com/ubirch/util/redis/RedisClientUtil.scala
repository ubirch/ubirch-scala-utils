package com.ubirch.util.redis

import com.ubirch.util.redis.config.Config

import akka.actor.ActorSystem
import redis.RedisClient

/**
  * author: cvandrei
  * since: 2017-03-15
  */
object RedisClientUtil {

  /**
    * Gives us an open Redis connection based on the configured host, port and password.
    *
    * This is how you should call it:
    *
    * <pre><code>
    * implicit val system = ActorSystem()
    * implicit val timeout = Timeout(15 seconds)
    * val configPrefix = "myService"
    * val redis = RedisClientUtil.newInstance(configPrefix)(system)
    * </code></pre>
    *
    * @param configPrefix prefix under which redis config keys will be looked for
    * @param system       Akka's Actor System since it's required by the Redis client
    * @return an open Redis connection
    */
  def newInstance(configPrefix: String)(implicit system: ActorSystem): RedisClient = {

    val hostPort = Config.hostAndPort(configPrefix)
    val password = Config.password(configPrefix)

    RedisClient(
      host = hostPort._1,
      port = hostPort._2,
      password = password
    )

  }

}
