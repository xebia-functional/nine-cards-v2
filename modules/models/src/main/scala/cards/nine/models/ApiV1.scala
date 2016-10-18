package cards.nine.models

import play.api.libs.json._

case class Device(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])

case class LoginResponseV1(
  userId: Option[String],
  sessionToken: Option[String],
  email: Option[String],
  devices: Seq[Device])

case class UserV1(
  _id: String,
  email: String,
  plusProfile: UserV1PlusProfile,
  devices: Seq[UserV1Device],
  status: UserV1StatusInfo)

//case class UserV1Info(
//  email: String,
//  name: String,
//  imageUrl: String,
//  androidId: String,
//  devices: Seq[UserV1Device])

case class UserV1Collection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[UserV1CollectionItem],
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

case class UserV1CollectionItem(
  itemType: String,
  title: String,
  metadata: JsValue,
  categories: Option[Seq[String]])

case class UserV1Device(
  deviceId: String,
  deviceName: String,
  collections: Seq[UserV1Collection])

case class UserV1PlusProfile(
  displayName: String,
  profileImage: UserV1ProfileImage)

case class UserV1ProfileImage(
  imageType: Int,
  imageUrl: String)

case class UserV1StatusInfo(
  products: Seq[String],
  friendsReferred: Int,
  themesShared: Int,
  collectionsShared: Int,
  customCollections: Int,
  earlyAdopter: Boolean,
  communityMember: Boolean,
  joinedThrough: Option[String],
  tester: Boolean)