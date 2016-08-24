packagedArtifacts in file(".") := Map.empty // disable publishing of root project

lazy val commonSettings = Seq(

  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature"),

  organization := "com.ubirch.util",

  homepage := Some(url("http://ubirch.com")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/ubirch/ubirch-scala-utils"),
    "https://github.com/ubirch/ubirch-scala-utils.git"
  ))

)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(config, crypto, jsonAutoConvert)

lazy val config = project
  .settings(commonSettings: _*)
    .settings(
      description := "common config related code",
      version := "0.1-SNAPSHOT",
      libraryDependencies += typesafeConfig
    )

lazy val crypto = project
  .settings(commonSettings: _*)
  .settings(
    description := "ubirch util with crypto related code",
    version := "0.2-SNAPSHOT",
    resolvers ++= Seq(
      "RoundEights" at "http://maven.spikemark.net/roundeights"
    ),
    libraryDependencies ++= depCrypto
  )

lazy val jsonAutoConvert = (project in file("json-auto-convert"))
  .settings(commonSettings: _*)
  .settings(
    name := "json-auto-convert",
    description := "convert requests/responses to/from JSON",
    version := "0.1-SNAPSHOT",
    resolvers ++= Seq(
      Resolver.bintrayRepo("hseeberger", "maven")
    ),
    libraryDependencies ++= json4sWithSeeberger
  )

lazy val depCrypto = Seq(
  roundeightsHasher,
  scalaTest % "test",
  jodaTime % "test"

)

val json4sV = "3.4.0"
val scalaTestV = "3.0.0"

lazy val json4sBase = Seq(
  json4sCore,
  json4sJackson,
  json4sExt
)

lazy val json4sWithSeeberger = json4sBase :+ seebergerJson4s

lazy val json4sJackson = "org.json4s" %% "json4s-jackson" % json4sV
lazy val json4sCore = "org.json4s" %% "json4s-core" % json4sV
lazy val json4sExt = "org.json4s" %% "json4s-ext" % json4sV
lazy val json4sNative = "org.json4s" %% "json4s-native" % json4sV
lazy val seebergerJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.8.0"

lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

lazy val roundeightsHasher = "com.roundeights" %% "hasher" % "1.2.0"

lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV

lazy val jodaTime = "joda-time" % "joda-time" % "2.9.4"
