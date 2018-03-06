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
    camelUtils,
    config,
    crypto,
    date,
    deepCheckModel,
    elasticsearchClientBinary,
    elasticsearchUtil,
    futures,
    json,
    mongoTestUtils,
    mongoUtils,
    oidcUtils,
    redisTestUtil,
    redisUtil,
    responseUtil,
    restAkkaHttp,
    restAkkaHttpTest,
    uuid
  )

lazy val camelUtils = (project in file("camel-utils"))
  .settings(commonSettings: _*)
  .settings(
    name := "camel-utils",
    description := "Camel related utils",
    version := "0.1.0",
    libraryDependencies ++= depCamelUtils
  )

lazy val config = project
  .settings(commonSettings: _*)
  .settings(
    description := "common config related code",
    version := "0.2-SNAPSHOT",
    libraryDependencies ++= depConfig
  )

lazy val crypto = project
  .settings(commonSettings: _*)
  .settings(
    description := "ubirch util with crypto related code",
    version := "0.4.2",
    resolvers ++= Seq(
      resolverHasher
    ),
    libraryDependencies ++= depCrypto
  )

lazy val date = project
  .settings(commonSettings: _*)
  .settings(
    description := "a collection of date related utils",
    version := "0.5.1",
    libraryDependencies ++= depDate
  )

lazy val deepCheckModel = (project in file("deep-check-model"))
  .settings(commonSettings: _*)
  .settings(
    name := "deep-check-model",
    description := "actor and JSON models for the /deepCheck endpoints",
    version := "0.2.0",
    libraryDependencies ++= depDeepCheckModel
  )

lazy val elasticsearchClientBinary = (project in file("elasticsearch-client-binary"))
  .settings(commonSettings: _*)
  .dependsOn(config)
  .settings(
    name := "elasticsearch-client-binary",
    description := "Elasticsearch client using the binary TransportClient",
    version := "2.3.4",
    resolvers ++= Seq(
      resolverElasticsearch
    ),
    libraryDependencies ++= depElasticsearchClientBinary
  )

