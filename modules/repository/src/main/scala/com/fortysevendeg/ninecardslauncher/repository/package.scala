package com.fortysevendeg.ninecardslauncher

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

package object repository {

  type Service[Req, Res] = Req => Future[Res]

  def tryToFuture[A](function: => Try[A])(implicit ec: ExecutionContext): Future[A] =
    Future(function).flatMap {
      case Success(success) => Future.successful(success)
      case Failure(failure) => Future.failed(failure)
    }
}
