package cards.nine.models

import cards.nine.models.types.{CollectionType, NineCardsCategory, PublicCollectionStatus}

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardsCategory] = None,
  cards: Seq[Card] = Seq.empty,
  moment: Option[Moment] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  publicCollectionStatus: PublicCollectionStatus) extends Serializable

case class CollectionData(
  position: Int,
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardsCategory] = None,
  cards: Seq[CardData] = Seq.empty,
  moment: Option[MomentData] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  publicCollectionStatus: PublicCollectionStatus) extends Serializable

object Collection {

  implicit class CollectionOps(collection: Collection) {

    def toData = CollectionData(
      position = collection.position,
      name = collection.name,
      collectionType = collection.collectionType,
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      appsCategory = collection.appsCategory,
      cards = collection.cards map (_.toData),
      moment = collection.moment map (_.toData),
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
      publicCollectionStatus = collection.publicCollectionStatus)

  }
}

//case class PrivateCollection(
//  name: String,
//  collectionType: CollectionType,
//  icon: String,
//  themedColorIndex: Int,
//  appsCategory: Option[NineCardCategory] = None,
//  cards: Seq[PrivateCard],
//  moment: Option[NineCardsMoment])
