package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.process.cloud.models._
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection, Moment, MomentTimeSlot}
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
      userDevice.collections map toCloudStorageCollection,
      moments = None)

  def toCloudStorageCollection(userCollection: UserCollection) =
    CloudStorageCollection(
      name = userCollection.name,
      originalSharedCollectionId = userCollection.originalSharedCollectionId,
      sharedCollectionId = userCollection.sharedCollectionId,
      sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
      items = userCollection.items map toCloudStorageCollectionItem,
      collectionType = userCollection.collectionType,
      icon = userCollection.icon,
      category = userCollection.category,
      moment = None)

  def toCloudStorageCollectionItem(userCollectionItem: UserCollectionItem) =
    CloudStorageCollectionItem(
      itemType = userCollectionItem.itemType,
      title = userCollectionItem.title,
      intent = userCollectionItem.intent)

  def toCloudStorageCollection(collection: Collection, moment: Option[Moment]) =
    CloudStorageCollection(
      name = collection.name,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = Some(collection.sharedCollectionSubscribed),
      items = collection.cards map toCloudStorageCollectionItem,
      collectionType = collection.collectionType,
      icon = collection.icon,
      category = collection.appsCategory,
      moment = moment map toCloudStorageMoment)

  def toCloudStorageCollectionItem(card: Card) =
    CloudStorageCollectionItem(
      itemType = card.cardType.name,
      title = card.term,
      intent = Json.toJson(card.intent).toString())

  def toCloudStorageMoment(moment: Moment) =
    CloudStorageMoment(
      timeslot = moment.timeslot map toCloudStorageMomentTimeSlot,
      wifi = moment.wifi,
      headphones = moment.headphone,
      momentType = moment.momentType)

  def toCloudStorageMomentTimeSlot(timeSlot: MomentTimeSlot) =
    CloudStorageMomentTimeSlot(
      from = timeSlot.from,
      to = timeSlot.to,
      days = timeSlot.days)
}
