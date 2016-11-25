# ubirch Utils

## crypto

### Scala Dependency

#### `config`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "config" % "0.1"
    )


#### `crypto`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
	  "RoundEights" at "http://maven.spikemark.net/roundeights"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "crypto" % "0.3.3"
    )

##### Release History

###### Version 0.3.3 (2016-11-09)

* fixed refactoring bug

###### Version 0.3.2 (2016-11-09)

* removed external dependency net.i2p.crypto" % "eddsa" % "0.1.0"
 * we use a local copy of that project

###### Version 0.3.1 (2016-11-09)

* added new methods to HashUtil:
  * sha256Base64(Array[Byte]
  * sha512Base64(Array[Byte]

###### Version 0.3 (2016-10-28)

migrated crypto code from old ubirch project to this util module.


#### `date`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "date" % "0.1"
    )


#### `elasticsearch-client-binary`

A client for Elasticsearch 2.4 using the binary protocol through
[TransportClient](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html).

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "elasticsearch-client-binary" % "0.3.3"
    )

##### Release History

###### Version 0.3.3 (2016-11-25)

* switch to com.typesafe.scalalogging.slf4j.StrictLogging

###### Version 0.3.2 (2016-11-25)

* update dependencies to use Elasticsearch 2.4.2

###### Version 0.3.1 (2016-11-24)

* bugfix: `ElasticsearchBulkStorage.bulkProcessor` must be lazy

###### Version 0.3.0 (2016-11-23)

* refactored `ElasticsearchBulkStorage.storeBulkData()` method to be functionally equivalent to `ElasticsearchStorage.storeDoc()`
* minor refactoring in ElasticsearchStorage

###### Version 0.2.10 (2016-11-09)

* `ttl` in `ElasticsearchStorage.storeDoc()` is now zero by default
* `timestamp` in `ElasticsearchStorage.storeDoc()` is now None by default

###### Version 0.2.9 (2016-11-06)

* `ElasticsearchStorage.storeDoc()` now supports timestamp functionality

###### Version 0.2.8 (2016-11-04)

* `ElasticsearchStorage` references JsonFormats.default now

###### Version 0.2.7 (2016-11-04)

update dependency com.ubirch.util:json from version 0.3.1 to 0.3.2.

###### Version 0.2.6 (2016-11-01)

update dependency com.ubirch.util:json from version 0.3 to 0.3.1.

###### Version 0.2.5 (2016-11-01)

update dependency com.ubirch.util:json from version 0.2 to 0.3.

###### Version 0.2.4 (2016-10-31)

update dependency com.ubirch.util:json from version 0.1 to 0.2.

###### Version 0.2.3 (2016-10-27)

added `SortUtil`.

###### Version 0.2.2 (2016-10-26)

added sort parameter to `ElasticsearchStorage.getDocs`.

###### Version 0.2.1 (2016-10-26)

additional check: `from` and `size` parameters in `ElasticsearchStorage.getDocs` may not be negative.

###### Version 0.2 (2016-10-25)

docId in `ElasticsearchStorage.storeDoc` is now optional.

###### Version 0.1

first release


#### `json`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "json" % "0.3.2"
    )

##### Release History

###### Version 0.3.2 (2016-11-04)

* introduced JsonFormats.default to have one fixed list of default formats

###### Version 0.3.1 (2016-11-01)

* fixed Json4sUtil.inputstream2jvalue()

###### Version 0.3 (2016-11-01)

* updated json4s dependencies to verion 3.4.2

###### Version 0.2 (2016-10-28)

* deleted method Json4sUtil.string2Any
* added method Json4sUtil.inputstream2jvalue


#### `json-auto-convert`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.bintrayRepo("hseeberger", "maven")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "json-auto-convert" % "0.3.1"
    )

##### Release History

###### Version 0.3.2 (2016-11-04)

* add com.ubirch.util:json:0.3.2 dependency for it's default formats

###### Version 0.3.1 (2016-11-02)

* update dependency "de.heikoseeberger":"akka-http-json4s" from version 1.8.0 to 1.10.1

###### Version 0.3 (2016-11-01)

* update json4s dependency from version 3.4.0 to 3.4.2


#### `rest-akka-http`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "rest-akka-http" % "0.1" // for Akka 2.4.9-RC2
      "com.ubirch.util" %% "rest-akka-http" % "0.2" // for Akka 2.4.10
      "com.ubirch.util" %% "rest-akka-http" % "0.3" // for Akka 2.4.11
    )


#### `rest-akka-http-test`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "rest-akka-http" % "0.3" // for Akka 2.4.11
    )

##### Release History

###### Version 0.3 (2016-11-17)

* initial release for Akka 2.4.11


#### `uuid`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "uuid" % "0.1"
    )
