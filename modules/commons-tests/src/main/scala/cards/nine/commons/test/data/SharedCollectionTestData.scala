package cards.nine.commons.test.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.SharedCollectionValues._
import cards.nine.models.types.AppCardType
import cards.nine.models.{Subscription, SharedCollection, SharedCollectionPackage}

trait SharedCollectionTestData extends CollectionTestData {

  def sharedCollectionPackage(num: Int = 0) = SharedCollectionPackage(
    packageName = sharedCollectionPackageName + num,
    title = sharedCollectionPackageTitle + num,
    icon = sharedCollectionPackageIcon,
    category = sharedCollectionPackageCategory,
    stars = sharedCollectionPackageStars,
    downloads = sharedCollectionDownloads,
    free = sharedCollectionFree)

  val sharedCollectionPackage: SharedCollectionPackage = sharedCollectionPackage(0)
  val seqSharedCollectionPackage: Seq[SharedCollectionPackage] = Seq(sharedCollectionPackage(0), sharedCollectionPackage(1), sharedCollectionPackage(2))

  def sharedCollection(num: Int = 0) = SharedCollection(
    id = sharedCollectionId + num,
    sharedCollectionId = sharedCollectionId + num,
    publishedOn = publishedOn,
    author = author,
    name = sharedCollectionName,
    packages = sharedCollectionPackageNamesStr,
    resolvedPackages = seqSharedCollectionPackage,
    views = views,
    subscriptions = Some(subscriptions),
    category = category,
    icon = sharedCollectionIcon,
    community = community,
    locallyAdded = None,
    publicCollectionStatus = publicCollectionStatus)

  val sharedCollection: SharedCollection = sharedCollection(0)
  val seqSharedCollection: Seq[SharedCollection] = Seq(sharedCollection(0), sharedCollection(1), sharedCollection(2))

  val publicationListIds = seqSharedCollection.map(_.sharedCollectionId)

  val seqPublicCollection =
    seqCollection.flatMap(collection => collection.originalSharedCollectionId.map((_, collection))).filter {
      case (sharedCollectionId: String, _) => !publicationListIds.contains(sharedCollectionId)
    }

  def subscription(num: Int = 0) = Subscription(
    id = collection.id + num,
    sharedCollectionId = sharedCollectionId + num,
    name = collection.name,
    apps = collection.cards.count(card => card.cardType == AppCardType),
    icon = collection.icon,
    themedColorIndex = collection.themedColorIndex,
    subscribed = collection.sharedCollectionSubscribed)

  val subscription: Subscription = subscription(0)
  val seqSubscriptions: Seq[Subscription] = Seq(subscription(0), subscription(1), subscription(2))

}
