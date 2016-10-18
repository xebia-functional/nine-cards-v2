package cards.nine.process.userv1.models

import cards.nine.models.types.{CollectionType, NineCardsCategory}

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
  category: Option[NineCardsCategory])

case class UserV1CollectionItem(
  itemType: String,
  title: String,
  intent: String,
  categories: Option[Seq[NineCardsCategory]])
