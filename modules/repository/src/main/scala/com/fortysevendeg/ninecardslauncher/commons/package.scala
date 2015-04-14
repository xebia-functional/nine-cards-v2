package com.fortysevendeg.ninecardslauncher

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

package object utils {

  type Service[Req, Res] = Req => Future[Res]

  def tryToFuture[A](function: => Try[A])(implicit ec: ExecutionContext): Future[A] =
    Future(function).flatMap {
      case Success(success) => Future.successful(success)
      case Failure(failure) => Future.failed(failure)
    }

}

package object concurrent {
  // Convenience function that wraps Future.successful and returns a successful Future if all goes well,
  // or a failed one if there is an exception.
  def now[T](body: => T)(implicit context: ExecutionContext): Future[T] =
    (Try(body) map Future.successful recover {
      case ex => Future.failed(ex)
    }).get
}
