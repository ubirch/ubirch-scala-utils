package com.ubirch.util.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-12-12
  */
object FutureUtil {

  @deprecated("use `scala.concurrent.Future.sequence()` instead; this method will be removed at some point", "0.1.1")
  def unfoldInnerFutures[T](seqOfFutures: Seq[Future[T]]): Future[Seq[T]] = {

    seqOfFutures.foldLeft(Future(Nil: Seq[T])) { (seqFuture, elementFuture) =>
      for {
        list <- seqFuture
        element <- elementFuture
      } yield list :+ element
    }

  }

}
