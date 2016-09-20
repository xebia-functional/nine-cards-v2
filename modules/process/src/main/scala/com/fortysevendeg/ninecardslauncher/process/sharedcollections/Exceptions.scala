package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class SharedCollectionsException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

case class SharedCollectionsConfigurationException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{
  cause map initCause
}