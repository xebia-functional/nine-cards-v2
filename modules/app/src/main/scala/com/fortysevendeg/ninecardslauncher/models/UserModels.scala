package com.fortysevendeg.ninecardslauncher.models

case class User(
  id: Option[String],
  sessionToken: Option[String],
  email: Option[String],
  devices: Seq[GoogleDevice])

case class Installation(
  id: Option[String],
  deviceType: Option[String],
  deviceToken: Option[String],
  userId: Option[String])