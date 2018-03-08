# ubirch Utils

## List of Modules in this Repository

* camel-utils
* config
* crypto
* date
* deep-check-model
* elasticsearch-client-binary
* elasticsearch-util
* futures
* json
* mongo-test-utils
* mongo-utils
* oidc-utils
* redis-test-util
* redis-util
* response-util
* rest-akka-http
* rest-akka-http-test
* uuid

## `camel-utils`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "camel-utils" % "0.1.0"
    )

### Release History

#### Version 0.1.0 (2017-10-16)

* initial release


## `config`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "config" % "0.2.0"
    )

### Configuration

#### Environment Id

Version 0.2.0 introduced the mandatory parameter: _ubirch.envid_. It consists of a prefix (e.g. _ubirch_) and a postfix
(_-local_, _-dev_, _-demo_, _-prod_). Some examples:

    ubirch.envid="ubirch-prod"
    ubirch.envid="ubirch-demo"
    ubirch.envid="ubirch-dev"
    ubirch.envid="ubirch-local"

### Release History

#### Version 0.2.0 (2018-03-08)

* added method _ConfigBase.environmentId()_
* added object _EnvironmentUtil_


## `crypto`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
	     "RoundEights" at "http://maven.spikemark.net/roundeights"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "crypto" % "0.4.2"
    )

### Release History

#### Version 0.4.2 (2018-01-22)

* `HashUtil`: replace bcrypt methods with PBKDF2

#### Version 0.4.1 (2018-01-16)

* fixed merge error where `EccUtil.encodePublicKey()` wasn't public

#### Version 0.4.0 (2018-01-16)

* added methods `HashUtil.bcrypt*()`

#### Version 0.3.5 (2017-09-26)

* make `EccUtil.encodePublicKey` public

#### Version 0.3.4 (2017-05-19)

* added EccUtil (sign/validate/keypair gen)

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
      "com.ubirch.util" %% "date" % "0.5.1"
    )

### Release History

#### Version 0.5.1 (2018-02-21)

* added method `DateUtil.toString_YYYY_MM_dd`

#### Version 0.5 (2018-01-15)

* refactored _stepSize_ parameter in `DateUtil.dateRange` from `Period` to `Int`

#### Version 0.4 (2017-11-01)

* add method `DateUtil.todayAtMidnight`

#### Version 0.3 (2017-10-16)

* add method `DateUtil.dateRange`

#### Version 0.2 (2016-09-26)

* add method `DateUtil.parseDateToUTC()`

#### Version 0.1 (2016-09-22)

* initial release


-----------------------

## `deep-check-model`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "deep-check-model" % "0.2.0"
    )

### Release History

#### Version 0.2.0 (2017-07-28)

* add `DeepCheckResponseUtil`

#### Version 0.1.3 (2017-07-27)

* update to `com.ubirch.util:json:0.4.3`

#### Version 0.1.2 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.1.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.1.0 (2017-06-09)

* extracted a refactored `DeepCheckResponse` from _com.ubirch.util:response-util:0.1.6_

-----------------------

## `elasticsearch-client-binary`

A client for Elasticsearch 2.4 using the binary protocol through
[TransportClient](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html).
To use it mixin the trait `ESSimpleStorage` or `ESBulkStorage` and add the configuration documented below to your
project. If you prefer working with objects they exists as well under the same names.

In addition to this there's some other utils as well:

* `SortUtil`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "elasticsearch-client-binary" % "2.3.5"
    )

### Config

