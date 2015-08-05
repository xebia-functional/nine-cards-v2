package com.fortysevendeg.ninecardslauncher.process.utils

import scalaz.Scalaz._

case class AssetException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsUtilsException {
  implicit def assetException = (t: Throwable) => AssetException(t.getMessage, t.some)
}