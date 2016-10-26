packagedArtifacts in file(".") := Map.empty // disable publishing of root project

lazy val ubirchUtilGroup = "com.ubirch.util"
lazy val commonSettings = Seq(

  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature"),

  organization := ubirchUtilGroup,

  homepage := Some(url("http://ubirch.com")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/ubirch/ubirch-scala-utils"),
    "https://github.com/ubirch/ubirch-scala-utils.git"
  ))

)

/*
 * MODULES
 ********************************************************/

lazy val scalaUtils = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(config, crypto, date, elasticsearchClientBinary, json, jsonAutoConvert, restAkkaHttp, uuid)

lazy val config = project
  .settings(commonSettings: _*)
    .settings(
      description := "common config related code",
      version := "0.1",
      libraryDependencies += typesafeConfig
    )

lazy val crypto = project
  .settings(commonSettings: _*)
  .settings(
    description := "ubirch util with crypto related code",
    version := "0.2",
    resolvers ++= Seq(
      resolverHasher
    ),
    libraryDependencies ++= depCrypto
  )

lazy val date = project
  .settings(commonSettings: _*)
  .settings(
    description := "a collection of date related utils",
    version := "0.1",
    libraryDependencies ++= Seq(
      jodaTime
    )
  )

lazy val elasticsearchClientBinary = (project in file("elasticsearch-client-binary"))
  .settings(commonSettings: _*)
  .settings(
    name := "elasticsearch-client-binary",
    description := "Elasticsearch client using the binary TransportClient",
    version := "0.2.1",
    libraryDependencies ++= depElasticsearchClientBinary
  )

lazy val json = project
  .settings(commonSettings: _*)
  .settings(
    description := "util to convert from/to JValue objects",
    version := "0.1",
    libraryDependencies ++= depJson
  )

lazy val jsonAutoConvert = (project in file("json-auto-convert"))
  .settings(commonSettings: _*)
  .settings(
    name := "json-auto-convert",
    description := "convert requests/responses to/from JSON",
    version := "0.2",
    resolvers ++= Seq(
      resolverSeebergerJson
    ),
    libraryDependencies ++= json4sWithSeeberger
  )

lazy val restAkkaHttp = (project in file("rest-akka-http"))
  .settings(commonSettings: _*)
  .settings(
    name := "rest-akka-http",
    description := "shared custom classes related to akka-http-experimental (for example certain directives)",
    version := "0.3",
    libraryDependencies += akkaHttp
  )

lazy val uuid = project
  .settings(commonSettings: _*)
  .settings(
    description := "UUID related utils",
    version := "0.1"
  )

/*
 * MODULE DEPENDENCIES
 ********************************************************/

lazy val depCrypto = Seq(
  roundeightsHasher,
  scalaTest % "test",
  jodaTime % "test",
  jodaConvert % "test"
)

lazy val depElasticsearchClientBinary = Seq(
  elasticSearch,
  ubirchUtilJson,
  scalaLoggingSlf4j,
  slf4j,
  scalaTest % "test"
) ++ json4sBase

lazy val depJson = Seq(
  scalaTest % "test",
  jodaTime % "test"
) ++ json4sWitNative

/*
 * DEPENDENCIES
 ********************************************************/

val json4sV = "3.4.0"
val akkaV = "2.4.11"
val elasticsearchV = "2.4.0"
val scalaTestV = "3.0.0"

lazy val json4sBase = Seq(
  json4sCore,
  json4sJackson,
  json4sExt
)
lazy val json4sWitNative = json4sBase :+ json4sNative
lazy val json4sWithSeeberger = json4sBase :+ seebergerJson4s

lazy val json4sJackson = "org.json4s" %% "json4s-jackson" % json4sV
lazy val json4sCore = "org.json4s" %% "json4s-core" % json4sV
lazy val json4sExt = "org.json4s" %% "json4s-ext" % json4sV
lazy val json4sNative = "org.json4s" %% "json4s-native" % json4sV
lazy val seebergerJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.8.0"

lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

lazy val roundeightsHasher = "com.roundeights" %% "hasher" % "1.2.0"

lazy val akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % akkaV

lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV

lazy val jodaTime = "joda-time" % "joda-time" % "2.9.4"
lazy val jodaConvert = "org.joda" % "joda-convert" % "1.8"

lazy val elasticSearch = "org.elasticsearch" % "elasticsearch" % elasticsearchV
lazy val scalaLoggingSlf4j = "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"

lazy val ubirchUtilJson = ubirchUtilGroup %% "json" % "0.1"

/*
 * RESOLVER
 ********************************************************/

lazy val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")
lazy val resolverHasher = "RoundEights" at "http://maven.spikemark.net/roundeights"
