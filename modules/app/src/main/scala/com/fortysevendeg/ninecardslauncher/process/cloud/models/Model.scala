package com.fortysevendeg.ninecardslauncher.process.cloud.models

import java.util.Date

import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, CollectionType}

trait CloudStorageResource {
  def resourceId: String
  def title: String
  def createdDate: Date
  def modifiedDate: Date
}

case class CloudStorageDeviceSummary(
  resourceId: String,
  title: String,
  createdDate: Date,
  modifiedDate: Date,
  currentDevice: Boolean) extends CloudStorageResource

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
