package com.fortysevendeg.ninecardslauncher.api.model

case class User(
  id: Option[String],
  sessionToken: Option[String],
  username: Option[String],
  password: Option[String],
  email: Option[String],
  authData: Option[AuthData])

case class AuthData(
  google: Option[AuthGoogle],
  facebook: Option[AuthFacebook],
  twitter: Option[AuthTwitter],
  anonymous: Option[AuthAnonymous])

case class AuthGoogleDevice(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])

case class AuthGoogle(
  email: String,
  devices: Seq[AuthGoogleDevice])

case class AuthTwitter(
  id: String,
  screenName: String,
  consumerKey: String,
  consumerSecret: String,
  authToken: String,
  authTokenSecret: String,
  key: String,
  secretKey: String)

case class AuthFacebook(
  id: String,
  accessToken: String,
  expirationDate: Long)

case class AuthAnonymous(
  id: String)

case class Installation(
  id: Option[String],
  deviceType: Option[String],
  deviceToken: Option[String],
  userId: Option[String],
  facebookId: Option[String])

