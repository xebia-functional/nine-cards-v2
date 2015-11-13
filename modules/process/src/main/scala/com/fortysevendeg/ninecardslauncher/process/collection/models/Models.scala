package com.fortysevendeg.ninecardslauncher.process.collection.models

import com.fortysevendeg.ninecardslauncher.process.types.{CollectionType, CardType}

case class Collection(
  id: Int,
  position: Int,
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card] = Seq.empty) extends Serializable

case class Card(
  id: Int,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardIntent,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None) extends Serializable

