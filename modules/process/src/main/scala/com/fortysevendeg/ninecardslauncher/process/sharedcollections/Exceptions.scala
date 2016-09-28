package cards.nine.process.sharedcollections

import cards.nine.commons.services.TaskService.NineCardException

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