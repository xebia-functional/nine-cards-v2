package com.fortysevendeg.ninecardslauncher.models

import android.content.{ComponentName, Intent}
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._

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
  secureUrl: String)

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
  metadata: NineCardIntent,
  categories: Option[Seq[String]])

case class NineCardIntent(
  intentExtras: Map[String, String]) extends Intent {

  def extractPackageName(): Option[String] = intentExtras.get(NineCardExtraPackageName)

  def extractClassName(): Option[String] = intentExtras.get(NineCardExtraClassName)

  def createIntentForApp(): Option[Intent] = {
    for {
      packageName <- extractPackageName()
      className <- extractClassName()
    } yield {
      val intent = new Intent(Intent.ACTION_MAIN)
      intent.addCategory(Intent.CATEGORY_LAUNCHER)
      intent.setComponent(new ComponentName(packageName, className))
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
      intent
    }
  }


}

case class UserConfigUserLocation(
  wifi: String,
  lat: Double,
  lng: Double,
  occurrence: Seq[UserConfigTimeSlot])

case class UserConfigTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])