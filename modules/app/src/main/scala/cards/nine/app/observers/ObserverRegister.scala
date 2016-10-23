package cards.nine.app.observers

import cards.nine.app.ui.commons.{ImplicitsObserverExceptions, ObserverException}
import cards.nine.commons.CatchAll
import cards.nine.commons.contentresolver.UriCreator
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.contentresolver.NotificationUri
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService

class ObserverRegister(uriCreator: UriCreator)(implicit contextSupport: ContextSupport)
  extends ImplicitsObserverExceptions {

  import NotificationUri._

  val baseUri = uriCreator.parse(baseUriNotificationString)

  val observer = new NineCardsObserver

  def registerObserverTask(): TaskService[Unit] = TaskService {
    CatchAll[ObserverException] {
      contextSupport.getContentResolver.registerContentObserver(baseUri, true, observer)
    }
  }

  def unregisterObserverTask(): TaskService[Unit] = TaskService {
    CatchAll[ObserverException] {
      contextSupport.getContentResolver.unregisterContentObserver(observer)
    }
  }

}
