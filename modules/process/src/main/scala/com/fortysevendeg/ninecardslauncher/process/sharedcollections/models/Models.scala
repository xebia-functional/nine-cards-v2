package com.fortysevendeg.ninecardslauncher.process.sharedcollections.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.{PublicCollectionStatus, NineCardCategory}

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
  category: NineCardCategory,
  icon: String,
  community: Boolean,
  publicCollectionStatus: PublicCollectionStatus)

case class CreateSharedCollection(
   author: String,
   name: String,
   packages: Seq[String],
   category: NineCardCategory,
   icon: String,
   community: Boolean)

case class UpdateSharedCollection(
   sharedCollectionId: String,
   name: String,
   packages: Seq[String])

case class SharedCollectionPackage(
  packageName: String,
  title: String,
  icon: String,
  stars: Double,
  downloads: String,
  free: Boolean)

case class CreatedCollection(
  name: String,
  author: String,
  packages: Seq[String],
  category: NineCardCategory,
  sharedCollectionId: String,
  icon: String,
  community: Boolean
)

case class Subscription(
  id: Int,
  sharedCollectionId: String,
  name: String,
  apps: Int,
  icon: String,
  themedColorIndex: Int,
  subscribed: Boolean)
