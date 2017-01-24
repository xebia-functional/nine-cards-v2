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

object JsonImplicits {

  import play.api.libs.json._

  implicit val authAnonymousReads            = Json.reads[AuthAnonymous]
  implicit val authFacebookReads             = Json.reads[AuthFacebook]
  implicit val authTwitterReads              = Json.reads[AuthTwitter]
  implicit val authGoogleDeviceReads         = Json.reads[AuthGoogleDevice]
  implicit val authGoogleReads               = Json.reads[AuthGoogle]
  implicit val authDataReads                 = Json.reads[AuthData]
  implicit val userReads                     = Json.reads[User]
  implicit val userConfigTimeSlotReads       = Json.reads[UserConfigTimeSlot]
  implicit val userConfigUserLocationReads   = Json.reads[UserConfigUserLocation]
  implicit val userConfigCollectionItemReads = Json.reads[UserConfigCollectionItem]
  implicit val userConfigCollectionReads     = Json.reads[UserConfigCollection]
  implicit val userConfigProfileImageReads   = Json.reads[UserConfigProfileImage]
  implicit val userConfigStatusInfoReads     = Json.reads[UserConfigStatusInfo]
  implicit val userConfigGeoInfoReads        = Json.reads[UserConfigGeoInfo]
  implicit val userConfigDeviceReads         = Json.reads[UserConfigDevice]
  implicit val userConfigPlusProfileReads    = Json.reads[UserConfigPlusProfile]
  implicit val userConfigReads               = Json.reads[UserConfig]

  implicit val authAnonymousWrites            = Json.writes[AuthAnonymous]
  implicit val authFacebookWrites             = Json.writes[AuthFacebook]
  implicit val authTwitterWrites              = Json.writes[AuthTwitter]
  implicit val authGoogleDeviceWrites         = Json.writes[AuthGoogleDevice]
  implicit val authGoogleWrites               = Json.writes[AuthGoogle]
  implicit val authDataWrites                 = Json.writes[AuthData]
  implicit val userWrites                     = Json.writes[User]
  implicit val userConfigTimeSlotWrites       = Json.writes[UserConfigTimeSlot]
  implicit val userConfigUserLocationWrites   = Json.writes[UserConfigUserLocation]
  implicit val userConfigCollectionItemWrites = Json.writes[UserConfigCollectionItem]
  implicit val userConfigCollectionWrites     = Json.writes[UserConfigCollection]
  implicit val userConfigProfileImageWrites   = Json.writes[UserConfigProfileImage]
  implicit val userConfigStatusInfoWrites     = Json.writes[UserConfigStatusInfo]
  implicit val userConfigGeoInfoWrites        = Json.writes[UserConfigGeoInfo]
  implicit val userConfigDeviceWrites         = Json.writes[UserConfigDevice]
  implicit val userConfigPlusProfileWrites    = Json.writes[UserConfigPlusProfile]
  implicit val userConfigWrites               = Json.writes[UserConfig]

}
