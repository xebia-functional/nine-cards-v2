package com.fortysevendeg.ninecardslauncher.services.image

import scalaz.Scalaz._

case class FileException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class BitmapTransformationException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsImageExceptions {
  implicit def fileException = (t: Throwable) => FileException(t.getMessage, t.some)
  implicit def bitmapTransformationException = (t: Throwable) => BitmapTransformationException(t.getMessage, t.some)
}