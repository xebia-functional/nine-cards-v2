package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

trait FileException
  extends RuntimeException
  with NineCardException {

  val message: String

  val cause: Option[Throwable]

}

case class FileExceptionImpl(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with FileException
  with NineCardException {
  cause map initCause
}

trait BitmapTransformationException
  extends RuntimeException
  with NineCardException {

  val message: String

  val cause: Option[Throwable]

}

case class BitmapTransformationExceptionImpl(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with BitmapTransformationException
  with NineCardException {
  cause map initCause
}

trait ImplicitsImageExceptions {

  implicit def fileException = (t: Throwable) => FileExceptionImpl(t.getMessage, Option(t))

  implicit def bitmapTransformationException = (t: Throwable) => BitmapTransformationExceptionImpl(t.getMessage, Option(t))

}