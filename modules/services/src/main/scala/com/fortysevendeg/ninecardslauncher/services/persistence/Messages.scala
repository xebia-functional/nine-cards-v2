package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.services.persistence.models._

sealed trait FetchAppOrder

case object OrderByName extends FetchAppOrder

case object OrderByInstallDate extends FetchAppOrder

case object OrderByCategory extends FetchAppOrder

case class AddAppRequest(
  name: String,
  packageName: String,
  className: String,
  category: String,
  imagePath: String,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)

case class DeleteAppsRequest(where: String)

case class UpdateAppRequest(
  id: Int,
  name: String,
  packageName: String,
  className: String,
  category: String,
  imagePath: String,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)

case class AddCardRequest(
  collectionId: Option[Int] = None,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  notification: Option[String] = None)

case class AddCardWithCollectionIdRequest(
  collectionId: Int,
  cards: Seq[AddCardRequest])

case class DeleteCardsRequest(where: String)

case class DeleteCardRequest(card: Card)

case class FindCardByIdRequest(id: Int)

case class FetchCardsByCollectionRequest(collectionId: Int)

case class UpdateCardsRequest(updateCardRequests: Seq[UpdateCardRequest])

case class UpdateCardRequest(
  id: Int,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  notification: Option[String] = None)

case class CardItem(
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  notification: Option[String] = None)

case class AddCollectionRequest(
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean],
  cards: Seq[AddCardRequest],
  moment: Option[AddMomentRequest])

case class DeleteCollectionsRequest(where: String)

case class DeleteCollectionRequest(collection: Collection)

case class FetchCollectionByPositionRequest(position: Int)

case class FetchCollectionBySharedCollectionRequest(sharedCollectionId: String)

case class FindCollectionByIdRequest(id: Int)

case class UpdateCollectionRequest(
  id: Int,
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean],
  cards: Seq[Card])

case class UpdateCollectionsRequest(updateCollectionsRequests: Seq[UpdateCollectionRequest])

case class AddUserRequest(
  email: Option[String],
  apiKey: Option[String],
  sessionToken: Option[String],
  deviceToken: Option[String],
  marketToken: Option[String],
  name: Option[String],
  avatar: Option[String],
  cover: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String])

case class DeleteUsersRequest(where: String)

case class DeleteUserRequest(user: User)

case class FindUserByIdRequest(id: Int)

case class UpdateUserRequest(
  id: Int,
  email: Option[String],
  apiKey: Option[String],
  sessionToken: Option[String],
  deviceToken: Option[String],
  marketToken: Option[String],
  name: Option[String],
  avatar: Option[String],
  cover: Option[String],
  deviceName: Option[String],
  deviceCloudId: Option[String])

case class CreateOrUpdateDockAppRequest(
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class DeleteDockAppsRequest(where: String)

case class DeleteDockAppRequest(dockApp: DockApp)

case class FindDockAppByIdRequest(id: Int)

case class AddMomentRequest(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[String])

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

case class DeleteWidgetsRequest(where: String)

case class DeleteWidgetRequest(widget: Widget)

case class UpdateWidgetRequest(
  id: Int,
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

case class UpdateWidgetsRequest(updateWidgetRequests: Seq[UpdateWidgetRequest])

