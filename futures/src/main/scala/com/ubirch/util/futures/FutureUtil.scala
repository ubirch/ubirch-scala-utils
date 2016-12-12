package com.ubirch.util.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-12-12
  */
object FutureUtil {

  def unfoldInnerFutures[T](listOfFutures: List[Future[T]]): Future[Seq[T]] = {

    listOfFutures.foldLeft(Future(Nil: Seq[T])) { (listFuture, elementFuture) =>
      for {
        list <- listFuture
        element <- elementFuture
      } yield list :+ element
    }

  }

}
