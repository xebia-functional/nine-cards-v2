package com.fortysevendeg.ninecardslauncher.process.user.models

case class Device(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])

case class User(
  id: Int,
  userId: Option[String],
  email: Option[String],
  sessionToken: Option[String],
  installationId: Option[String],
  deviceToken: Option[String],
  androidToken: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String],
  userProfile: UserProfile)

case class UserProfile(
  name: Option[String],
  avatar: Option[String],
  cover: Option[String])