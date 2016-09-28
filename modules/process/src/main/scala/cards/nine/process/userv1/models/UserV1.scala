package cards.nine.process.userv1.models

import cards.nine.process.commons.types.{CollectionType, NineCardCategory}

case class Device(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])

case class UserV1Info(
  email: String,
  name: String,
  imageUrl: String,
  androidId: String,
  devices: Seq[UserV1Device])

case class UserV1Device(
  deviceId: String,
  deviceName: String,
  collections: Seq[UserV1Collection])

case class UserV1Collection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[UserV1CollectionItem],
  collectionType: CollectionType,
  constrains: Seq[String],
  wifi: Seq[String],
  occurrence: Seq[String],
  icon: String,
  category: Option[NineCardCategory])

case class UserV1CollectionItem(
  itemType: String,
  title: String,
  intent: String,
  categories: Option[Seq[NineCardCategory]])
