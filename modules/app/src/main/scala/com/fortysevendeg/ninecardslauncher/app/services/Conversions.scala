package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.process.collection.models.{FormedItem, FormedCollection, UnformedItem}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollectionItem, UserCollection}

trait Conversions {

  def toSeqUnformedItem(apps: Seq[AppCategorized]) = apps map toUnformedItem

  def toUnformedItem(appCategorized: AppCategorized) = UnformedItem(
    name = appCategorized.name,
    packageName = appCategorized.packageName,
    className = appCategorized.className,
    imagePath = appCategorized.imagePath getOrElse "", // TODO image default?
    category = appCategorized.category getOrElse misc,
    starRating = appCategorized.starRating getOrElse 0,
    numDownloads = appCategorized.numDownloads getOrElse "",
    ratingsCount = appCategorized.ratingsCount getOrElse 0,
    commentCount = appCategorized.commentCount getOrElse 0
  )

  def toSeqFormedCollection(collections: Seq[UserCollection]) = collections map toFormedCollection

  def toFormedCollection(userCollection: UserCollection) = FormedCollection(
    name = userCollection.name,
    originalSharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
    items = userCollection.items map toFormedItem,
    collectionType = userCollection.collectionType,
    constrains = userCollection.constrains,
    icon = userCollection.icon,
    category = userCollection.category
  )

  def toFormedItem(item: UserCollectionItem) = FormedItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.intent
  )

}
