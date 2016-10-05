package cards.nine.process.sharedcollections

import cards.nine.models.types.{AppCardType, CardType, NineCardCategory}
import cards.nine.process.commons.CommonConversions
import cards.nine.process.sharedcollections.models._
import cards.nine.services.api.{SharedCollection => SharedCollectionService, SharedCollectionPackageResponse}
import cards.nine.services.persistence.UpdateCollectionRequest
import cards.nine.services.persistence.models.Collection

trait Conversions
  extends CommonConversions {

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
      category = NineCardCategory(item.category),
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

  def toUpdateCollectionRequest(collection: Collection, sharedCollectionSubscribed: Boolean): UpdateCollectionRequest =
    UpdateCollectionRequest(
      id = collection.id,
      position = collection.position,
      name = collection.name,
      collectionType = collection.collectionType,
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      appsCategory = collection.appsCategory,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
      cards = collection.cards)

  def toSubscription(subscriptions: (String, Collection)): Subscription = {
    val (sharedCollectionId, collection) = subscriptions
    Subscription(
      id = collection.id,
      sharedCollectionId = sharedCollectionId,
      name = collection.name,
      apps = collection.cards.count(card => CardType(card.cardType) == AppCardType),
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      subscribed = collection.sharedCollectionSubscribed)
  }

}
