package com.fortysevendeg.ninecardslauncher.process.userconfig.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.{CollectionType, NineCardCategory}

case class UserInfo(
  email: String,
  name: String,
  imageUrl: String,
  androidId: String,
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
  collectionType: CollectionType,
  constrains: Seq[String],
  wifi: Seq[String],
  occurrence: Seq[String],
  icon: String,
  category: Option[NineCardCategory])

case class UserCollectionItem(
  itemType: String,
  title: String,
  intent: String,
  categories: Option[Seq[NineCardCategory]])
