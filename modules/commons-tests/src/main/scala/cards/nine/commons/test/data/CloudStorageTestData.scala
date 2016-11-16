package cards.nine.commons.test.data

import cards.nine.models._
import cards.nine.models.types._
import org.joda.time.DateTime
import cards.nine.commons.test.data.CloudStorageValues._
import play.api.libs.json.Json
import cards.nine.models.NineCardsIntentImplicits._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.DockAppValues._

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

  def generateCloudStorageDeviceSummary(
    cloudId: String = cloudId,
    minusDays: Int = 0,
    deviceId: String = deviceId,
    deviceName: String = deviceName,
    currentDevice: Boolean = true) =
    CloudStorageDeviceSummary(
      cloudId = cloudId,
      deviceId = Option(deviceId),
      deviceName = deviceName,
      createdDate = DateTime.now().minusDays(minusDays).toDate,
      modifiedDate = DateTime.now().minusDays(minusDays).toDate,
      currentDevice = currentDevice)

  def generateCollections(num: Int, numItems: Int): Seq[CloudStorageCollection] = 1 to num map { i =>
    CloudStorageCollection(
      name = collectionName + num,
      originalSharedCollectionId = Option(originalSharedCollectionId + num),
      sharedCollectionId = Option(sharedCollectionId + num),
      sharedCollectionSubscribed = Option(true),
      generateCollectionItems(num: Int),
      collectionType = collectionTypeFree,
      icon = icon + num,
      category = Option(Business),
      moment = None)
  }

  def generateCollectionItems(num: Int): Seq[CloudStorageCollectionItem] = 1 to num map { i =>
    CloudStorageCollectionItem(
      itemType = itemType + num,
      title = itemTitle + num,
      intent = intentCloud.format(num))
  }

  def generateMoments(num: Int, numItems: Int): Seq[CloudStorageMoment] = 1 to num map { i =>
    CloudStorageMoment(
      timeslot = generateTimeSlots(numItems),
      wifi = Seq(wifiNetwork + num, nameMobile + num),
      headphones = headphone,
      momentType = NineCardsMoment(momentTypeHome),
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
      spanX = spanX,
      spanY = spanY)

  def generateTimeSlots(num: Int): Seq[CloudStorageMomentTimeSlot] = 1 to num map { i =>
    CloudStorageMomentTimeSlot(
      from = from,
      to = to,
      days = daysSeq)
  }

  def generateDockApps(num: Int): Seq[CloudStorageDockApp] = 1 to num map { i =>
    CloudStorageDockApp(
      name = dockAppName + num,
      dockType = AppDockType,
      intent = intentCloud.format(num),
      imagePath = dockAppImagePath + num,
      position = num)
  }

  val cloudStorageDevice = generateCloudStorageDevice(
    cloudId = cloudId,
    minusDays = 1,
    deviceId = deviceId)

  val cloudStorageDeviceSummary = generateCloudStorageDeviceSummary()

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

  val dockAppSeq = cloudStorageDevice.data.dockApps map (_ map {
    case dockApps => DockAppData(
      name = dockApps.name,
      dockType = dockApps.dockType,
      intent = Json.parse(dockApps.intent).as[NineCardsIntent],
      imagePath = dockApps.imagePath,
      position = dockApps.position)
  })

  val tokenFirebase = "token-firebase"

}