The following config is required to use the Elasticsearch binary client (**NOTE: there's no default config!!!**).

| Config Item                             | Category        | Description                                             |
|:----------------------------------------|:----------------|:--------------------------------------------------------|
| esBinaryClient.connection.hosts         | Connection      | list of ES hosts to connect to                          | 
| esBinaryClient.connection.xpackEnabled  | Connection      | (optional) set to "true" to activate Shield/X-Pack (default=false) | 
| esBinaryClient.connection.settings      | Connection      | ES connection settings (like cluster, Shield/X-Pack configs, etc | 
| esBinaryClient.bulk.bulkActions         | Flush           | max number of items to trigger flush                    | 
| esBinaryClient.bulk.bulkSize            | Flush           | max size of of all documents (in mega bytes)) to trigger flush |
| esBinaryClient.bulk.flushInterval       | Flush           | maximum number of seconds between flushes               |
| esBinaryClient.bulk.concurrentRequests  | Connection Pool | maximum number of concurrent requests                   |

Example Config (minimum config to connect to cloud.elastic.co with Shield/X-Pack):

    esBinaryClient {
      connection {
        hosts = ["1234asdf.us-east-1.aws.found.io:9343"]
        xpackEnabled = true
        settings = [
          { "cluster.name": "1234asdf" },
          { "xpack.security.user": ${ELASTIC_IO_USER}":"${ELASTIC_IO_PASSWORD} },
          { "xpack.security.transport.ssl.enabled": "true" },
          { "request.headers.X-Found-Cluster": "${cluster.name}" }
        ]
      }
      bulk { // only needed if you mixin `ESBulkStorage`
        bulkActions = 10000
        bulkSize = 10 # bulkSize in mega bytes
        flushInterval = 1 # flush every x seconds
        concurrentRequests = 2 # connection pooling: max concurrent requests
      }
    }

Example Config (extended config to connect to cloud.elastic.co with Shield/X-Pack):

    esBinaryClient {
      clusterName = "1234asdf"
      connection {
        hosts = [${esBinaryClient.clusterName}".us-east-1.aws.found.io:9343"]
        xpackEnabled = true
        settings = [
          { "cluster.name": ${esBinaryClient.clusterName} },
          { "xpack.security.user": ${ELASTIC_IO_USER}":"${ELASTIC_IO_PASSWORD} },
          { "xpack.security.transport.ssl.enabled": "true" },
          { "request.headers.X-Found-Cluster": "${cluster.name}" },
          { "xpack.ssl.key": "/path/to/client.key" }, // (optional)
          { "xpack.ssl.certificate": "/path/to/client.crt" }, // (optional)
          { "xpack.ssl.certificate_authorities": "/path/to/ca.crt" }, // (optional)
          { "transport.sniff": "true"}, // (optional)
          { "transport.ping_schedule": "5s"}, // (optional)
          { "client.transport.ping_timeout": "10s"}, // (optional) default: 5s
          { "client.transport.nodes_sampler_interval": "10s"} // (optional) default: 5s
        ]
      }
      bulk { // only needed if you mixin `ESBulkStorage`
        bulkActions = 10000
        bulkSize = 10 # bulkSize in mega bytes
        flushInterval = 1 # flush every x seconds
        concurrentRequests = 2 # connection pooling: max concurrent requests
      }
    }

Example Config (simple localhost cluster without Shield/X-Pack):

    esBinaryClient {
      connection {
        hosts = ["localhost:9300", "localhost:9301"]
        settings = [
          { "cluster.name": "my-test-cluster" }
        ]
      }
      bulk { // only needed if you mixin `ESBulkStorage`
        bulkActions = 10000
        bulkSize = 10 # bulkSize in mega bytes
        flushInterval = 1 # flush every x seconds
        concurrentRequests = 2 # connection pooling: max concurrent requests
      }
    }

### Release History

#### Version 2.3.5 (2018-03-08)

* update to `com.ubirch.util:config:0.2.0`

#### Version 2.3.4 (2018-02-21)

* added _ESStorageBase.getAverage()_

#### Version 2.3.3 (2018-01-04)

* changed backoffPolicy
* fixed deprecation warning (IndexRequest.source)

#### Version 2.3.2 (2018-01-03)

* update to Elasticsearch 5.6.5

#### Version 2.3.1 (2017-11-03)

* update to Elasticsearch 5.6.3

#### Version 2.3.0 (2017-11-03)

* update to Elasticsearch 5.6.1

#### Version 2.2.0 (2017-09-26)

* roll back to Elasticsearch 5.3.2

#### Version 2.1.1 (2017-09-26)

* `ESStorageBase.getDoc` catches Elasticsearch 5.5 QueryShardExceptions, too, now (most common cause: no timestamp mapping on empty index)
* change log level from error to info when catching a SearchParseException or QueryShardException in`ESStorageBase.getDoc()` and `ESStorageBase.getDoc()`
* refactored `ESStorageBase.connectivityCheck` to accept a doc index and type 

#### Version 2.1.0 (2017-07-31)

* update to Elasticsearch 5.5.1 while remaining compatible with 5.3.2
* update to `com.ubirch.util:deep-check-model:0.2.0`

##### Known Bugs

* login with credentials on a 5.3 instance no longer works!!!

#### Version 2.0.8 (2017-07-27)

* update to `com.ubirch.util:json:0.4.3`

#### Version 2.0.7 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 2.0.6 (2017-06-16)

* update json4s to 3.5.2

#### Version 2.0.5 (2017-06-09)

* add method `ESStorageBase.connectivityCheck`

#### Version 2.0.4 (2017-06-07)

* update to _com.ubirch.util:json:0.4.0_

#### Version 2.0.3 (2017-06-07)

* update to _com.ubirch.util:json:0.3.5_

#### Version 2.0.2 (2017-05-02)

* updated Elasticsearch to 5.3.2

#### Version 2.0.1 (2017-04-21)

* replace log4j dependency with log4f-to-slf4j bridge (see https://www.elastic.co/guide/en/elasticsearch/client/java-api/5.3/_using_another_logger.html)
* improve exception handling in `ESStorageBase`
* fix existential types problem in `SortUtil.sortBuilder`

#### Version 2.0.0 (2017-04-18)

* update from Elasticsearch version 2.4.4 to 5.3.0

#### Version 0.7.1 (2017-04-18)

* update Elasticsearch from version 2.4.2 to 2.4.4

#### Version 0.7.0 (2017-04-13)

* refactored config to allow configuring more than one host (`esBinaryClient.connection.hosts` replaces:
`esBinaryClient.connection.host` and `esBinaryClient.connection.port`)
* add Shield/X-Pack on/off switch: see config key `esBinaryClient.connection.xpackEnabled`
* removed config key `esBinaryClsient.connection.cluster`
* connection settings are now generic: see config key `esBinaryClient.connection.settings`

#### Version 0.6.2 (2017-04-10)

* update json4s to version 3.5.1
* update Akka Http to 10.0.5

#### Version 0.6.1 (2017-03-31)

* update to com.ubirch.util:json:0.3.3

#### Version 0.6.0 (2017-02-27)

* `ESStorageBase.getDoc` catches exceptions related to missing indexes and search parse errors (usually a cause of no
mappings existing yet) and returns None instead of an exception
* improved logging in `ESStorageBase.getDocs`

#### Version 0.5.2 (2017-02-24)

* parameter `imestamp` in ESBulkStorageBase.storeDocBulk() now has a default value: now.getMillis   

#### Version 0.5.1 (2017-02-24)

* added objects `ESSimpleStorage` and `ESBulkStorage`

#### Version 0.5.0 (2017-02-24)

**This version is not compatible with 0.4.x releases**

* enabled cluster support
* simplified usage in projects by adding host, port and cluster
* added connection config
    | Config Item                            | Category        | Description                                             |
    |:---------------------------------------|:----------------|:--------------------------------------------------------|
    | esBinaryClient.connection.host         | Connection      | host ES is running on                                   | 
    | esBinaryClient.connection.port         | Connection      | port ES is running on                                   | 
    | esBinaryClient.connection.cluster      | Connection      | (optional) ES cluster to connect to                     | 

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
      Resolver.bintrayRepo("rick-beton", "maven"),
      "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "elasticsearch-util" % "2.3.1"
    )

### Release History

#### Version 2.3.1 (2018-01-03)

* update to Elasticsearch 5.6.5

#### Version 2.3.1 (2017-11-03)

* update to Elasticsearch 5.6.3

#### Version 2.3.0 (2017-11-03)

* update to Elasticsearch 5.6.1

#### Version 2.2.0 (2017-09-26)

* roll back to Elasticsearch 5.3.2

#### Version 2.1.0 (2017-07-31)

* update to Elasticsearch 5.5.1 while remaining compatible with 5.3.2

##### Known Bugs

* login with credentials on a 5.3 instance no longer works!!!

#### Version 2.0.1 (2017-05-02)

* updated Elasticsearch to 5.3.2

#### Version 2.0.0 (2017-04-18)

* update from Elasticsearch version 2.4.4 to 5.3.0

#### Version 1.0.1 (2017-04-18)

* update Elasticsearch from version 2.4.2 to 2.4.4

#### Version 1.0.0 (2017-04-12)

* refactored `ElasticsearchMappingsBase` to use the official ES driver's `IndicesAdminClient` instead of HTTP calls 

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
      "com.ubirch.util" %% "futures" % "0.1.1"
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
      Resolver.sonatypeRepo("releases"),
      Resolver.bintrayRepo("hseeberger", "maven")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "json" % "0.4.2"
    )

