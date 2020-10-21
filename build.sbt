concurrentRestrictions in Global := Seq(
  Tags.limit(Tags.Test, 1)
)


/*
 * RESOLVER
 ********************************************************/

val sonatypeReleases = Resolver.sonatypeRepo("releases")
val sonatypeSnapshots = Resolver.sonatypeRepo("snapshots")
val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")
val resolverElasticsearch = "elasticsearch-releases" at "https://artifacts.elastic.co/maven"

val ubirchUtilGroup = "com.ubirch.util"

val commonSettings = Seq(

  scalaVersion := "2.11.12",
  scalacOptions ++= Seq(
    "-feature"
  ),

  organization := ubirchUtilGroup,

  homepage := Some(url("http://ubirch.com")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/ubirch/ubirch-scala-utils"),
    "https://github.com/ubirch/ubirch-scala-utils.git"
  )),
  (sys.env.get("CLOUDREPO_USER"), sys.env.get("CLOUDREPO_PW")) match {
    case (Some(username), Some(password)) =>
      println("USERNAME and/or PASSWORD found.")
      credentials += Credentials("ubirch.mycloudrepo.io", "ubirch.mycloudrepo.io", username, password)
    case _ =>
      println("USERNAME and/or PASSWORD is taken from /.sbt/.credentials.")
      credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
  },
  resolvers ++= Seq(
    sonatypeReleases,
    sonatypeSnapshots),
  publishTo := Some("io.cloudrepo" at "https://ubirch.mycloudrepo.io/repositories/trackle-mvn"),
  publishMavenStyle := true
)

/*
 * MODULES
 ********************************************************/

lazy val scalaUtils = project
  .settings(commonSettings ++ Seq(packagedArtifacts := Map.empty): _*)
  .aggregate(
    elasticsearchClientBinary,
    elasticsearchUtil,
    neo4jConfig,
    neo4jUtils
  )


lazy val elasticsearchClientBinary = (project in file("elasticsearch-client-binary"))
  .settings(commonSettings)
  .settings(
    name := "elasticsearch-client-binary",
    description := "Elasticsearch client using the binary TransportClient",
    version := "3.3.1",
    resolvers ++= Seq(
      resolverElasticsearch
    ),
    libraryDependencies ++= depElasticsearchClientBinary
  )

lazy val elasticsearchUtil = (project in file("elasticsearch-util"))
  .settings(commonSettings)
  .settings(
    name := "elasticsearch-util",
    description := "Elasticsearch related utils",
    version := "3.3.0",
    resolvers ++= Seq(
      resolverElasticsearch
    ),
    libraryDependencies ++= depElasticsearchUtil
  )

lazy val neo4jConfig = (project in file("neo4j-config"))
  .settings(commonSettings)
  .settings(
    name := "neo4j-config",
    description := "Neo4j config reader",
    version := "0.1.0",
    libraryDependencies ++= depNeo4jConfig
  )

lazy val neo4jUtils = (project in file("neo4j-utils"))
  .settings(commonSettings)
  .settings(
    name := "neo4j-utils",
    description := "Neo4j utils",
    version := "0.2.1",
    libraryDependencies ++= depNeo4jUtils
  )

/*
 * MODULE DEPENDENCIES
 ********************************************************/

lazy val depElasticsearchClientBinary = Seq(
  elasticSearch,
  elasticSearchTransport,
  elasticsearchXPack,
  luceneCore,
  ubirchUtilConfig,
  ubirchUtilDeepCheckModel,
  ubirchUtilJson,
  ubirchUtilUuid,
  scalaTest % "test"
) ++ json4sBase ++ depSlf4jLogging ++ depLog4jToSlf4j

lazy val depElasticsearchUtil = Seq(
  elasticSearch,
  elasticSearchTransport,
  elasticsearchXPack,
  luceneCore,
  scalaLoggingSlf4j
)

lazy val depNeo4jConfig = Seq(
  ubirchUtilConfig
)

lazy val depNeo4jUtils = Seq(
  ubirchUtilNeo4jConfig,
  neo4jJavaDriver,
  scalaLoggingSlf4j
) ++ joda


/*
 * DEPENDENCIES
 ********************************************************/

val elasticsearchV = "6.7.1"
val scalaTestV = "3.0.5"
val json4sV = "3.6.0"
val log4jV = "2.8.2"

val log4jG = "org.apache.logging.log4j"
lazy val json4sBase = Seq(
  json4sCore,
  json4sJackson,
  json4sExt
)
lazy val json4sJackson = "org.json4s" %% "json4s-jackson" % json4sV
lazy val json4sCore = "org.json4s" %% "json4s-core" % json4sV
lazy val json4sExt = "org.json4s" %% "json4s-ext" % json4sV
lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV
val jodaTime = "joda-time" % "joda-time" % "2.10"
val jodaConvert = "org.joda" % "joda-convert" % "2.1.1"
val joda = Seq(jodaTime, jodaConvert)
val elasticSearch = "org.elasticsearch" % "elasticsearch" % elasticsearchV
val elasticSearchTransport = "org.elasticsearch.client" % "transport" % elasticsearchV
val elasticsearchXPack = "org.elasticsearch.client" % "x-pack-transport" % elasticsearchV
val luceneCore = "org.apache.lucene" % "lucene-core" % "7.7.1"
val neo4jJavaDriver = "org.neo4j.driver" % "neo4j-java-driver" % "1.6.2"
lazy val scalaLoggingSlf4j = "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.7"
lazy val log4jApi = log4jG % "log4j-api" % log4jV
lazy val log4jToSlf4j = "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.7"

lazy val depSlf4jLogging = Seq(
  scalaLoggingSlf4j,
  slf4j,
  logbackClassic
)
lazy val depLog4jToSlf4j = Seq(
  log4jApi,
  log4jToSlf4j
)
lazy val ubirchUtilConfig = ubirchUtilGroup %% "config" % "0.2.3"
lazy val ubirchUtilDeepCheckModel = ubirchUtilGroup %% "deep-check-model" % "0.3.1"
lazy val ubirchUtilJson = ubirchUtilGroup %% "json" % "0.5.1"
lazy val ubirchUtilNeo4jConfig = ubirchUtilGroup %% "neo4j-config" % "0.1.0"
lazy val ubirchUtilUuid = ubirchUtilGroup %% "uuid" % "0.1.3"
