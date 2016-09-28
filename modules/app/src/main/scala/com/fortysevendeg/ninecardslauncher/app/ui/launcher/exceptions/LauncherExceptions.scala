package com.fortysevendeg.ninecardslauncher.app.ui.launcher.exceptions

import cards.nine.commons.services.TaskService.NineCardException


case class SpaceException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsSpaceException {
  implicit def momentException = (t: Throwable) => SpaceException(t.getMessage, Option(t))
}