### Release History

#### Version 0.4.3 (tbd)

* add method `Json4sUtil#string2any`

#### Version 0.4.2 (2017-06-28)

* add method `Json4sUtil#any2String`

#### Version 0.4.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.4.0 (2017-06-07)

* integrate code from _com.ubirch.util:json-auto-convert:0.3.5_

#### Version 0.3.5 (2017-06-07)

* add `DeepCheckResponse` model

#### Version 0.3.4 (2017-04-10)

* update json4s to version 3.5.1

#### Version 0.3.3 (2017-03-31)

* added method com.ubirch.util.json.Json4sUtil#any2any

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

**all code from this module has been moved to _com.ubirch.util:json:0.4.0+_**


-----------------------

## `mongo-utils`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "mongo-utils" % "0.3.7"
    )

### Release History

#### Version 0.3.7 (2018-03-08)

* update to `com.ubirch.util:config:0.2.0`

#### Version 0.3.6 (2017-07-31)

* update to `com.ubirch.util:deep-check-model:0.2.0`

#### Version 0.3.5 (2017-07-27)

* update to `com.ubirch.util:deep-check-model:0.1.3`

#### Version 0.3.4 (2017-07-18)

* update to reactivemongo 0.12.5

#### Version 0.3.3 (2017-07-13)

