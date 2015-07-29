package com.fortysevendeg.ninecardslauncher.services.image

import scalaz.Scalaz._

case class BitmapTransformationException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsImageExceptions {
  implicit def bitmapTransformationExceptionConverter = (t: Throwable) => BitmapTransformationException(t.getMessage, t.some)
}