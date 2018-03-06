package com.ubirch.util.config

/**
  * author: cvandrei
  * since: 2018-03-06
  */
object EnvironmentUtil {

  val POSTFIX_PROD = "-prod"

  val POSTFIX_DEMO = "-demo"

  val POSTFIX_DEV = "-dev"

  val POSTFIX_LOCAL = "-local"

  def isProd(envId: String): Boolean = envId.endsWith(POSTFIX_PROD)

  def isNotProd(envId: String): Boolean = !isProd(envId)

  def isDemo(envId: String): Boolean = envId.endsWith(POSTFIX_DEMO)

  def isNotDemo(envId: String): Boolean = !isDemo(envId)

  def isDev(envId: String): Boolean = envId.endsWith(POSTFIX_DEV)

  def isNotDev(envId: String): Boolean = !isDev(envId)

  def isLocal(envId: String): Boolean = envId.endsWith(POSTFIX_LOCAL)

  def isNotLocal(envId: String): Boolean = !isLocal(envId)

}
