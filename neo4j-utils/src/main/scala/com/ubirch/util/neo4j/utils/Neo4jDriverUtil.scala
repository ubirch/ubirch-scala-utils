package com.ubirch.util.neo4j.utils

import java.util.concurrent.TimeUnit._

import com.ubirch.neo4j.config.{Neo4jConfig, Neo4jConfigReader}

import org.neo4j.driver.v1.{AuthTokens, Config, Driver, GraphDatabase}

/**
  * author: cvandrei
  * since: 2018-08-11
  */
object Neo4jDriverUtil {

  def config(neo4jConf: Neo4jConfig): Config = {

    var configBuilder = Config.build()

    if (!neo4jConf.encryptionRequired) {
      configBuilder = configBuilder.withoutEncryption()
    }

    val trustStrategy = neo4jConf.trustStrategy match {
      case "TRUST_ALL_CERTIFICATES" => Config.TrustStrategy.trustAllCertificates()
      case "TRUST_SYSTEM_CA_SIGNED_CERTIFICATES" => Config.TrustStrategy.trustSystemCertificates()
      case s => throw new IllegalArgumentException(s"invalid Neo4j trust strategy: $s")
    }
    configBuilder = configBuilder.withTrustStrategy(trustStrategy)

    configBuilder = configBuilder.withMaxConnectionLifetime(neo4jConf.poolMaxLifetime, MINUTES)

    configBuilder = configBuilder.withMaxConnectionPoolSize(neo4jConf.poolMaxPoolSize)

    configBuilder = configBuilder.withConnectionAcquisitionTimeout(neo4jConf.poolAcquisitionTimeout, SECONDS)

    configBuilder = configBuilder.withConnectionTimeout(neo4jConf.timeout, SECONDS)

    configBuilder = configBuilder.withMaxTransactionRetryTime(neo4jConf.maxRetryTime, SECONDS)

    val loadBalancingStrategy = neo4jConf.loadBalanceStrategy match {
      case "ROUND_ROBIN" => Config.LoadBalancingStrategy.ROUND_ROBIN
      case "LEAST_CONNECTED" => Config.LoadBalancingStrategy.LEAST_CONNECTED
      case s => throw new IllegalArgumentException(s"invalid Neo4j load balancing strategy: $s")
    }
    configBuilder.withLoadBalancingStrategy(loadBalancingStrategy)

    configBuilder.toConfig

  }

  def config(configPrefix: String): Config = {

    config(
      new Neo4jConfigReader(configPrefix).neo4jConfig()
    )

  }

  def driver(neo4jConf: Neo4jConfig): Driver = {

    GraphDatabase.driver(
      neo4jConf.uri,
      AuthTokens.basic( neo4jConf.userName, neo4jConf.password ),
      config(neo4jConf)
    )

  }

  def driver(configPrefix: String): Driver = {

    driver(
      new Neo4jConfigReader(configPrefix).neo4jConfig()
    )

  }

}
