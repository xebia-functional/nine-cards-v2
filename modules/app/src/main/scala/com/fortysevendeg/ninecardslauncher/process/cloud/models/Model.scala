package com.fortysevendeg.ninecardslauncher.process.cloud.models

import java.util.Date

import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, CollectionType}

case class CloudStorageResource(
  resourceId: String,
  title: String,
  createdDate: Date,
  modifiedDate: Date)

case class CloudStorageDevice(
  deviceId: String,
  deviceName: String,
  documentVersion: Int,
  collections: Seq[CloudStorageCollection])

case class CloudStorageCollection(
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
  intent: String)
