package cards.nine.commons.test.data

import cards.nine.models._
import cards.nine.models.types._
import org.joda.time.DateTime
import cards.nine.commons.test.data.CloudStorageValues._

trait  CloudStorageTestData extends UserTestData {

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
}
