# ubirch Utils

## List of Modules in this Repository

* config
* crypto
* date
* elasticsearch-client-binary
* elasticsearch-util
* futures
* json
* json-auto-convert
* response-util
* rest-akka-http
* rest-akka-http-test
* uuid

## `config`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "config" % "0.1"
    )


## `crypto`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
	  "RoundEights" at "http://maven.spikemark.net/roundeights"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "crypto" % "0.3.3"
    )

### Release History

#### Version 0.3.3 (2016-11-09)

* fixed refactoring bug

#### Version 0.3.2 (2016-11-09)

* removed external dependency net.i2p.crypto" % "eddsa" % "0.1.0"
* we use a local copy of that project

#### Version 0.3.1 (2016-11-09)

* added new methods to HashUtil:
  * sha256Base64(Array[Byte]
  * sha512Base64(Array[Byte]

#### Version 0.3 (2016-10-28)

* migrated crypto code from old ubirch project to this util module.


-----------------------

## `date`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "date" % "0.1"
    )

### Release History

#### Version 0.1 (2016-09-22)

* initial release


-----------------------

## `elasticsearch-client-binary`

A client for Elasticsearch 2.4 using the binary protocol through
[TransportClient](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html).
To use it mixin the trait `ESSimpleStorage` or `ESBulkStorage` and add the configuration documented below to your
project.

In addition to this there's some other utils as well:

* `SortUtil`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "elasticsearch-client-binary" % "0.5.0"
    )



### Config

The following config is required to use the Elasticsearch binary client (**NOTE: there's no default config!!!**).

| Config Item                            | Category        | Description                                             |
|:---------------------------------------|:----------------|:--------------------------------------------------------|
| esBinaryClient.connection.host         | Connection      | host ES is running on                                   | 
| esBinaryClient.connection.port         | Connection      | port ES is running on                                   | 
| esBinaryClient.connection.cluster      | Connection      | (optional) ES cluster to connect to                     | 
| esBinaryClient.bulk.bulkActions        | Flush           | max number of items to trigger flush                    | 
| esBinaryClient.bulk.bulkSize           | Flush           | max size of of all documents (in mega bytes)) to trigger flush |
| esBinaryClient.bulk.flushInterval      | Flush           | maximum number of seconds between flushes               |
| esBinaryClient.bulk.concurrentRequests | Connection Pool | maximum number of concurrent requests                   |

Example Config:

    esBinaryClient {
      connection {
        host = localhost
        port = 9300 // 9300 is the default port for the binary client
        cluster = elasticsearch // (optional) connect to an Elasticsearch cluster
      }
      bulk { // only needed if you mixin `ESBulkStorage`
        bulkActions = 10000
        bulkSize = 10 # bulkSize in mega bytes
        flushInterval = 1 # flush every x seconds
        concurrentRequests = 2 # connection pooling: max concurrent requests
      }
    }

### Release History

#### Version 0.5.0 (2017-02-24)

**This version is not compatible with 0.4.x releases**

* enabled cluster support
* simplified usage in projects by adding host, port and cluster

#### Version 0.4.1 (2016-12-14)

* added access to current ElasticSearch Client 
    * ElasticsearchStorage.getCurrentEsClient
    * ElasticsearchBulkStorage.getCurrentEsClient

#### Version 0.4.0 (2016-12-13)

* `ElasticsearchBulkStorage` parameters are read from a config now (**NOTE: there's no default config!!!**)

| Config Item                            | Category        | Description                                             |
|:---------------------------------------|:----------------|:--------------------------------------------------------|
| esBinaryClient.bulk.bulkActions        | Flush           | max number of items                                     | 
| esBinaryClient.bulk.bulkSize           | Flush           | max size of documents of all documents (in mega bytes)) |
| esBinaryClient.bulk.flushInterval      | Flush           | maximum number of seconds                               |
| esBinaryClient.bulk.concurrentRequests | Connection Pool | maximum number of concurrent requests                   |

Example Config:

    esBinaryClient {
      bulk {
        bulkActions = 2000 # flush: max number of changes
        bulkSize = 10 # flush: bulkSize in mega bytes
        flushInterval = 5 # flush: flush every x seconds
        concurrentRequests = 2 # connection pool: max concurrent requests
      }
    }

#### Version 0.3.5 (2016-11-30)

* `ElasticsearchStorage` now catches `SearchParseException`, too
* improved logging

#### Version 0.3.4 (2016-11-28)

* upgrade `uuid` dependency to version 0.1.1

#### Version 0.3.3 (2016-11-25)

* switch to com.typesafe.scalalogging.slf4j.StrictLogging

#### Version 0.3.2 (2016-11-25)

* update dependencies to use Elasticsearch 2.4.2

#### Version 0.3.1 (2016-11-24)

* bugfix: `ElasticsearchBulkStorage.bulkProcessor` must be lazy

#### Version 0.3.0 (2016-11-23)

* refactored `ElasticsearchBulkStorage.storeBulkData()` method to be functionally equivalent to `ElasticsearchStorage.storeDoc()`
* minor refactoring in ElasticsearchStorage

#### Version 0.2.10 (2016-11-09)

