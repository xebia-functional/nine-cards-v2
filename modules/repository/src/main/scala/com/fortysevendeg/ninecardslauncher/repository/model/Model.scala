package com.fortysevendeg.ninecardslauncher.repository.model

case class App(
  id: Int,
  data: AppData)

case class AppData(
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

case class Collection(
  id: Int,
  data: CollectionData)

case class CollectionData(
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean])

case class Card(
  id: Int,
  data: CardData)

case class CardData(
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

case class DockApp(
  id: Int,
  data: DockAppData)

case class DockAppData(
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class Moment(
  id: Int,
  data: MomentData)

case class MomentData(
  collectionId: Option[Int],
  timeslot: String,
  wifi: String,
  headphone: Boolean)

case class User(
  id: Int,
  data: UserData)

case class UserData(
  userId: Option[String],
  email: Option[String],
  sessionToken: Option[String],
  installationId: Option[String],
  deviceToken: Option[String],
  androidToken: Option[String])