* introduce `MongoConstraintsBase`

#### Version 0.3.2 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.3.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.3.0 (2017-06-09)

* migrate from _com.ubirch.util:response-util_ to _com.ubirch.util:deep-check-model_

#### Version 0.2.3 (2017-06-08)

* add method `MongoUtil.connectivityCheck`

#### Version 0.2.2 (2017-05-18)

* update to reactivemongo 0.12.3
* update to Akka 2.4.18

#### Version 0.2.1 (2017-05-15)

* exclude Akka 2.3.x dependencies

#### Version 0.2.0 (2017-04-28)

* method `MongoUtil#db` is now a value to prevent too many open Mongo connections

#### Version 0.1.0 (2017-04-06)

* initial release

-----------------------

## `mongo-test-utils`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "mongo-test-utils" % "0.3.7"
    )

### Release History

#### Version 0.3.7 (2017-03-08)

* update to `com.ubirch.util:mongo-utils:0.3.7`

#### Version 0.3.6 (2017-07-31)

* update to `com.ubirch.util:mongo-utils:0.3.6`

#### Version 0.3.5 (2017-07-27)

* update to `com.ubirch.util:mongo-utils:0.3.5`

#### Version 0.3.4 (2017-07-18)

* update to reactivemongo 0.12.5

#### Version 0.3.3 (2017-07-13)

* update to _com.ubirch.util:mongo-utils:0.3.3_

#### Version 0.3.2 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.3.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.2.3 (2017-06-09)

