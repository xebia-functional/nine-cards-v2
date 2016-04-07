package com.fortysevendeg.ninecardslauncher.commons.contentresolver

object NotificationUri {

  val authorityPart = "com.fortysevendeg.ninecardslauncher2"

  val contentPrefix = "notification://"

  val baseUriNotificationString = s"$contentPrefix$authorityPart"

  val appUriNotificationString = s"$baseUriNotificationString/app"

  val cardUriNotificationString = s"$baseUriNotificationString/card"

  val collectionUriNotificationString = s"$baseUriNotificationString/collection"

  val dockAppUriNotificationString = s"$baseUriNotificationString/dockApp"

  val momentUriNotificationString = s"$baseUriNotificationString/moment"

  val userUriNotificationString = s"$baseUriNotificationString/user"

}
