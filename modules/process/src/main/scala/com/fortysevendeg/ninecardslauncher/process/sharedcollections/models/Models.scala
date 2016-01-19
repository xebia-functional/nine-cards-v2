package com.fortysevendeg.ninecardslauncher.process.sharedcollections.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory

case class SharedCollection(
  id: String,
  sharedCollectionId: String,
  publishedOn: Long,
  description: String,
  screenshots: Seq[String],
  author: String,
  tags: Seq[String],
  name: String,
  shareLink: String,
  packages: Seq[String],
  resolvedPackages: Seq[SharedCollectionPackage],
  views: Int,
  category: NineCardCategory,
  icon: String,
  community: Boolean)

case class SharedCollectionPackage(
  packageName: String,
  title: String,
  description: String,
  icon: String,
  stars: Double,
  downloads: String,
  free: Boolean)

case class CreatedCollection(
  name: String,
  description: String,
  author: String,
  packages: Seq[String],
  category: String,
  icon: String,
  community: Boolean
)
