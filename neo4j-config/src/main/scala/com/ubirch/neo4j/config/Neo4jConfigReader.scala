package com.ubirch.neo4j.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2018-08-11
  */
class Neo4jConfigReader(configPrefix: String = "ubirch.neo4j") extends ConfigBase {

  private def uri(): String = stringWithDefault(Neo4jConfigKeys.uri(configPrefix), default = "bolt://localhost:7687")

  private def userName(): String = stringWithDefault(Neo4jConfigKeys.userName(configPrefix), default = "neo4j")

  private def password(): String = stringWithDefault(Neo4jConfigKeys.password(configPrefix), default = "neo4jneo4j")

  private def neo4jEncryptionRequired(): Boolean = booleanWithDefault(Neo4jConfigKeys.encryptionRequired(configPrefix), true)

  /**
    * Strategy by which to trust TLS certificates.
    *
    * @return only the following values are valid: TRUST_ALL_CERTIFICATES, TRUST_SYSTEM_CA_SIGNED_CERTIFICATES
    */
  private def neo4jTrustStrategy(): String = stringWithDefault(Neo4jConfigKeys.trustStrategy(configPrefix), "TRUST_SYSTEM_CA_SIGNED_CERTIFICATES")

  /**
    * @return minutes
    */
  private def neo4jPoolMaxLifetime(): Int = intWithDefault(Neo4jConfigKeys.poolMaxLifetime(configPrefix), 60)

  private def neo4jPoolMaxSize(): Int = intWithDefault(Neo4jConfigKeys.poolMaxSize(configPrefix), 50)

  /**
    * @return seconds
    */
  private def neo4jPoolAcquisitionTimeout(): Int = intWithDefault(Neo4jConfigKeys.poolAcquisitionTimeout(configPrefix), 60)

  /**
    * @return seconds
    */

  private def neo4jTimeout(): Int = intWithDefault(Neo4jConfigKeys.timeout(configPrefix), 60)

  /**
    * @return seconds
    */
  private def neo4jMaxRetryTime(): Int = intWithDefault(Neo4jConfigKeys.maxRetryTime(configPrefix), 60)

  /**
    * only the following values would be valid: ROUND_ROBIN, LEAST_CONNECTED
    * @return
    */
  private def neo4jLoadBalanceStrategy(): String = stringWithDefault(Neo4jConfigKeys.loadBalanceStrategy(configPrefix), "LEAST_CONNECTED")

  def neo4jConfig(): Neo4jConfig = Neo4jConfig(
    // see https://neo4j.com/docs/developer-manual/3.4/drivers/client-applications/
    uri = uri(),
    userName = userName(),
    password = password(),
    encryptionRequired = neo4jEncryptionRequired(),
    trustStrategy = neo4jTrustStrategy(),
    poolMaxLifetime = neo4jPoolMaxLifetime(),
    poolMaxPoolSize = neo4jPoolMaxSize(),
    poolAcquisitionTimeout = neo4jPoolAcquisitionTimeout(),
    timeout = neo4jTimeout(),
    maxRetryTime = neo4jMaxRetryTime(),
    loadBalanceStrategy = neo4jLoadBalanceStrategy()
  )

}

case class Neo4jConfig(uri: String,
                       userName: String,
                       password: String,
                       encryptionRequired: Boolean = true,
                       trustStrategy: String = "TRUST_SYSTEM_CA_SIGNED_CERTIFICATES",
                       poolMaxLifetime: Int = 60, // minutes
                       poolMaxPoolSize: Int = 50,
                       poolAcquisitionTimeout: Int = 60, // seconds
                       timeout: Int = 60, // seconds
                       maxRetryTime: Int = 60, // seconds
                       loadBalanceStrategy: String = "LEAST_CONNECTED"
                      )
