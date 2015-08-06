package com.fortysevendeg.ninecardslauncher.services.apps

import scalaz.Scalaz._

case class AppsInstalledException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsAppsExceptions {
  implicit def appsInstalledExceptionConverter = (t: Throwable) => AppsInstalledException(t.getMessage, t.some)
}