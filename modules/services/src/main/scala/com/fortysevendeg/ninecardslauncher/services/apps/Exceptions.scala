package com.fortysevendeg.ninecardslauncher.services.apps

case class AppsInstalledException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}