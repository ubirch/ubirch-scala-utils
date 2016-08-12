packagedArtifacts in file(".") := Map.empty // disable publishing of root project

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature"),
  organization := "com.ubirch.util"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(crypto)

lazy val crypto = project
  .settings(commonSettings: _*)
  .settings(

    name := "crypto",
    description := "ubirch util with crypto related code",
    version := "0.2-SNAPSHOT",

    libraryDependencies ++= depCrypto,

    homepage := Some(url("https://github.com/ubirch/ubirch-scala-utils")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/ubirch/ubirch-scala-utils"),
      "https://github.com/ubirch/ubirch-scala-utils.git"
    ))

  )

val scalaTestV = "3.0.0"

resolvers ++= Seq("RoundEights" at "http://maven.spikemark.net/roundeights")

lazy val depCrypto = Seq(

  "com.roundeights" %% "hasher" % "1.2.0",

  // test
  "org.scalatest" %% "scalatest" % scalaTestV % "test",
  "joda-time" % "joda-time" % "2.9.4" % "test"

)