package com.fortysevendeg.ninecardslauncher.process.commons.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, CollectionType, NineCardCategory, NineCardsMoment}

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card] = Seq.empty,
  moment: Option[Moment]) extends Serializable

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
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment])

case class MomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])