package com.ubirch.util.camel

/**
  * author: cvandrei
  * since: 2017-10-09
  */
case class SqsConfig(queue: String,
                     region: String,
                     queueOwnerId: String,
                     accessKey: String,
                     secretAccessKey: String,
                     concurrentConsumers: Int = 2,
                     maxMessagesPerPoll: Option[Int] = None
                    )
