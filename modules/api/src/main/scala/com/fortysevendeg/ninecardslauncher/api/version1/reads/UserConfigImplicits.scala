package com.fortysevendeg.ninecardslauncher.api.version1.reads

import com.fortysevendeg.ninecardslauncher.api.version1.model._

object UserConfigImplicits {

  import play.api.libs.json._

  implicit val userConfigTimeSlotReads = Json.reads[UserConfigTimeSlot]
  implicit val userConfigUserLocationReads = Json.reads[UserConfigUserLocation]
  implicit val userConfigCollectionItemReads = Json.reads[UserConfigCollectionItem]
  implicit val userConfigCollectionReads = Json.reads[UserConfigCollection]
  implicit val userConfigProfileImageReads = Json.reads[UserConfigProfileImage]
  implicit val userConfigStatusInfoReads = Json.reads[UserConfigStatusInfo]
  implicit val userConfigGeoInfoReads = Json.reads[UserConfigGeoInfo]
  implicit val userConfigDeviceReads = Json.reads[UserConfigDevice]
  implicit val userConfigPlusProfileReads = Json.reads[UserConfigPlusProfile]
  implicit val userConfigReads = Json.reads[UserConfig]

  implicit val userConfigTimeSlotWrites = Json.writes[UserConfigTimeSlot]
  implicit val userConfigUserLocationWrites = Json.writes[UserConfigUserLocation]
  implicit val userConfigCollectionItemWrites = Json.writes[UserConfigCollectionItem]
  implicit val userConfigCollectionWrites = Json.writes[UserConfigCollection]
  implicit val userConfigProfileImageWrites = Json.writes[UserConfigProfileImage]
  implicit val userConfigStatusInfoWrites = Json.writes[UserConfigStatusInfo]
  implicit val userConfigGeoInfoWrites = Json.writes[UserConfigGeoInfo]
  implicit val userConfigDeviceWrites = Json.writes[UserConfigDevice]
  implicit val userConfigPlusProfileWrites = Json.writes[UserConfigPlusProfile]
  implicit val userConfigWrites = Json.writes[UserConfig]
}
