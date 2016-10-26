package cards.nine.models

import cards.nine.models.types.{NineCardsCategory, PublicCollectionStatus}

case class SharedCollection(
  id: String,
  sharedCollectionId: String,
  publishedOn: Long,
  author: String,
  name: String,
  packages: Seq[String],
  resolvedPackages: Seq[SharedCollectionPackage],
  views: Int,
  subscriptions: Option[Int],
  category: NineCardsCategory,
  icon: String,
  community: Boolean,
  publicCollectionStatus: PublicCollectionStatus)

case class SharedCollectionPackage(
  packageName: String,
  title: String,
  icon: String,
  category: Option[NineCardsCategory],
  stars: Double,
  downloads: String,
  free: Boolean)

case class Subscription(
  id: Int,
  sharedCollectionId: String,
  name: String,
  apps: Int,
  icon: String,
  themedColorIndex: Int,
  subscribed: Boolean)