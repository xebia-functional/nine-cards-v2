package cards.nine.services.persistence

import cards.nine.models._

sealed trait FetchAppOrder

case object OrderByName extends FetchAppOrder

case object OrderByInstallDate extends FetchAppOrder

case object OrderByCategory extends FetchAppOrder

case class CreateOrUpdateDockAppRequest(
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class DeleteDockAppsRequest(where: String)

case class DeleteDockAppRequest(dockApp: PersistenceDockApp)

case class FindDockAppByIdRequest(id: Int)

case class AddMomentRequest(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[String],
  widgets: Seq[SaveWidgetRequest])

case class DeleteMomentsRequest(where: String)

case class DeleteMomentRequest(moment: Moment)

case class FindMomentByIdRequest(id: Int)

case class UpdateMomentRequest(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[String])

case class AddWidgetRequest(
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Int,
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: String,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])

case class SaveWidgetRequest(
  packageName: String,
  className: String,
  appWidgetId: Int,
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: String,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])

case class DeleteWidgetsRequest(where: String)

case class DeleteWidgetRequest(widget: PersistenceWidget)

case class UpdateWidgetRequest(
  id: Int,
  momentId: Int,
  packageName: String,
  className: String,
  appWidgetId: Option[Int],
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: String,
  label: Option[String],
  imagePath: Option[String],
  intent: Option[String])

case class UpdateWidgetsRequest(updateWidgetRequests: Seq[UpdateWidgetRequest])