* update _com.ubirch.util:mongo-utils_ to 0.3.0

#### Version 0.2.3 (2017-06-08)

* update _com.ubirch.util:mongo-utils_ to 0.2.3

#### Version 0.2.2 (2017-05-18)

* update to reactivemongo 0.12.3
* update to Akka 2.4.18

#### Version 0.2.1 (2017-05-15)

* update to _com.ubirch.util:mongo-utils:0.2.1

#### Version 0.2.0 (2017-04-28)

* upgrade `com.ubirch.util:mongo-utils` to version 0.2.0

#### Version 0.1.0 (2017-04-06)

* initial release

### Config

To use `MongoClientBuilder` the following configuration is needed (`$PREFIX` defaults to `ubirch.mongo-utils` and a default; a default configuration connecting to localhost:27017 is included):

| Config Item    | Mandatory | Description                                                                             |
|:---------------|:----------|:----------------------------------------------------------------------------------------|
| $PREFIX.hosts  | yes       | string of cluster hosts (see https://docs.mongodb.com/manual/reference/connection-string/) |

Here's an example of a config with more than one host:

    YOUR_PREFIX {

      // for uri formats see: https://docs.mongodb.com/manual/reference/connection-string/

      user = ${MONGO_USER}
      password = ${MONGO_PASSWORD}
      port = 10250
      options = "sslEnabled=true&sslAllowsInvalidCert=true&connectTimeoutMS=10000&maxIdleTimeMS=60000"

      host1 = "mongodb://"${ubirch.mongo-utils.user}":"${ubirch.mongo-utils.password}"@ubirch001.documents.azure.com:"${ubirch.mongo-utils.port}"?"${ubirch.mongo-utils.options}
      host2 = "mongodb://"${ubirch.mongo-utils.user}":"${ubirch.mongo-utils.password}"@ubirch002.documents.azure.com:"${ubirch.mongo-utils.port}"?"${ubirch.mongo-utils.options}

      // the only mandatory configPath (all the others only serve the purpose to make constructing host uris easier)
      hosts = ${ubirch.mongo-utils.host1}","${ubirch.mongo-utils.host2}

    }

### Release History

#### Version 0.1.0 (2017-04-04)

* initial release


-----------------------

## `oidc-utils`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
	  "RoundEights" at "http://maven.spikemark.net/roundeights"
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "oidc-utils" % "0.4.15"
    )

### Config

To use the `OidcDirective` the following configuration is needed:

| Config Item                          | Mandatory | Description     |
|:-------------------------------------|:----------|:----------------|
| ubirch.oidcUtils.redis.updateExpiry  | yes       | number of seconds by a token's expiry date is extended after successfully validating it |
| ubirch.redisUtil.redis.host          | yes       | Redis host      |
| ubirch.redisUtil.redis.port          | yes       | Redis port      |
| ubirch.redisUtil.redis.password      | no        | Redis password  |

A default `application.conf` (connecting to localhost:6379) is included in this module.

### Usage of `OidcDirective`

One header is required by the directive:

* Authorization: Bearer $TOKEN (same as with OAuth2 tokens)

If the provided token is valid a `UserContext` object will be returned.

An example of how to use it can be found in `OidcDirectiveSpec`.

### Release History


#### Version 0.4.15 (2018-03-08)

* reject not active user using ubirchToken
* add optional field _UserContext.email_
* updated to `com.ubirch.util:redis-util:0.3.5` 
* updated to `com.ubirch.util:redis-test-util:0.3.5`
* updated to `com.ubirch.user:client-rest:0.7.0` 

#### Version 0.4.14 (2018-01-16)

* updated to `com.ubirch.util:crypto:0.4.1`

#### Version 0.4.13 (2018-01-16)

* updated to `com.ubirch.util:crypto:0.4.0`

#### Version 0.4.12 (2018-01-15)

* added ubirchToken auth (early beta)
* updated to `com.ubirch.user:client-rest:0.6.4`

#### Version 0.4.11 (2017-08-09)

