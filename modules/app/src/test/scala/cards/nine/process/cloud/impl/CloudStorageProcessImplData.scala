package cards.nine.process.cloud.impl

import cards.nine
import cards.nine.NineCardCategories
import cards.nine.process.cloud.models._
import cards.nine.process.commons.types._
import cards.nine.process.commons.CollectionTypes
import cards.nine.services.drive.models.{DriveServiceFile, DriveServiceFileSummary}
import cards.nine.services.persistence.models.User
import org.joda.time.DateTime

import scala.util.Random

trait CloudStorageProcessImplData {

  val activeUserId = 10

  val cloudId = "drive-id"

  val anotherCloudId = "drive-id-2"

  val account = "example@domain.com"

  val user = User(
    activeUserId,
    email = Some(account),
    apiKey = Some("api-key"),
    sessionToken = Some("session-token"),
    deviceToken = Some("device-token"),
    marketToken = Some("android-token"),
    name = None,
    avatar = None,
    cover = None,
    deviceName = Some("device"),
    deviceCloudId = Some(cloudId))

  val driveServiceFileSummary = generateDriveServiceFileSummary

  val driveServiceFileSummarySeq: Seq[DriveServiceFileSummary] = 1 to 10 map (_ => generateDriveServiceFileSummary)

  val driveServiceFileSummaryEmptySeq = Seq.empty[DriveServiceFileSummary]

  def generateDriveServiceFileSummary =
    DriveServiceFileSummary(
      uuid = java.util.UUID.randomUUID.toString,
      deviceId = Option(Random.nextString(10)),
      title = Random.nextString(10),
      createdDate = DateTime.now().minusMonths(6).toDate,
      modifiedDate = DateTime.now().minusMonths(3).toDate)

  val deviceId = "device-id"

  val anotherDeviceId = "device-id-2"

  val deviceName = "device-name"

  val packageName = "package-name"

  val className = "class-name"

  val documentVersion = 1

  val numCollections = 1

  val numItemsPerCollection = 1

  val numMoments = 2

  val numTimeSlot = 2

  val numDockApps = 4

  val numWidgets = 2

  val momentType = Option("HOME")

  val widgetType = "APP"

  def generateCloudStorageDeviceData(deviceId: String = deviceId) =
    CloudStorageDeviceData(
      deviceId,
      deviceName,
      documentVersion,
      generateCollections(numCollections, numItemsPerCollection),
      Some(generateMoments(numMoments, numTimeSlot)),
      Some(generateDockApps(numDockApps)))

  def generateCloudStorageDevice(
    cloudId: String = cloudId,
    minusDays: Int = 0,
    deviceId: String = deviceId) =
    CloudStorageDevice(
      cloudId,
      createdDate = DateTime.now().minusDays(minusDays).toDate,
      modifiedDate = DateTime.now().minusDays(minusDays).toDate,
      data = generateCloudStorageDeviceData(deviceId))

  def generateCollections(num: Int, numItems: Int): Seq[CloudStorageCollection] = 1 to num map { i =>
    CloudStorageCollection(
      name = s"Collection $num",
      originalSharedCollectionId = Option(s"Original Shared Collection Id $num"),
      sharedCollectionId = Option(s"Shared Collection Id $num"),
      sharedCollectionSubscribed = Option(true),
      generateCollectionItems(num: Int),
      collectionType = FreeCollectionType,
      icon = s"Collection Icon $num",
      category = Option(Business),
      None)
  }

  def generateCollectionItems(num: Int): Seq[CloudStorageCollectionItem] = 1 to num map { i =>
    CloudStorageCollectionItem(
      s"Item Type $num",
      s"Item Title $num",
      s"Item intent $num")
  }

  def generateMoments(num: Int, numItems: Int): Seq[CloudStorageMoment] = 1 to num map { i =>
    CloudStorageMoment(
      timeslot = generateTimeSlots(numItems),
      wifi = Seq(s"Wifi_Network $num", s"Mobile $num "),
      headphones = false,
      momentType = momentType map (NineCardsMoment(_)),
      widgets = Some(generateWidgets(numWidgets)))
  }

  def generateWidgets(num: Int): Seq[CloudStorageWidget] = 1 to num map { i =>
    CloudStorageWidget(
      packageName = packageName,
      className = className,
      area = generateWidgetArea(i),
      widgetType = WidgetType(widgetType),
      label = None,
      imagePath = None,
      intent = None)
  }

  def generateWidgetArea(num: Int): CloudStorageWidgetArea =
    CloudStorageWidgetArea(
      startX = num,
      startY = num,
      spanX = 1,
      spanY = 1)

  def generateTimeSlots(num: Int): Seq[CloudStorageMomentTimeSlot] = 1 to num map { i =>
    CloudStorageMomentTimeSlot(
      "8:00",
      "19:00",
      Seq(0, 1, 1, 1, 1, 1, 0))
  }

  def generateDockApps(num: Int): Seq[CloudStorageDockApp] = 1 to num map { i =>
    CloudStorageDockApp(
      name = s"DockApp $num",
      dockType = AppDockType,
      intent = s"Item intent $num",
      imagePath = s"/path/to/image/$num",
      position = num)
  }

  val validCloudStorageDeviceJson =
    s"""
      |{
      | "deviceId": "$deviceId",
      | "deviceName": "$deviceName",
      | "documentVersion": $documentVersion,
      | "collections": [${generateCollectionsJson(numCollections, numItemsPerCollection).mkString(",")}],
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
      | "collectionType": "${nine.CollectionTypes.free}",
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

  val driveServiceFile = DriveServiceFile(
    driveServiceFileSummary,
    validCloudStorageDeviceJson)

  val invalidDriveServiceFileJson = DriveServiceFile(
    driveServiceFileSummary,
    invalidCloudStorageDeviceJson)
}
