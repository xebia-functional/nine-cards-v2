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

case class AuthGoogle(email: String, devices: Seq[AuthGoogleDevice])

case class AuthTwitter(
    id: String,
    screenName: String,
    consumerKey: String,
    consumerSecret: String,
    authToken: String,
    authTokenSecret: String,
    key: String,
    secretKey: String)

case class AuthFacebook(id: String, accessToken: String, expirationDate: Long)

case class AuthAnonymous(id: String)

case class UserConfig(
    _id: String,
    email: String,
    plusProfile: UserConfigPlusProfile,
    devices: Seq[UserConfigDevice],
    geoInfo: UserConfigGeoInfo,
    status: UserConfigStatusInfo)

case class UserConfigPlusProfile(displayName: String, profileImage: UserConfigProfileImage)

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

case class UserConfigProfileImage(imageType: Int, imageUrl: String, secureUrl: Option[String])

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

case class UserConfigTimeSlot(from: String, to: String, days: Seq[Int])