* improve logging of tokenKey expiry refreshes

#### Version 0.4.10 (2017-07-31)

* update to `com.ubirch.util:redis(-test)-utils:0.3.4`

#### Version 0.4.9 (2017-07-27)

* `com.ubirch.util.oidc.directive.OidcDirective.bearerToken` is now public
* update to `com.ubirch.util:json:0.4.3`

#### Version 0.4.8 (2017-07-17)

* update Akka Http to 10.0.9

#### Version 0.4.7 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.4.6 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.4.5 (2017-06-09)

* update _com.ubirch.util:redis-test-util_ to 0.3.0
* update _com.ubirch.util:redis-util_ to 0.3.0

#### Version 0.4.4 (2017-06-08)

* update _com.ubirch.util:redis-util_ to 0.2.3

#### Version 0.4.3 (2017-06-07)

* update to _com.ubirch.util:json:0.4.0_

#### Version 0.4.2 (2017-06-07)

* update to _com.ubirch.util:json:0.3.5_

#### Version 0.4.1 (2017-05-18)

* update Akka Http to 10.0.6
* update to Akka 2.4.18

#### Version 0.4.0 (2017-04-26)

* add new fields to `UserContext`:
  * `userName`
  * `locale`

#### Version 0.3.0 (2017-04-21)

* add field `providerId` to `UserContext`

#### Version 0.2.5 (2017-04-10)

* update json4s to version 3.5.1
* update Akka Http to 10.0.5

#### Version 0.2.4 (2017-04-10)

* update to `redis-test-utils` 0.2.1

#### Version 0.2.3 (2017-04-09)

* config is now under a fixed prefix
* `OidcDirective` creates `RedisClient` instance itself

#### Version 0.2.2 (2017-03-31)

* update to com.ubirch.util:json:0.3.3

#### Version 0.2.1 (2017-03-28)

* improved logging

#### Version 0.2.0 (2017-03-23)

* removed reading headers `X-UBIRCH-CONTEXT` and `X-UBIRCH-PROVIDER` from `OidcDirective`
* `OidcUtil.tokenToHashedKey` accepts only a token (removed parameter `provider`)

#### Version 0.1.0 (2017-03-22)

* initial release


-----------------------

## `redis-test-util`

### Scala Dependency

    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "redis-test-util" % "0.3.5"
    )

### Config

The required config is documented in the `redis-util` section.

### Release History

#### Version 0.3.5 (2018-03-08)

* update to `com.ubirch.util:redis-util:0.3.5`

#### Version 0.3.4 (2017-07-31)

* update to `com.ubirch.util:redis-util:0.3.4`

#### Version 0.3.3 (2017-07-27)

* update to `com.ubirch.util:redis-util:0.3.3`

#### Version 0.3.2 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.3.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.3.0 (2017-06-09)

* update _com.ubirch.util:redis-util_ to 0.3.0

#### Version 0.2.3 (2017-06-08)

* update _com.ubirch.util:redis-util_ to 0.2.3

#### Version 0.2.2 (2017-05-18)

* update to Akka 2.4.18

#### Version 0.2.1 (2017-04-10)

* refactor `com.ubirch.util.redis.test.RedisCleanup.deleteAll` to depends on implict `RedisClient` (instead of creating
the whole environment itself)

#### Version 0.2.0 (2017-04-09)

* updated to `redis-util` version 0.2.0

#### Version 0.1.0 (2017-03-21)

* initial release


-----------------------

## `redis-util`

### Scala Dependency

    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "redis-util" % "0.3.5"
    )

### Config

You can place the below config keys where you want in the config. When calling `RedisClientUtil.newInstance())` you need
to provide a config prefix and the software will look for them under it.

| Config Item                | Mandatory  | Description               |
|:---------------------------|:-----------|:--------------------------|
| ubirch.redisUtil.host      | yes        | host redis is running on  |
| ubirch.redisUtil.port      | yes        | redis TCP port            |
| ubirch.redisUtil.password  | no         | redis password            |

