package cards.nine.commons.test.data

import cards.nine.models._
import cards.nine.models.types._
import org.joda.time.DateTime
import cards.nine.commons.test.data.CloudStorageValues._
import play.api.libs.json.Json
import cards.nine.models.NineCardsIntentImplicits._

trait CloudStorageTestData extends UserTestData {

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
      itemType = s"Item Type $num",
      title = s"Item Title $num",
      intent = s"""{ \"Item intent\":\"$num\"}""")
  }

  def generateMoments(num: Int, numItems: Int): Seq[CloudStorageMoment] = 1 to num map { i =>
    CloudStorageMoment(
      timeslot = generateTimeSlots(numItems),
      wifi = Seq(s"Wifi_Network $num", s"Mobile $num "),
      headphones = false,
      momentType = NineCardsMoment(momentType),
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
      intent = s"""{ \"Item intent\":\"$num\"}""",
      imagePath = s"/path/to/image/$num",
      position = num)
  }

  val cloudStorageDevice = generateCloudStorageDevice(
    cloudId = cloudId,
    minusDays = 1,
    deviceId = deviceId)

  val momentSeq: Option[Seq[MomentData]] = cloudStorageDevice.data.moments map (_ map {
    case moment => MomentData(
      collectionId = None,
      timeslot = moment.timeslot map { timeSlot => MomentTimeSlot(
        from = timeSlot.from,
        to = timeSlot.to,
        days = timeSlot.days)
      },
      wifi = moment.wifi,
      headphone = moment.headphones,
      momentType = moment.momentType,
      widgets = moment.widgets map (_ map { widget => WidgetData(
        packageName = widget.packageName,
        className = widget.className,
        appWidgetId = None,
        area = WidgetArea(
          startX = widget.area.startX,
          startY = widget.area.startY,
          spanX = widget.area.spanX,
          spanY = widget.area.spanY),
        widgetType = widget.widgetType,
        label = widget.label,
        imagePath = widget.imagePath,
        intent = widget.intent map (intentStr => Json.parse(intentStr).as[NineCardsIntent]))
      }))
  })


}
