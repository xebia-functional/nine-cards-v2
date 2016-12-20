package cards.nine.process.cloud.impl

import cards.nine.commons.test.data.UserTestData
import cards.nine.models.types.{Business, FreeCollectionType}
import cards.nine.services.drive.models.{DriveServiceFile, DriveServiceFileSummary}
import org.joda.time.DateTime
import cards.nine.commons.test.data.CloudStorageValues._

import scala.util.Random

trait CloudStorageProcessImplData extends UserTestData {

  val driveServiceFileSummary = generateDriveServiceFileSummary
  val driveServiceFileSummarySeq: Seq[DriveServiceFileSummary] = 1 to 10 map (_ =>
                                                                                generateDriveServiceFileSummary)
  val driveServiceFileSummaryEmptySeq = Seq.empty[DriveServiceFileSummary]

  def generateDriveServiceFileSummary =
    DriveServiceFileSummary(
      uuid = java.util.UUID.randomUUID.toString,
      deviceId = Option(Random.nextString(10)),
      title = Random.nextString(10),
      createdDate = DateTime.now().minusMonths(6).toDate,
      modifiedDate = DateTime.now().minusMonths(3).toDate)

  val validCloudStorageDeviceJson =
    s"""
       |{
       | "deviceId": "$deviceId",
       | "deviceName": "$deviceName",
       | "documentVersion": $documentVersion,
       | "collections": [${generateCollectionsJson(numCollections, numItemsPerCollection).mkString(
         ",")}],
       | "moments": [${generateMomentsJson(numMoments, numTimeSlot).mkString(",")}],
       | "dockApps": [${generateDockAppsJson(numDockApps).mkString(",")}]
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
       | "collectionType": "${FreeCollectionType.name}",
       | "icon": "Collection Icon $num",
       | "category": "${Business.name}"
       |}
    """.stripMargin
  }

  def generateCollectionItemsJson(num: Int): Seq[String] = 1 to num map { i =>
    s"""
       |{
       | "itemType": "Item Type $num",
       | "title": "Item Title $num",
       | "intent": "Item intent $num"
       |}
    """.stripMargin
  }

  def generateMomentsJson(num: Int, numItems: Int): Seq[String] = 1 to num map { i =>
    s"""
       |{
       | "timeslot": [${generateTimeSlotJson(numItems).mkString(",")}],
       | "wifi": ["Wifi_Network $num", "Mobile $num "],
       | "headphones": false,
       | "momentType": "HOME",
       | "widgets": [${generateWidgetJson(numWidgets).mkString(",")}]
       |}
    """.stripMargin
  }

  def generateTimeSlotJson(num: Int): Seq[String] = 1 to num map { i =>
    s"""
       |{
       | "from": "8:00",
       | "to": "19:00",
       | "days": [0, 1, 1, 1, 1, 1, 0]
       |}
    """.stripMargin
  }

  def generateWidgetJson(num: Int): Seq[String] = 1 to num map { i =>
    s"""
       |{
       | "packageName": "package-name",
       | "className": "class-name",
       | "area": ${generateWidgetAreaJson(i)},
       | "widgetType": "APP"
       |}
    """.stripMargin
  }

  def generateWidgetAreaJson(num: Int): String =
    s"""
       |{
       | "startX": $num,
       | "startY": $num,
       | "spanX": 1,
       | "spanY": 1
       |}
  """.stripMargin

  def generateDockAppsJson(num: Int): Seq[String] = 1 to num map { i =>
    s"""
       |{
       | "name": "DockApp $num",
       | "dockType": "APP",
       | "intent": "Item intent $num",
       | "imagePath": "/path/to/image/$num",
       | "position": $num
       |}
    """.stripMargin
  }

  val invalidCloudStorageDeviceJson =
    """
      |{ "inexistendField": "Value" }
    """.stripMargin

  val driveServiceFile = DriveServiceFile(driveServiceFileSummary, validCloudStorageDeviceJson)

  val invalidDriveServiceFileJson =
    DriveServiceFile(driveServiceFileSummary, invalidCloudStorageDeviceJson)
}