lazy val elasticsearchUtil = (project in file("elasticsearch-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "elasticsearch-util",
    description := "Elasticsearch related utils",
    version := "2.3.2",
    resolvers ++= Seq(
      resolverElasticsearch
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
    description := "collection of JSON utils",
    version := "0.4.3",
    resolvers ++= Seq(
      resolverSeebergerJson
    ),
    libraryDependencies ++= depJson
  )

lazy val mongoTestUtils = (project in file("mongo-test-utils"))
  .settings(commonSettings: _*)
  .settings(
    name := "mongo-test-utils",
    description := "MongoDB related test utils",
    version := "0.3.6",
    libraryDependencies ++= depMongoTestUtils
  )

lazy val mongoUtils = (project in file("mongo-utils"))
  .settings(commonSettings: _*)
  .settings(
    name := "mongo-utils",
    description := "MongoDB related utils",
    version := "0.3.6",
    libraryDependencies ++= depMongoUtils
  )

lazy val oidcUtils = (project in file("oidc-utils"))
  .settings(commonSettings: _*)
  .settings(
    name := "oidc-utils",
    description := "OpenID Connect related authorization utils",
    version := "0.4.15-SNAPSHOT",
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
    version := "0.3.4",
    libraryDependencies ++= depRedisTestUtils
  )

lazy val redisUtil = (project in file("redis-util"))
  .settings(commonSettings: _*)
  .settings(
    name := "redis-util",
    description := "Redis related utils",
    version := "0.3.4",
    libraryDependencies ++= depRedisUtil
  )

lazy val responseUtil = project
  .settings(commonSettings: _*)
  .settings(
    name := "response-util",
    description := "HTTP Response Utils",
    version := "0.2.4",
    libraryDependencies ++= depResponseUtil
  )

lazy val restAkkaHttp = (project in file("rest-akka-http"))
  .settings(commonSettings: _*)
  .settings(
    name := "rest-akka-http",
    description := "shared custom classes related to akka-http-experimental (for example certain directives)",
    version := "0.3.8", // NOTE: please keep major.minor version synchronized with restAkkaHttpTest
    libraryDependencies ++= depRestAkkaHttp
  )

lazy val restAkkaHttpTest = (project in file("rest-akka-http-test"))
  .settings(commonSettings: _*)
  .settings(
    name := "rest-akka-http-test",
    description := "akka-http-experimental related test utils",
    version := "0.3.8", // NOTE: please keep major.minor version synchronized with restAkkaHttp
    libraryDependencies ++= depRestAkkaHttpTest
  )

lazy val uuid = project
  .settings(commonSettings: _*)
  .settings(
    description := "UUID related utils",
    version := "0.1.1"
  )

/*
 * MODULE DEPENDENCIES
 ********************************************************/

lazy val depCamelUtils = Seq(
  scalaTest % "test"
)

lazy val depConfig = Seq(
  typesafeConfig,
  scalaTest % "test"
)

lazy val depCrypto = Seq(
  roundeightsHasher,
  apacheCommonsCodec,
  scalaTest % "test",
  jodaTime % "test",
  jodaConvert % "test"
) ++ depSlf4jLogging

lazy val depDate = Seq(
  jodaTime,
  scalaTest % "test"
)

lazy val depDeepCheckModel = Seq(
  ubirchUtilJson,
  scalaTest % "test"
) ++ json4sWitNative

lazy val depElasticsearchClientBinary = Seq(
  elasticSearch,
  elasticsearchXPack,
  ubirchUtilJson,
  ubirchUtilDeepCheckModel,
  ubirchUtilUuid,
  scalaTest % "test"
) ++ json4sBase ++ depSlf4jLogging ++ depLog4jToSlf4j

lazy val depElasticsearchUtil = Seq(
  elasticSearch,
  scalaLoggingSlf4j
)

lazy val depJson = Seq(
  seebergerJson4s,
  scalaTest % "test",
  jodaTime % "test"
) ++ json4sWitNative

lazy val depMongoTestUtils = Seq(
  ubirchUtilMongoUtils
)

lazy val depMongoUtils = Seq(
  akkaSlf4j,
  reactiveMongo,
  ubirchUtilDeepCheckModel,
  ubirchUtilConfig,
  jodaTime,
  jodaConvert,
  scalaTest % "test"
) ++ depSlf4jLogging

lazy val depOidcUtils = Seq(
  ubirchUserRest,
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
  ubirchUtilConfig,
  ubirchUtilDeepCheckModel
)

lazy val depRestAkkaHttp = Seq(
  akkaHttp,
  akkaHttpCors
)

lazy val depRestAkkaHttpTest = Seq(
  akkaHttp,
  akkaHttpTestkit,
  scalaTest
)

lazy val depResponseUtil = Seq(
  akkaHttp,
  ubirchUtilJson,
  akkaHttpTestkit % "test",
  scalaTest % "test"
)


/*
 * DEPENDENCIES
 ********************************************************/

// Versions
val json4sV = "3.5.2"
val akkaV = "2.4.18"
val akkaHttpV = "10.0.9"
val elasticsearchV = "5.6.5"
val log4jV = "2.8.2"
val scalaTestV = "3.0.1"

// Groups
val akkaG = "com.typesafe.akka"
val log4jG = "org.apache.logging.log4j"

lazy val json4sBase = Seq(
  json4sCore,
  json4sJackson,
  json4sExt
)
lazy val json4sWitNative = json4sBase :+ json4sNative

lazy val json4sJackson = "org.json4s" %% "json4s-jackson" % json4sV
lazy val json4sCore = "org.json4s" %% "json4s-core" % json4sV
lazy val json4sExt = "org.json4s" %% "json4s-ext" % json4sV
lazy val json4sNative = "org.json4s" %% "json4s-native" % json4sV
lazy val seebergerJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.14.0"

lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

lazy val roundeightsHasher = "com.roundeights" %% "hasher" % "1.2.0"

lazy val apacheCommonsCodec = "commons-codec" % "commons-codec" % "1.11"

lazy val netI2pCryptoEddsa = "net.i2p.crypto" % "eddsa" % "0.1.0"

lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaV
lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaV
lazy val akkaHttp = akkaG %% "akka-http" % akkaHttpV
lazy val akkaHttpTestkit = akkaG %% "akka-http-testkit" % akkaHttpV

lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV

lazy val jodaTime = "joda-time" % "joda-time" % "2.9.4"
lazy val jodaConvert = "org.joda" % "joda-convert" % "1.8.1"

lazy val elasticSearch = "org.elasticsearch" % "elasticsearch" % elasticsearchV
lazy val elasticsearchXPack = "org.elasticsearch.client" % "x-pack-transport" % elasticsearchV

lazy val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % "0.12.5" excludeAll ExclusionRule(organization = s"${akkaActor.organization}", name = s"${akkaActor.name}")

// https://github.com/lomigmegard/akka-http-cors
lazy val akkaHttpCors = "ch.megard" %% "akka-http-cors" % "0.2.1"

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

val ubirchUserG = "com.ubirch.user"
val ubirchUserV = "0.6.4"

lazy val rediscala = "com.github.etaty" %% "rediscala" % "1.8.0" excludeAll ExclusionRule(organization = s"${akkaActor.organization}", name = s"${akkaActor.name}")

lazy val ubirchUtilConfig = ubirchUtilGroup %% "config" % "0.1"
lazy val ubirchUtilCrypto = ubirchUtilGroup %% "crypto" % "0.4.2"
lazy val ubirchUtilDeepCheckModel = ubirchUtilGroup %% "deep-check-model" % "0.2.0"
lazy val ubirchUtilJson = ubirchUtilGroup %% "json" % "0.4.3"
lazy val ubirchUtilMongoUtils = ubirchUtilGroup %% "mongo-utils" % "0.3.6"
lazy val ubirchUtilRedisTestUtil = ubirchUtilGroup %% "redis-test-util" % "0.3.4"
lazy val ubirchUtilRedisUtil = ubirchUtilGroup %% "redis-util" % "0.3.4"
lazy val ubirchUtilUuid = ubirchUtilGroup %% "uuid" % "0.1.1"
lazy val ubirchUserRest = ubirchUserG %% "client-rest" % ubirchUserV
/*
 * RESOLVER
 ********************************************************/

val sonatypeReleases = Resolver.sonatypeRepo("releases")
val sonatypeSnapshots = Resolver.sonatypeRepo("snapshots")
val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")
val resolverHasher = "RoundEights" at "http://maven.spikemark.net/roundeights"
val resolverElasticsearch = "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
