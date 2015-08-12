package com.fortysevendeg.ninecardslauncher.services.api.models

case class User(
  id: Option[String],
  sessionToken: Option[String],
  email: Option[String],
  devices: Seq[GoogleDevice])

case class Installation(
  id: Option[String],
  deviceType: Option[DeviceType],
  deviceToken: Option[String],
  userId: Option[String])

sealed trait DeviceType {
  val paramValue: String
}

case object AndroidDevice extends DeviceType {
  override val paramValue: String = "ANDROID"
}