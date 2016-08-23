package com.fortysevendeg.ninecardslauncher.api.version1

import play.api.libs.json.JsNull

trait ApiServiceData {

  val baseUrl = "http://localhost:8080"

  val statusCodeOk = 200

  val userId = 10

  val sessionToken = "session-token"

  val username = "username"
  val password = "password"
  val email = "email"

  val deviceId = "device-id"
  val deviceName = "Nexus 47"
  val secretToken = "secret-token"
  val permission = "androidmarket"

  val headers = Seq(
    ("header-1", "header-1-value"),
    ("header-2", "header-2-value"),
    ("header-3", "header-3-value"))

  val authGoogleDevice = AuthGoogleDevice(
    name = deviceName,
    deviceId = deviceId,
    secretToken = secretToken,
    permissions = Seq(permission))

  val authGoogle = AuthGoogle(
    email = email,
    devices = Seq(authGoogleDevice))

  val authData = AuthData(
    google = Some(authGoogle),
    facebook = None,
    twitter = None,
    anonymous = None)

  val emptyUser = User(None, None, None, None, None, None)

  val user = User(
    Some(userId.toString),
    Some(sessionToken),
    Some(username),
    Some(password),
    Some(email),
    authData = Some(authData))

  val userConfigProfileImage = UserConfigProfileImage(
    imageType = 0,
    imageUrl = "http://fakeUrl",
    secureUrl = None)

  val userConfigPlusProfile = UserConfigPlusProfile(
    username,
    userConfigProfileImage)

  val userConfigCollectionItem = UserConfigCollectionItem(
    itemType = "item type",
    title = "item title",
    metadata = JsNull,
    categories = None)

  val userConfigCollection = UserConfigCollection(
    name = "collection name",
    originalSharedCollectionId = Some("original collection id"),
    sharedCollectionId = Some("collection id"),
    sharedCollectionSubscribed = Some(true),
    items = Seq(userConfigCollectionItem),
    collectionType = "collection type",
    constrains = Seq.empty,
    wifi = Seq.empty,
    occurrence = Seq.empty,
    icon = "social",
    radius = 0,
    lat = 0,
    lng = 0,
    alt = 0,
    category = Some("SOCIAL")
  )

  val userConfigDevice = UserConfigDevice(
    deviceId,
    deviceName,
    Seq(userConfigCollection))

  val userConfigGeoInfo = UserConfigGeoInfo(None, None, None, None)

  val userConfigStatusInfo = UserConfigStatusInfo(
    products = Seq.empty,
    friendsReferred = 10,
    themesShared = 5,
    collectionsShared = 5,
    customCollections = 2,
    earlyAdopter = false,
    communityMember = true,
    joinedThrough = None,
    tester = false)

  val userConfig = UserConfig(
    userId.toString,
    email,
    userConfigPlusProfile,
    Seq(userConfigDevice),
    userConfigGeoInfo,
    userConfigStatusInfo)

}
