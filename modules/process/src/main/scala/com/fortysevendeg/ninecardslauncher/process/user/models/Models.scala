package com.fortysevendeg.ninecardslauncher.process.user.models

case class Device(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])

case class User(
  id: Int,
  email: Option[String],
  apiKey: Option[String],
  sessionToken: Option[String],
  deviceToken: Option[String],
  marketToken: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String],
  name: Option[String],
  userProfile: UserProfile)

case class UserProfile(
  name: Option[String],
  avatar: Option[String],
  cover: Option[String])