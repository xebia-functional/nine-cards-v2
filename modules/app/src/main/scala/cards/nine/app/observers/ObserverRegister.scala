package com.fortysevendeg.ninecardslauncher.app.observers

import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImplicitsObserverExceptions, ObserverException}
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.UriCreator
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService

class ObserverRegister(uriCreator: UriCreator)(implicit contextSupport: ContextSupport)
  extends ImplicitsObserverExceptions {

  import NotificationUri._

  val baseUri = uriCreator.parse(baseUriNotificationString)

  val observer = new NineCardsObserver

  @deprecated
  def registerObserver(): Unit =
    contextSupport.getContentResolver.registerContentObserver(baseUri, true, observer)

  @deprecated
  def unregisterObserver(): Unit =
    contextSupport.getContentResolver.unregisterContentObserver(observer)

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
