package cards.nine.process.cloud

import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserCloudDevice
import cards.nine.process.cloud.models._
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models.{Card, Collection, Moment, MomentTimeSlot}
import cards.nine.process.commons.types.WidgetType
import cards.nine.process.device.models.DockApp
import cards.nine.process.userv1.models.{UserV1Collection, UserV1CollectionItem, UserV1Device}
import cards.nine.process.widget.models.{WidgetArea, AppWidget}
import cards.nine.services.drive.models.DriveServiceFileSummary
import play.api.libs.json.Json

object Conversions {

  def toCloudStorageDeviceSummary(driveServiceFile: DriveServiceFileSummary, maybeCloudId: Option[String]): CloudStorageDeviceSummary =
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
      itemType = userCollectionItem.itemType,
      title = userCollectionItem.title,
      intent = userCollectionItem.intent)

  def toCloudStorageCollection(collection: Collection, widgets: Option[Seq[AppWidget]]) =
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

  def toCloudStorageMoment(moment: Moment, widgets: Option[Seq[AppWidget]]) =
    CloudStorageMoment(
      timeslot = moment.timeslot map toCloudStorageMomentTimeSlot,
      wifi = moment.wifi,
      headphones = moment.headphone,
      momentType = moment.momentType,
      widgets = widgets map toCloudStorageWidgetSeq)

  def toCloudStorageWidgetSeq(widgets: Seq[AppWidget]) =
    widgets map toCloudStorageWidget

  def toCloudStorageWidget(widget: AppWidget) =
    CloudStorageWidget(
      packageName = widget.packageName,
      className = widget.className,
      area = toCloudStorageWidgetArea(widget.area),
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

  def toCloudStorageWidgetArea(area: WidgetArea) =
    CloudStorageWidgetArea(
      startX = area.startX,
      startY = area.startY,
      spanX = area.spanX,
      spanY = area.spanY)

  def toCloudStorageMomentTimeSlot(timeSlot: MomentTimeSlot) =
    CloudStorageMomentTimeSlot(
      from = timeSlot.from,
      to = timeSlot.to,
      days = timeSlot.days)

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
      fromV1 = false,
      modifiedDate = device.modifiedDate)

  def toUserCloudDevice(device: CloudStorageDevice) =
    UserCloudDevice(
      deviceName = device.data.deviceName,
      cloudId = device.cloudId,
      currentDevice = false,
      fromV1 = true,
      modifiedDate = new java.util.Date())
}
