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
  colorPrimary: String,
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
  colorPrimary: String,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)

case class AddCardRequest(
  collectionId: Option[Int] = None,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class DeleteCardsRequest(where: String)

case class DeleteCardRequest(card: Card)

case class FindCardByIdRequest(id: Int)

case class FetchCardsByCollectionRequest(collectionId: Int)

case class UpdateCardRequest(
  id: Int,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class CardItem(
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class AddCollectionRequest(
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean],
  cards: Seq[AddCardRequest])

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
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean],
  cards: Seq[Card])

case class AddUserRequest(
  userId: Option[String],
  email: Option[String],
  sessionToken: Option[String],
  installationId: Option[String],
  deviceToken: Option[String],
  androidToken: Option[String])

case class DeleteUsersRequest(where: String)

case class DeleteUserRequest(user: User)

case class FindUserByIdRequest(id: Int)

case class UpdateUserRequest(
  id: Int,
  userId: Option[String],
  email: Option[String],
  sessionToken: Option[String],
  installationId: Option[String],
  deviceToken: Option[String],
  androidToken: Option[String])

case class CreateOrUpdateDockAppRequest(
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class DeleteDockAppsRequest(where: String)

case class DeleteDockAppRequest(dockApp: DockApp)

case class FindDockAppByIdRequest(id: Int)


