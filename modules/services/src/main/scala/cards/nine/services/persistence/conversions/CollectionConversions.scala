package cards.nine.services.persistence.conversions

import cards.nine.models.types._
import cards.nine.models.{Collection, CollectionData}
import cards.nine.repository.model.{
  Card => RepositoryCard,
  Collection => RepositoryCollection,
  CollectionData => RepositoryCollectionData
}

trait CollectionConversions extends CardConversions with MomentConversions {

  def toCollection(collection: RepositoryCollection): Collection =
    Collection(
      id = collection.id,
      position = collection.data.position,
      name = collection.data.name,
      collectionType = CollectionType(collection.data.collectionType),
      icon = collection.data.icon,
      themedColorIndex = collection.data.themedColorIndex,
      appsCategory = collection.data.appsCategory.map(NineCardsCategory(_)),
      originalSharedCollectionId = collection.data.originalSharedCollectionId,
      sharedCollectionId = collection.data.sharedCollectionId,
      sharedCollectionSubscribed = collection.data.sharedCollectionSubscribed getOrElse false,
      moment = None,
      publicCollectionStatus = determinePublicCollectionStatus(collection))

  def toCollection(collection: RepositoryCollection, cards: Seq[RepositoryCard]): Collection =
    Collection(
      id = collection.id,
      position = collection.data.position,
      name = collection.data.name,
      collectionType = CollectionType(collection.data.collectionType),
      icon = collection.data.icon,
      themedColorIndex = collection.data.themedColorIndex,
      appsCategory = collection.data.appsCategory.map(NineCardsCategory(_)),
      originalSharedCollectionId = collection.data.originalSharedCollectionId,
      sharedCollectionId = collection.data.sharedCollectionId,
      sharedCollectionSubscribed = collection.data.sharedCollectionSubscribed getOrElse false,
      cards = cards map toCard,
      moment = None,
      publicCollectionStatus = determinePublicCollectionStatus(collection))

  def toRepositoryCollection(collection: Collection): RepositoryCollection =
    RepositoryCollection(
      id = collection.id,
      data = RepositoryCollectionData(
        position = collection.position,
        name = collection.name,
        collectionType = collection.collectionType.name,
        icon = collection.icon,
        themedColorIndex = collection.themedColorIndex,
        appsCategory = collection.appsCategory map (_.name),
        originalSharedCollectionId = collection.originalSharedCollectionId,
        sharedCollectionId = collection.sharedCollectionId,
        sharedCollectionSubscribed = Option(collection.sharedCollectionSubscribed)
      )
    )

  def toRepositoryCollectionData(collection: CollectionData): RepositoryCollectionData =
    RepositoryCollectionData(
      position = collection.position,
      name = collection.name,
      collectionType = collection.collectionType.name,
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      appsCategory = collection.appsCategory map (_.name),
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = Option(collection.sharedCollectionSubscribed))

  private[this] def determinePublicCollectionStatus(
      repositoryCollection: RepositoryCollection): PublicCollectionStatus =
    repositoryCollection match {
      case collection
          if collection.data.sharedCollectionId.isDefined && collection.data.originalSharedCollectionId == collection.data.sharedCollectionId =>
        PublishedByOther
      case collection if collection.data.sharedCollectionId.isDefined =>
        PublishedByMe
      case _ =>
        NotPublished
    }

}
