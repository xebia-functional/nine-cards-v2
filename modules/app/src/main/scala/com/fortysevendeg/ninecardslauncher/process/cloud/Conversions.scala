package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollection, CloudStorageCollectionItem, CloudStorageDevice, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserCollectionItem, UserDevice}
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import play.api.libs.json.Json

object Conversions {

  def toDriveDevice(driveServiceFile: DriveServiceFile, deviceId: String): CloudStorageDeviceSummary =
    CloudStorageDeviceSummary(
      resourceId = driveServiceFile.googleDriveId,
      title = driveServiceFile.title,
      createdDate = driveServiceFile.createdDate,
      modifiedDate = driveServiceFile.modifiedDate,
      currentDevice = driveServiceFile.fileId contains deviceId)

  def toCloudStorageDevice(userDevice: UserDevice) =
    CloudStorageDevice(
      deviceId = userDevice.deviceId,
      deviceName = userDevice.deviceName,
      documentVersion = CloudStorageProcess.actualDocumentVersion,
      userDevice.collections map toCloudStorageCollection)

  def toCloudStorageCollection(userCollection: UserCollection) =
    CloudStorageCollection(
      name = userCollection.name,
      originalSharedCollectionId = userCollection.originalSharedCollectionId,
      sharedCollectionId = userCollection.sharedCollectionId,
      sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
      items = userCollection.items map toCloudStorageCollectionItem,
      collectionType = userCollection.collectionType,
      icon = userCollection.icon,
      category = userCollection.category)

  def toCloudStorageCollectionItem(userCollectionItem: UserCollectionItem) =
    CloudStorageCollectionItem(
      itemType = userCollectionItem.itemType,
      title = userCollectionItem.title,
      intent = userCollectionItem.intent)

  def toCloudStorageDevice(deviceId: String, deviceName: String, collections: Seq[Collection]) =
    CloudStorageDevice(
      deviceId = deviceId,
      deviceName = deviceName,
      documentVersion = CloudStorageProcess.actualDocumentVersion,
      collections map toCloudStorageCollection)

  def toCloudStorageCollection(collection: Collection) =
    CloudStorageCollection(
      name = collection.name,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = Some(collection.sharedCollectionSubscribed),
      items = collection.cards map toCloudStorageCollectionItem,
      collectionType = collection.collectionType,
      icon = collection.icon,
      category = collection.appsCategory)

  def toCloudStorageCollectionItem(card: Card) =
    CloudStorageCollectionItem(
      itemType = card.cardType.name,
      title = card.term,
      intent = Json.toJson(card.intent).toString())
}
