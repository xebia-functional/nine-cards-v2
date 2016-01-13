package com.fortysevendeg.ninecardslauncher.process.drive.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.commons.types.CollectionType

case class CloudStorageResource(resourceId: String, title: String)

case class CloudStorageDevice(
  deviceId: String,
  deviceName: String,
  documentVersion: Int,
  collections: Seq[ClodStorageCollection])

case class ClodStorageCollection(
  name: String,
  originalSharedCollectionId: Option[String],
  sharedCollectionId: Option[String],
  sharedCollectionSubscribed: Option[Boolean],
  items: Seq[CloudStorageCollectionItem],
  collectionType: CollectionType,
  icon: String,
  category: Option[NineCardCategory])

case class CloudStorageCollectionItem(
  itemType: String,
  title: String,
  intent: String,
  categories: Option[Seq[NineCardCategory]])
