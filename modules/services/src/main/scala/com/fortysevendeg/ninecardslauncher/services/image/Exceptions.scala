package com.fortysevendeg.ninecardslauncher.services.image

case class BitmapTransformationException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}
