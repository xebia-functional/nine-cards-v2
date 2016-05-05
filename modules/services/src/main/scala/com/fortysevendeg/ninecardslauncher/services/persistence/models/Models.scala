package com.fortysevendeg.ninecardslauncher.services.persistence.models

case class App(
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

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card] = Seq.empty,
  moment: Option[Moment])

case class Card(
  id: Int,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  notification: Option[String] = None)

case class User(
  id: Int,
  userId: Option[String],
  email: Option[String],
  sessionToken: Option[String],
  installationId: Option[String],
  deviceToken: Option[String],
  androidToken: Option[String],
  name: Option[String],
  avatar: Option[String],
  cover: Option[String])

case class DockApp(
  id: Int,
  name: String,
  dockType: String,
  intent: String,
  imagePath: String,
  position: Int)

case class Moment(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[String])

case class MomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])

case class DataCounter(term: String, count: Int)