* `ttl` in `ElasticsearchStorage.storeDoc()` is now zero by default
* `timestamp` in `ElasticsearchStorage.storeDoc()` is now None by default

#### Version 0.2.9 (2016-11-06)

* `ElasticsearchStorage.storeDoc()` now supports timestamp functionality

#### Version 0.2.8 (2016-11-04)

* `ElasticsearchStorage` references JsonFormats.default now

#### Version 0.2.7 (2016-11-04)

* update dependency com.ubirch.util:json from version 0.3.1 to 0.3.2.

#### Version 0.2.6 (2016-11-01)

* update dependency com.ubirch.util:json from version 0.3 to 0.3.1.

#### Version 0.2.5 (2016-11-01)

* update dependency com.ubirch.util:json from version 0.2 to 0.3.

#### Version 0.2.4 (2016-10-31)

* update dependency com.ubirch.util:json from version 0.1 to 0.2.

#### Version 0.2.3 (2016-10-27)

* added `SortUtil`.

#### Version 0.2.2 (2016-10-26)

* added sort parameter to `ElasticsearchStorage.getDocs`.

#### Version 0.2.1 (2016-10-26)

* additional check: `from` and `size` parameters in `ElasticsearchStorage.getDocs` may not be negative.

#### Version 0.2 (2016-10-25)

* docId in `ElasticsearchStorage.storeDoc` is now optional.

#### Version 0.1

* first release


-----------------------

## `elasticsearch-util`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.bintrayRepo("rick-beton", "maven")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "elasticsearch-util" % "0.1.0"
    )

### Release History

#### Version 0.1.0
     
* first release


-----------------------

## `futures`

Utils related to Scala Futures.

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "futures" % "0.1.0"
    )

### Release History

#### Version 0.1.1 (2016-12-12)

* switch input to type `Seq`

#### Version 0.1.0 (2016-12-12)

* initial release


-----------------------

## `json`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "json" % "0.3.2"
    )

### Release History

#### Version 0.3.2 (2016-11-04)

* introduced JsonFormats.default to have one fixed list of default formats

#### Version 0.3.1 (2016-11-01)

* fixed Json4sUtil.inputstream2jvalue()

#### Version 0.3 (2016-11-01)

* updated json4s dependencies to verion 3.4.2

#### Version 0.2 (2016-10-28)

* deleted method Json4sUtil.string2Any
* added method Json4sUtil.inputstream2jvalue


-----------------------

## `json-auto-convert`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.bintrayRepo("hseeberger", "maven")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "json-auto-convert" % "0.3.1"
    )

### Release History

#### Version 0.3.2 (2016-11-04)

* add com.ubirch.util:json:0.3.2 dependency for it's default formats

#### Version 0.3.1 (2016-11-02)

* update dependency "de.heikoseeberger":"akka-http-json4s" from version 1.8.0 to 1.10.1

#### Version 0.3 (2016-11-01)

* update json4s dependency from version 3.4.0 to 3.4.2


-----------------------

## `response-util`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "response-util" % "0.1.2"
    )

### Release History

#### Version 0.1.2 (2017-02-16)

* update to Akka HTTP 10.0.3

#### Version 0.1.1 (2017-02-10)

* changed artifact name from `responseutil` to `response-util`
* refactor `ResponseUtil` to allow passing in http status codes (only for errors))


-----------------------

## `rest-akka-http`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "rest-akka-http" % "0.3.3" // for Akka HTTP 10.0.3
      "com.ubirch.util" %% "rest-akka-http" % "0.3.1" // for Akka HTTP 2.4.11.1
      "com.ubirch.util" %% "rest-akka-http" % "0.3" // for Akka HTTP 2.4.11
      "com.ubirch.util" %% "rest-akka-http" % "0.2" // for Akka HTTP 2.4.10
      "com.ubirch.util" %% "rest-akka-http" % "0.1" // for Akka HTTP 2.4.9-RC2
    )

### Release History

#### Version 0.3.3 (2017-02-16)

* add `Authorization` to `Access-Control-Allow-Headers`

#### Version 0.3.2 (2017-02-16)

* update to Akka HTTP 10.0.3

#### Version 0.3.1 (2017-02-16)

* update to Akka HTTP 2.4.11.1


-----------------------

## `rest-akka-http-test`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "rest-akka-http-test" % "0.3.3" // for Akka HTTP 10.0.3
      "com.ubirch.util" %% "rest-akka-http-test" % "0.3.1" // for Akka HTTP 2.4.11.1
      "com.ubirch.util" %% "rest-akka-http-test" % "0.3" // for Akka HTTP 2.4.11
    )

### Release History

#### Version 0.3.3 (2017-02-17)

* no changes
* incremented version to remain the as for module `rest-akka-http`

#### Version 0.3.2 (2017-02-16)

* update to Akka HTTP 10.0.3

#### Version 0.3.1 (2017-02-16)

* update to Akka HTTP 2.4.11.1

#### Version 0.3 (2016-11-17)

* initial release for Akka 2.4.11


-----------------------

## `uuid`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "uuid" % "0.1.1"
    )

### Release History

#### Version 0.1.1 (2016-11-28)

* add method `UUIDUtil.fromString`
