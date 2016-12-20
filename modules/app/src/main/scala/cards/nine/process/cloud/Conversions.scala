package cards.nine.process.cloud

import cards.nine.app.ui.wizard.models.UserCloudDevice
import cards.nine.models.NineCardsIntentImplicits._
import cards.nine.models._
import cards.nine.models.{UserV1Collection, UserV1CollectionItem, UserV1Device}
import cards.nine.services.drive.models.DriveServiceFileSummary
import play.api.libs.json.Json

object Conversions extends NineCardsIntentConversions {

  def toCloudStorageDeviceSummary(
      driveServiceFile: DriveServiceFileSummary,
      maybeCloudId: Option[String]): CloudStorageDeviceSummary =
    CloudStorageDeviceSummary(
      cloudId = driveServiceFile.uuid,
      deviceName = driveServiceFile.title,
      deviceId = driveServiceFile.deviceId,
      createdDate = driveServiceFile.createdDate,
      modifiedDate = driveServiceFile.modifiedDate,
      currentDevice = maybeCloudId contains driveServiceFile.uuid)

  def toCloudStorageDevice(userDevice: UserV1Device) =
    CloudStorageDeviceData(
      deviceId = userDevice.deviceId,
      deviceName = userDevice.deviceName,
      documentVersion = CloudStorageProcess.actualDocumentVersion,
      userDevice.collections map toCloudStorageCollection,
      moments = None,
      dockApps = None)

  def toCloudStorageCollection(userCollection: UserV1Collection) =
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

  def toCloudStorageCollectionItem(userCollectionItem: UserV1CollectionItem) =
    CloudStorageCollectionItem(
      itemType = userCollectionItem.itemType.name,
      title = userCollectionItem.title,
      intent = userCollectionItem.intent)

  def toCloudStorageCollection(collection: Collection, widgets: Option[Seq[Widget]]) =
    CloudStorageCollection(
      name = collection.name,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = Some(collection.sharedCollectionSubscribed),
      items = collection.cards map toCloudStorageCollectionItem,
      collectionType = collection.collectionType,
      icon = collection.icon,
      category = collection.appsCategory,
      moment = collection.moment map (moment => toCloudStorageMoment(moment, widgets)))

  def toCloudStorageCollectionItem(card: Card) =
    CloudStorageCollectionItem(
      itemType = card.cardType.name,
      title = card.term,
      intent = Json.toJson(card.intent).toString())

  def toCloudStorageMoment(moment: Moment, widgets: Option[Seq[Widget]]) =
    CloudStorageMoment(
      timeslot = moment.timeslot map toCloudStorageMomentTimeSlot,
      wifi = moment.wifi,
      headphones = moment.headphone,
      momentType = moment.momentType,
      widgets = widgets map toCloudStorageWidgetSeq)

  def toCloudStorageWidgetSeq(widgets: Seq[Widget]) =
    widgets map toCloudStorageWidget

  def toCloudStorageWidget(widget: Widget) =
    CloudStorageWidget(
      packageName = widget.packageName,
      className = widget.className,
      area = toCloudStorageWidgetArea(widget.area),
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent map nineCardIntentToJson)

  def toCloudStorageWidgetArea(area: WidgetArea) =
    CloudStorageWidgetArea(
      startX = area.startX,
      startY = area.startY,
      spanX = area.spanX,
      spanY = area.spanY)

  def toCloudStorageMomentTimeSlot(timeSlot: MomentTimeSlot) =
    CloudStorageMomentTimeSlot(from = timeSlot.from, to = timeSlot.to, days = timeSlot.days)

  def toCloudStorageDockApp(dockApp: DockApp) =
    CloudStorageDockApp(
      name = dockApp.name,
      dockType = dockApp.dockType,
      intent = Json.toJson(dockApp.intent).toString(),
      imagePath = dockApp.imagePath,
      position = dockApp.position)

  def toUserCloudDevice(device: CloudStorageDeviceSummary) =
    UserCloudDevice(
      deviceName = device.deviceName,
      cloudId = device.cloudId,
      currentDevice = device.currentDevice,
      modifiedDate = device.modifiedDate)

}
