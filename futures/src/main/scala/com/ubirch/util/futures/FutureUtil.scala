package com.ubirch.util.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-12-12
  */
object FutureUtil {

  def unfoldInnerFutures[T](seqOfFutures: Seq[Future[T]]): Future[Seq[T]] = {

    seqOfFutures.foldLeft(Future(Nil: Seq[T])) { (seqFuture, elementFuture) =>
      for {
        list <- seqFuture
        element <- elementFuture
      } yield list :+ element
    }

  }

}
