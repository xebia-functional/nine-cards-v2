package com.fortysevendeg.ninecardslauncher.commons.contentresolver

object NotificationUri {

  val authorityPart = "com.fortysevendeg.ninecardslauncher2"

  val contentPrefix = "notification://"

  val baseUriNotificationString = s"$contentPrefix$authorityPart"

  val appUriPath = "app"

  val cardUriPath = "card"

  val collectionUriPath = "collection"

  val dockAppUriPath = "dockApp"

  val momentUriPath = "moment"

  val userUriPath = "user"

  val widgetUriPath = "widget"

}
