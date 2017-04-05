import sbt.Keys.libraryDependencies

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
  )),

  resolvers ++= Seq(
    sonatypeReleases,
    sonatypeSnapshots
  )

)

/*
 * MODULES
 ********************************************************/

lazy val scalaUtils = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(
    config,
    crypto,
    date,
    elasticsearchClientBinary,
    elasticsearchUtil,
    futures,
    json,
    jsonAutoConvert,
    mongoUtils,
    oidcUtils,
    redisTestUtil,
    redisUtil,
    restAkkaHttp,
    restAkkaHttpTest,
    uuid
  )

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
    version := "0.3.3",
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
  .dependsOn(config)
  .settings(
    name := "elasticsearch-client-binary",
    description := "Elasticsearch client using the binary TransportClient",
    version := "0.6.1",
    libraryDependencies ++= depElasticsearchClientBinary
  )

lazy val elasticsearchUtil = (project in file("elasticsearch-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "elasticsearch-util",
    description := "Elasticsearch related utils",
    version := "0.1.0",
    resolvers ++= Seq(
      resolverBeeClient
    ),
    libraryDependencies ++= depElasticsearchUtil
  )

lazy val futures = project
  .settings(commonSettings: _*)
  .settings(
    description := "utils related to Scala Futures",
    version := "0.1.1"
  )

lazy val json = project
  .settings(commonSettings: _*)
  .settings(
    description := "util to convert from/to JValue objects",
    version := "0.3.3",
    libraryDependencies ++= depJson
  )

lazy val jsonAutoConvert = (project in file("json-auto-convert"))
  .settings(commonSettings: _*)
  .settings(
    name := "json-auto-convert",
    description := "convert objects to/from JSON",
    version := "0.3.3",
    resolvers ++= Seq(
      resolverSeebergerJson
    ),
    libraryDependencies ++= depJsonAutoConvert
  )

lazy val mongoUtils = (project in file("mongo-utils"))
  .settings(commonSettings: _*)
  .settings(
    name := "mongo-utils",
    description := "MongoDB related utils",
    version := "0.1.0",
    libraryDependencies ++= depMongoUtils
  )

lazy val oidcUtils = (project in file("oidc-utils"))
  .settings(commonSettings: _*)
  .settings(
    name := "oidc-utils",
    description := "OpenID Connect related authorization utils",
    version := "0.2.2",
    resolvers ++= Seq(
      resolverHasher
    ),
    libraryDependencies ++= depOidcUtils
  )

lazy val redisTestUtil = (project in file("redis-test-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "redis-test-util",
    description := "Redis related test utils",
    version := "0.1.0",
    libraryDependencies ++= depRedisTestUtils
  )

lazy val redisUtil = (project in file("redis-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "redis-util",
    description := "Redis related utils",
    version := "0.1.0",
    libraryDependencies ++= depRedisUtil
  )

lazy val restAkkaHttp = (project in file("rest-akka-http"))
  .settings(commonSettings: _*)
  .settings(
    name := "rest-akka-http",
    description := "shared custom classes related to akka-http-experimental (for example certain directives)",
    version := "0.3.3", // NOTE: please keep major.minor version synchronized with restAkkaHttpTest
    libraryDependencies += akkaHttp
  )

lazy val restAkkaHttpTest = (project in file("rest-akka-http-test"))
  .settings(commonSettings: _*)
  .settings(
    name := "rest-akka-http-test",
    description := "akka-http-experimental related test utils",
    version := "0.3.3", // NOTE: please keep major.minor version synchronized with restAkkaHttp
    libraryDependencies ++= depRestAkkaHttpTest
  )

lazy val uuid = project
  .settings(commonSettings: _*)
  .settings(
    description := "UUID related utils",
    version := "0.1.1"
  )

lazy val responseUtil = project
  .settings(commonSettings: _*)
  .settings(
    name := "response-util",
    description := "HTTP Response Utils",
    version := "0.1.2",
    libraryDependencies ++= depResponseUtil
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
  ubirchUtilUuid,
  scalaLoggingSlf4j,
  slf4j,
  scalaTest % "test"
) ++ json4sBase

lazy val depElasticsearchUtil = Seq(
  beeClient,
  scalaLoggingSlf4j
)

lazy val depJson = Seq(
  scalaTest % "test",
  jodaTime % "test"
) ++ json4sWitNative

lazy val depJsonAutoConvert = Seq(
  seebergerJson4s,
  ubirchUtilJson
)

lazy val depMongoUtils = Seq(
  reactiveMongo,
  ubirchUtilConfig,
  akkaSlf4j,
  jodaTime,
  jodaConvert,
  scalaTest % "test"
) ++ depSlf4jLogging

lazy val depOidcUtils = Seq(
  akkaHttp,
  json4sNative,
  ubirchUtilJson,
  ubirchUtilCrypto,
  ubirchUtilRedisUtil,
  scalaTest % "test",
  akkaHttpTestkit % "test",
  ubirchUtilRedisTestUtil % "test"
) ++ depSlf4jLogging

lazy val depRedisTestUtils = Seq(
  ubirchUtilRedisUtil
)

lazy val depRedisUtil = Seq(
  akkaActor,
  akkaSlf4j,
  rediscala,
  scalaLoggingSlf4j,
  ubirchUtilConfig
)

lazy val depRestAkkaHttpTest = Seq(
  akkaHttp,
  akkaHttpTestkit,
  scalaTest
)

lazy val depResponseUtil = Seq(
  akkaHttp,
  akkaHttpTestkit % "test",
  scalaTest % "test"
)



/*
 * DEPENDENCIES
 ********************************************************/

// Versions
val json4sV = "3.4.2"
val akkaV = "2.4.17"
val akkaHttpV = "10.0.3"
val elasticsearchV = "2.4.2"
val scalaTestV = "3.0.1"

// Groups
val akkaG = "com.typesafe.akka"

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
lazy val seebergerJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.10.1"

lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

lazy val roundeightsHasher = "com.roundeights" %% "hasher" % "1.2.0"

lazy val netI2pCryptoEddsa = "net.i2p.crypto" % "eddsa" % "0.1.0"

lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaV
lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaV
lazy val akkaHttp = akkaG %% "akka-http" % akkaHttpV
lazy val akkaHttpTestkit = akkaG %% "akka-http-testkit" % akkaHttpV

lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV

lazy val jodaTime = "joda-time" % "joda-time" % "2.9.4"
lazy val jodaConvert = "org.joda" % "joda-convert" % "1.8.1"

lazy val elasticSearch = "org.elasticsearch" % "elasticsearch" % elasticsearchV

lazy val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % "0.12.1"

lazy val scalaLoggingSlf4j = "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.21"
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.7"
lazy val depSlf4jLogging = Seq(
  scalaLoggingSlf4j,
  slf4j,
  logbackClassic
)

lazy val beeClient = "uk.co.bigbeeconsultants" %% "bee-client" % "0.29.1"

lazy val rediscala = "com.github.etaty" %% "rediscala" % "1.8.0" excludeAll ExclusionRule(organization = "com.typesafe.akka")

lazy val ubirchUtilConfig = ubirchUtilGroup %% "config" % "0.1"
lazy val ubirchUtilCrypto = ubirchUtilGroup %% "crypto" % "0.3.3"
lazy val ubirchUtilJson = ubirchUtilGroup %% "json" % "0.3.3"
lazy val ubirchUtilRedisTestUtil = ubirchUtilGroup %% "redis-test-util" % "0.1.0"
lazy val ubirchUtilRedisUtil = ubirchUtilGroup %% "redis-util" % "0.1.0"
lazy val ubirchUtilUuid = ubirchUtilGroup %% "uuid" % "0.1.1"

/*
 * RESOLVER
 ********************************************************/

val sonatypeReleases = Resolver.sonatypeRepo("releases")
val sonatypeSnapshots = Resolver.sonatypeRepo("snapshots")
val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")
val resolverHasher = "RoundEights" at "http://maven.spikemark.net/roundeights"
val resolverBeeClient = Resolver.bintrayRepo("rick-beton", "maven")
