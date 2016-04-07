package com.fortysevendeg.ninecardslauncher.app.observers

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.UriCreator
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri

class ObserverRegister(uriCreator: UriCreator) {

  import NotificationUri._

  val baseUri = uriCreator.parse(baseUriNotificationString)

  val observer = new NineCardsObserver

  def registerObserver(implicit contextSupport: ContextSupport): Unit =
    contextSupport.getContentResolver.registerContentObserver(baseUri, true, observer)

  def unregisterObserver(implicit contextSupport: ContextSupport): Unit =
    contextSupport.getContentResolver.unregisterContentObserver(observer)

}
