package com.fortysevendeg.ninecardslauncher.app.observers

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.UriCreator
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri

class ObserverRegister(uriCreator: UriCreator)(implicit contextSupport: ContextSupport) {

  import NotificationUri._

  val baseUri = uriCreator.parse(baseUriNotificationString)

  val observer = new NineCardsObserver

  def registerObserver(): Unit =
    contextSupport.getContentResolver.registerContentObserver(baseUri, true, observer)

  def unregisterObserver(): Unit =
    contextSupport.getContentResolver.unregisterContentObserver(observer)

}
