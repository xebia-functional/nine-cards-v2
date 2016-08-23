package com.fortysevendeg.ninecardslauncher.api.version1

import play.api.libs.json._

case class User(
  _id: Option[String],
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

case class UserConfig(
  _id: String,
  email: String,
  plusProfile: UserConfigPlusProfile,
  devices: Seq[UserConfigDevice],
  geoInfo: UserConfigGeoInfo,
  status: UserConfigStatusInfo)

case class UserConfigPlusProfile(
  displayName: String,
  profileImage: UserConfigProfileImage)

case class UserConfigDevice(
  deviceId: String,
  deviceName: String,
  collections: Seq[UserConfigCollection])

case class UserConfigGeoInfo(
  homeMorning: Option[UserConfigUserLocation],
  homeNight: Option[UserConfigUserLocation],
  work: Option[UserConfigUserLocation],
  current: Option[UserConfigUserLocation])

case class UserConfigStatusInfo(
  products: Seq[String],
  friendsReferred: Int,
  themesShared: Int,
  collectionsShared: Int,
  customCollections: Int,
  earlyAdopter: Boolean,
  communityMember: Boolean,
  joinedThrough: Option[String],
  tester: Boolean)

case class UserConfigProfileImage(
  imageType: Int,
  imageUrl: String,
  secureUrl: Option[String])

case class UserConfigCollection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[UserConfigCollectionItem],
  collectionType: String,
  constrains: Seq[String],
  wifi: Seq[String],
  occurrence: Seq[String],
  icon: String,
  radius: Int,
  lat: Double,
  lng: Double,
  alt: Double,
  category: Option[String])

case class UserConfigCollectionItem(
  itemType: String,
  title: String,
  metadata: JsValue,
  categories: Option[Seq[String]])

case class UserConfigUserLocation(
  wifi: String,
  lat: Double,
  lng: Double,
  occurrence: Seq[UserConfigTimeSlot])

case class UserConfigTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])
