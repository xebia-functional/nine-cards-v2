package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, CollectionType, NineCardCategory, NineCardsMoment}

case class CollectionProcessConfig(
  namesCategories: Map[NineCardCategory, String])

case class AddCollectionRequest(
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None,
  cards: Seq[AddCardRequest],
  moment: Option[NineCardsMoment],
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean] = None)

case class AddCardRequest(
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardIntent,
  imagePath: String) extends Serializable

case class EditCollectionRequest(
  name: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None)

