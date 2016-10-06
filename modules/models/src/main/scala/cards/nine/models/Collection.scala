package cards.nine.models

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card] = Seq.empty,
  moment: Option[Moment])

case class CollectionData(
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[CardData] = Seq.empty,
  moment: Option[MomentData])

object Collection {

  implicit class CollectionOps(collection: Collection) {

    def toData = CollectionData(
      position = collection.position,
      name = collection.name,
      collectionType = collection.collectionType,
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      appsCategory = collection.appsCategory,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
      cards = collection.cards map (_.toData),
      moment = collection.moment map (_.toData))
  }
}