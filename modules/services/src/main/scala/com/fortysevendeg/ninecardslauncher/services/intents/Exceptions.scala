package com.fortysevendeg.ninecardslauncher.services.intents

import cards.nine.commons.services.TaskService.NineCardException

case class IntentLauncherServicesException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class IntentLauncherServicesPermissionException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}