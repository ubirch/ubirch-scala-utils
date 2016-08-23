# ubirch Utils

## crypto

### Scala Dependency

#### `crypto`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "crypto" % "0.2-SNAPSHOT"
    )

#### `json-auto-convert`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.bintrayRepo("hseeberger", "maven")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "json-auto-convert" % "0.1-SNAPSHOT"
    )
