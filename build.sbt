import sbt.Keys._

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature")
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(crypto)

lazy val crypto = project
  .settings(commonSettings: _*)
  .settings(

    name := "util-crypto",
    version := "0.1",

    resolvers ++= Seq("RoundEights" at "http://maven.spikemark.net/roundeights"),

    libraryDependencies ++= {

      val scalaTestV = "3.0.0"

      Seq(

        "com.roundeights" %% "hasher" % "1.2.0",

        // test
        "org.scalatest" %% "scalatest" % scalaTestV % "test",
        "joda-time" % "joda-time" % "2.9.4" % "test"

      )
    },

    homepage := Some(url("https://gitlab.com/ubirch/ubirch-util/crypto")),
    scmInfo := Some(ScmInfo(
      url("https://gitlab.com/ubirch/ubirch-util/"),
      "scm:git:git@gitlab.com:ubirch/ubirch-util.git"
    ))

  )

