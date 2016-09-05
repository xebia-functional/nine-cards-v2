package com.fortysevendeg.ninecardslauncher.process.commons.models

import com.fortysevendeg.ninecardslauncher.process.commons.types._

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None,
  cards: Seq[Card] = Seq.empty,
  moment: Option[Moment] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean) extends Serializable

case class Card(
  id: Int,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardIntent,
  imagePath: String,
  notification: Option[String] = None) extends Serializable

case class PrivateCollection(
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None,
  cards: Seq[PrivateCard],
  moment: Option[NineCardsMoment])

case class PrivateCard(
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardIntent,
  imagePath: String)

case class Moment(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment])

case class MomentWithCollection(
  collection: Collection,
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment])

case class MomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])

case class FormedWidget(
  packageName: String,
  className: String,
  startX: Int,
  startY: Int,
  spanX: Int,
  spanY: Int,
  widgetType: WidgetType = AppWidgetType,
  label: Option[String] = None,
  imagePath: Option[String] = None,
  intent: Option[String] = None)