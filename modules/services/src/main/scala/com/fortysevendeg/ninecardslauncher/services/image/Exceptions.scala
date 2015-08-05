package com.fortysevendeg.ninecardslauncher.services.image

import scalaz.Scalaz._

trait FileException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class FileExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with FileException {
  cause map initCause
}

trait BitmapTransformationException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class BitmapTransformationExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with BitmapTransformationException {
  cause map initCause
}

trait ImplicitsImageExceptions {
  implicit def fileException = (t: Throwable) => FileExceptionImpl(t.getMessage, t.some)
  implicit def bitmapTransformationException = (t: Throwable) => BitmapTransformationExceptionImpl(t.getMessage, t.some)
}