package cards.nine.process.sharedcollections

import cards.nine.models.Collection
import cards.nine.models.types._
import cards.nine.process.sharedcollections.models._
import cards.nine.services.api.{SharedCollection => SharedCollectionService, SharedCollectionPackageResponse}

trait Conversions {

  def toSharedCollections(items: Seq[SharedCollectionService], localCollectionMap: Map[String, Collection]): Seq[SharedCollection] =
    items map (col => toSharedCollection(col, localCollectionMap.get(col.sharedCollectionId)))

  def toSharedCollection(item: SharedCollectionService, maybeLocalCollection: Option[Collection]): SharedCollection =
    SharedCollection(
      id = item.id,
      sharedCollectionId = item.sharedCollectionId,
      publishedOn = item.publishedOn,
      author = item.author,
      name = item.name,
      packages = item.packages,
      resolvedPackages = item.resolvedPackages map toSharedCollectionPackage,
      views = item.views,
      subscriptions = item.subscriptions,
      category = NineCardsCategory(item.category),
      icon = item.icon,
      community = item.community,
      publicCollectionStatus = determinePublicCollectionStatus(maybeLocalCollection))

  def toSharedCollectionPackage(item: SharedCollectionPackageResponse): SharedCollectionPackage =
    SharedCollectionPackage(
      packageName = item.packageName,
      title = item.title,
      icon = item.icon,
      stars = item.stars,
      downloads = item.downloads,
      free = item.free)

  def toSubscription(subscriptions: (String, Collection)): Subscription = {
    val (sharedCollectionId, collection) = subscriptions
    Subscription(
      id = collection.id,
      sharedCollectionId = sharedCollectionId,
      name = collection.name,
      apps = collection.cards.count(card => card.cardType == AppCardType),
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      subscribed = collection.sharedCollectionSubscribed)
  }

  def determinePublicCollectionStatus(maybeCollection: Option[Collection]): PublicCollectionStatus =
    maybeCollection match {
      case Some(c) if c.sharedCollectionId.isDefined && c.sharedCollectionSubscribed => Subscribed
      case Some(c) if c.sharedCollectionId.isDefined && c.originalSharedCollectionId == c.sharedCollectionId =>
        PublishedByOther
      case Some(c) if c.sharedCollectionId.isDefined =>
        PublishedByMe
      case _ => NotPublished
    }


}
