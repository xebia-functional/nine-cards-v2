/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.api.version1

import play.api.libs.json.JsNull

trait ApiServiceData {

  val baseUrl = "http://localhost:8080"

  val statusCodeOk = 200

  val userId = 10

  val sessionToken = "session-token"

  val username = "username"
  val password = "password"
  val email    = "email"

  val deviceId    = "device-id"
  val deviceName  = "Nexus 47"
  val secretToken = "secret-token"
  val permission  = "androidmarket"

  val headers = Seq(
    ("header-1", "header-1-value"),
    ("header-2", "header-2-value"),
    ("header-3", "header-3-value"))

  val authGoogleDevice = AuthGoogleDevice(
    name = deviceName,
    deviceId = deviceId,
    secretToken = secretToken,
    permissions = Seq(permission))

  val authGoogle = AuthGoogle(email = email, devices = Seq(authGoogleDevice))

  val authData =
    AuthData(google = Some(authGoogle), facebook = None, twitter = None, anonymous = None)

  val emptyUser = User(None, None, None, None, None, None)

  val user = User(
    Some(userId.toString),
    Some(sessionToken),
    Some(username),
    Some(password),
    Some(email),
    authData = Some(authData))

  val userConfigProfileImage =
    UserConfigProfileImage(imageType = 0, imageUrl = "http://fakeUrl", secureUrl = None)

  val userConfigPlusProfile = UserConfigPlusProfile(username, userConfigProfileImage)

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
    category = Some("SOCIAL"))

  val userConfigDevice = UserConfigDevice(deviceId, deviceName, Seq(userConfigCollection))

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
