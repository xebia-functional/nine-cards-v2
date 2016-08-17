package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class AppsInstalledException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsAppsExceptions {
  implicit def appsInstalledExceptionConverter = (t: Throwable) => AppsInstalledException(t.getMessage, Option(t))
}