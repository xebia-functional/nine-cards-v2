package com.fortysevendeg.ninecardslauncher.commons.utils

import scalaz.Scalaz._

case class AssetException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsAssetException {
  implicit def assetException = (t: Throwable) => AssetException(t.getMessage, t.some)
}