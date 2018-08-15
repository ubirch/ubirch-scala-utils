package com.ubirch.util.neo4j.utils

import com.typesafe.scalalogging.slf4j.StrictLogging

import org.neo4j.driver.v1.Driver

import scala.collection.JavaConverters._

/**
  * author: cvandrei
  * since: 2017-05-10
  */
object Neo4jUtils extends StrictLogging {

  def createConstraints(constraints: Set[String])
                       (implicit neo4jDriver: Driver): Boolean = {

    val results = constraints map { constraint =>
      runBoolean(s"CREATE $constraint")
    }

    results.forall(b => b)

  }

  def createIndices(indices: Set[String])
                   (implicit neo4jDriver: Driver): Boolean = {

    val results = indices map { index =>
      runBoolean(s"CREATE $index")
    }

    results.forall(b => b)

  }

  def dropAllConstraints()
                        (implicit neo4jDriver: Driver): Boolean = {

    val results = queryConstraints() map { constraint =>
      logger.debug(s"constraint to drop: $constraint")
      runBoolean(s"DROP $constraint")
    }

    results.forall(b => b)

  }

  def dropAllIndices()
                    (implicit neo4jDriver: Driver): Boolean = {

    val results = queryIndices() map { index =>
      runBoolean(s"DROP $index")
    }

    results.forall(b => b)

  }

  def queryConstraints()
                      (implicit neo4jDriver: Driver): Seq[String] = {

    queryStringField("CALL db.constraints()", "description")

  }

  def queryIndices()
                  (implicit neo4jDriver: Driver): Seq[String] = {

    queryStringField("CALL db.indexes()", "description")

  }

  def deleteAllNodesAndRelationships()
                                    (implicit neo4jDriver: Driver): Boolean = {

    deleteNodesInRelationships() && deleteFreeNodes()

  }

  def deleteNodesInRelationships()
                                (implicit neo4jDriver: Driver): Boolean = {

    val deletedRelationships = runBoolean("MATCH (n)-[r]-(m) DELETE n, r, m")
    if (deletedRelationships) {
      logger.info("Neo4j clean up: deleted nodes in a relationship (including relationships)")
      true
    } else {
      logger.error("failed to delete nodes in a relationship (and the relationships)")
      false
    }

  }

  def deleteFreeNodes()
                     (implicit neo4jDriver: Driver): Boolean = {

    val deletedFreeNodes = runBoolean("MATCH (n) DELETE n")
    if (deletedFreeNodes) {
      logger.info("Neo4j clean up: deleted free nodes")
      true
    } else {
      logger.error(s"failed to delete free nodes")
      false
    }

  }

  private def runBoolean(statement: String)
                        (implicit neo4jDriver: Driver): Boolean = {

    logger.debug(s"runBoolean() -- statement=$statement")
    try {

      val session = neo4jDriver.session
      try {

        session.run(statement)
        true

      } finally {
        if (session != null) session.close()
      }

    } catch {

      case e: Exception =>

        logger.error(s"Exception: statement=$statement, e.message=${e.getMessage}", e)
        false

      case re: RuntimeException =>

        logger.error(s"RuntimeException: statement=$statement, re.message=${re.getMessage}", re)
        false

    }

  }

  private def queryStringField(statement: String, field: String)
                              (implicit neo4jDriver: Driver): Seq[String] = {

    logger.debug(s"queryStringField(): statement=$statement, field=$field")
    try {

      val session = neo4jDriver.session
      try {

        val result = session.run(statement)
        val records = result.list().asScala
        records.map(
          _.get(field)
            .asString()
        )

      } finally {
        if (session != null) session.close()
      }

    } catch {

      case e: Exception =>

        logger.error(s"Exception: statement=$statement, e.message=${e.getMessage}", e)
        throw e

      case re: RuntimeException =>

        logger.error(s"RuntimeException: statement=$statement, re.message=${re.getMessage}", re)
        throw re

    }

  }

}
