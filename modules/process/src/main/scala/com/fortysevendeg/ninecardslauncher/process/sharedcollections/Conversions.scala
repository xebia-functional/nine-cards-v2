package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models._
import com.fortysevendeg.ninecardslauncher.services.api.{SharedCollectionPackageResponse, SharedCollection => SharedCollectionService}
import com.fortysevendeg.ninecardslauncher.services.persistence.UpdateCollectionRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection

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
      category = NineCardCategory(item.category),
      icon = item.icon,
      community = item.community,
      publicCollectionStatus = determineSubscription(maybeLocalCollection))

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

  def toSubscription(subscriptions: (String, Collection, Boolean)): Subscription = {
    val (sharedCollectionId, collection, subscribed) = subscriptions
    Subscription(
      id = collection.id,
      sharedCollectionId = sharedCollectionId,
      name = collection.name,
      apps = collection.cards.count(card => CardType(card.cardType) == AppCardType),
      icon = collection.icon,
      themedColorIndex = collection.themedColorIndex,
      subscribed = subscribed)
  }

  private[this] def determineSubscription(maybeLocalCollection: Option[Collection]): PublicCollectionStatus =
    maybeLocalCollection match {
      case Some(c) if c.sharedCollectionId.isDefined && c.sharedCollectionSubscribed => Subscribed
      case Some(c) if c.sharedCollectionId.isDefined && c.originalSharedCollectionId == c.sharedCollectionId =>
        PublishedByOther
      case Some(c) if c.sharedCollectionId.isDefined =>
        PublishedByMe
      case _ => NotPublished
    }
}
