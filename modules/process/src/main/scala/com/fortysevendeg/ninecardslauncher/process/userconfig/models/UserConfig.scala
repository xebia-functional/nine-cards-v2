package com.fortysevendeg.ninecardslauncher.process.userconfig.models

case class UserInfo(
  email: String,
  name: String,
  imageUrl: String,
  devices: Seq[UserDevice])

case class UserDevice(
  deviceId: String,
  deviceName: String,
  collections: Seq[UserCollection])

case class UserCollection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[UserCollectionItem],
  collectionType: String,
  constrains: Seq[String],
  wifi: Seq[String],
  occurrence: Seq[String],
  icon: String,
  category: Option[String])

case class UserCollectionItem(
  itemType: String,
  title: String,
  intent: String,
  categories: Option[Seq[String]])
