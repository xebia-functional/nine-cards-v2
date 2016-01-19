package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollection, CloudStorageCollectionItem, CloudStorageDevice}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.commons.{CollectionTypes, NineCardCategories}
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import org.joda.time.DateTime

import scala.util.Random
import scalaz.Scalaz._

trait CloudStorageProcessImplData {

  val driveServiceFileSeq: Seq[DriveServiceFile] = 1 to 10 map (_ => generateDriveServiceFile)

  val driveServiceFileEmptySeq = Seq.empty[DriveServiceFile]

  def generateDriveServiceFile =
    DriveServiceFile(
      driveId = Random.nextString(10),
      title = Random.nextString(10),
      createdDate = DateTime.now().minusMonths(6).toDate,
      modifiedDate = DateTime.now().minusMonths(3).toDate)

  val driveId = "drive-id"

  val deviceId = "device-id"

  val deviceName = "device-name"

  val documentVersion = 1

  val numCollections = 1

  val numItemsPerCollection = 1

  val cloudStorageDevice =
    CloudStorageDevice(
      deviceId,
      deviceName,
      documentVersion,
      generateCollections(numCollections, numItemsPerCollection))

  def generateCollections(num: Int, numItems: Int): Seq[CloudStorageCollection] = 1 to num map { i =>
    CloudStorageCollection(
      name = s"Collection $num",
      originalSharedCollectionId = s"Original Shared Collection Id $num".some,
      sharedCollectionId = s"Shared Collection Id $num".some,
      sharedCollectionSubscribed = true.some,
      generateCollectionItems(num: Int),
      collectionType = FreeCollectionType,
      icon = s"Collection Icon $num",
      category = Business.some)
  }

  def generateCollectionItems(num: Int): Seq[CloudStorageCollectionItem] = 1 to num map { i =>
    CloudStorageCollectionItem(
      s"Item Type $num",
      s"Item Title $num",
      s"Item intent $num",
      Seq(AppWallpaper, Game))
  }

  val validCloudStorageDeviceJson =
    s"""
      |{
      | "deviceId": "$deviceId",
      | "deviceName": "$deviceName",
      | "documentVersion": $documentVersion,
      | "collections": [${generateCollectionsJson(numCollections, numItemsPerCollection).mkString(",")}]
      |}
    """.stripMargin

  def generateCollectionsJson(num: Int, numItems: Int): Seq[String] = 1 to num map { i =>
    s"""
      |{
      | "name": "Collection $num",
      | "originalSharedCollectionId": "Original Shared Collection Id $num",
      | "sharedCollectionId": "Shared Collection Id $num",
      | "sharedCollectionSubscribed": true,
      | "items": [${generateCollectionItemsJson(numItems).mkString(",")}],
      | "collectionType": "${CollectionTypes.free}",
      | "icon": "Collection Icon $num",
      | "category": "${NineCardCategories.business}"
      |}
    """.stripMargin
  }

  def generateCollectionItemsJson(num: Int): Seq[String] = 1 to num map { i =>
    s"""
      |{
      | "itemType": "Item Type $num",
      | "title": "Item Title $num",
      | "intent": "Item intent $num",
      | "categories": ["${NineCardCategories.appWallpaper}", "${NineCardCategories.game}"]
      |}
    """.stripMargin
  }

  val invalidCloudStorageDeviceJson =
    """
      |{ "inexistendField": "Value" }
    """.stripMargin
}