Here's an example config:

    ubirchRedisUtil {
        host = localhost
        port = 6379
        password = not-a-secure-password
    }
    

And this how you get a redis client:

    ```scala
    implicit val system = ActorSystem()
    implicit val timeout = Timeout(15 seconds)
    val redis = RedisClientUtil.redisClient
    ```

### Release History

#### Version 0.3.5 (2018-03-08)

* update to `com.ubirch.util:config:0.2.0`

#### Version 0.3.4 (2017-07-31)
                   
* update to `com.ubirch.util:deep-check-model:0.2.0`

#### Version 0.3.3 (2017-07-27)

* update to `com.ubirch.util:deep-check-model:0.1.3`

#### Version 0.3.2 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.3.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.3.0 (2017-06-09)

* migrate from _com.ubirch.util:response-util_ to _com.ubirch.util:deep-check-model_

#### Version 0.2.3 (2017-06-08)

* add method `RedisClientUtil.connectivityCheck`

#### Version 0.2.2 (2017-05-18)

* update to Akka 2.4.18

#### Version 0.2.0 (2017-04-09)

* `RedisClientUtil` reads config from a fixed prefix now

#### Version 0.1.0 (2017-03-15)

* initial release


-----------------------

## `response-util`

### Scala Dependency

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.util" %% "response-util" % "0.2.4"
    )

### Release History

#### Version 0.2.4 (2017-07-27)

* update to `com.ubirch.util:json:0.4.3`

#### Version 0.2.3 (2017-07-17)

* update Akka Http to 10.0.9

#### Version 0.2.2 (2017-06-28)

* update to _com.ubirch.util:json:0.4.2_

#### Version 0.2.1 (2017-06-16)

* update json4s to 3.5.2

#### Version 0.2.0 (2017-06-09)

* extracted a refactored `DeepCheckResponse` to new module: _com.ubirch.util:deep-check-model:0.1.0_

#### Version 0.1.6 (2017-06-07)

* update to _com.ubirch.util:json:0.4.0_
* changed `ResponseUtil` to extend `MyJsonProtocol`

#### Version 0.1.5 (2017-06-07)

* update to _com.ubirch.util:json:0.3.5_
* refactored `ResponseUtil` to accept `AnyRef` instead of just `JsonResponse`

#### Version 0.1.4 (2017-05-18)

* update Akka Http to 10.0.6

#### Version 0.1.3 (2017-04-10)

* update Akka Http to 10.0.5

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
      "com.ubirch.util" %% "rest-akka-http" % "0.3.8" // for Akka HTTP 10.0.9
      "com.ubirch.util" %% "rest-akka-http" % "0.3.7" // for Akka HTTP 10.0.6
    )

### Release History

#### Version 0.3.8 (107-07-17)

* update Akka Http to 10.0.9

#### Version 0.3.7 (107-05-22)

* switch to using a CORS library: https://github.com/lomigmegard/akka-http-cors

#### Version 0.3.6 (2017-05-18)

* update Akka Http to 10.0.6

#### Version 0.3.5 (2017-05-05)

* no changes (updated version number to be the same as `rest-akka-http-test`)

#### Version 0.3.4 (2017-04-10)

* update Akka Http to 10.0.5

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
      "com.ubirch.util" %% "rest-akka-http-test" % "0.3.8" // for Akka HTTP 10.0.9
      "com.ubirch.util" %% "rest-akka-http-test" % "0.3.7" // for Akka HTTP 10.0.6
    )

### Release History

#### Version 0.3.8 (107-07-17)

* update Akka Http to 10.0.9

#### Version 0.3.7 (107-05-22)

* switch to using a CORS library: https://github.com/lomigmegard/akka-http-cors
* updated `CORSUtil` to conform with CORS library

#### Version 0.3.6 (2017-05-18)

* update Akka Http to 10.0.6

#### Version 0.3.5 (2017-05-05)

* fixed bug in `CORSUtil`

#### Version 0.3.4 (2017-04-10)

* update Akka Http to 10.0.5

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
