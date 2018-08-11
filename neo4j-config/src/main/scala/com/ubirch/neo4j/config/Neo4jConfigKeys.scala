package com.ubirch.neo4j.config

/**
  * author: cvandrei
  * since: 2018-08-11
  */
object Neo4jConfigKeys {

  private final def neo4jPoolPrefix(prefix: String): String = s"$prefix.pool"

  final def uri(prefix: String): String = s"$prefix.uri"

  final def userName(prefix: String): String = s"$prefix.userName"

  final def password(prefix: String): String = s"$prefix.password"

  final def encryptionRequired(prefix: String): String = s"$prefix.encryptionRequired"

  final def trustStrategy(prefix: String): String = s"$prefix.trustStrategy"

  final def poolMaxLifetime(prefix: String): String = s"${neo4jPoolPrefix(prefix)}.maxLifetime"

  final def poolMaxSize(prefix: String): String = s"${neo4jPoolPrefix(prefix)}.maxSize"

  final def poolAcquisitionTimeout(prefix: String): String = s"${neo4jPoolPrefix(prefix)}.acquisitionTimeout"

  final def timeout(prefix: String): String = s"$prefix.timeout"

  final def maxRetryTime(prefix: String): String = s"$prefix.maxRetryTime"

  final def loadBalanceStrategy(prefix: String): String = s"$prefix.loadBalancingStrategy"

}
