package com.fortysevendeg.ninecardslauncher.process.recommendations

import cards.nine.commons.services.TaskService.NineCardException

case class RecommendedAppsException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

case class RecommendedAppsConfigurationException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}