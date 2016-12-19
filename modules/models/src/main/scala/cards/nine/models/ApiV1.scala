package cards.nine.models

import cards.nine.models.types.{CardType, CollectionType, NineCardsCategory}
import play.api.libs.json._

case class Device(name: String, deviceId: String, secretToken: String, permissions: Seq[String])

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
    itemType: CardType,
    title: String,
    intent: String,
    categories: Option[Seq[NineCardsCategory]])

case class UserV1Device(deviceId: String, deviceName: String, collections: Seq[UserV1Collection])

case class UserV1PlusProfile(displayName: String, profileImage: UserV1ProfileImage)

case class UserV1ProfileImage(imageType: Int, imageUrl: String)

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
