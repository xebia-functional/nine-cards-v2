package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.types.{CollectionType, CardType}

case class CollectionProcessConfig(
  namesCategories: Map[String, String])

case class AddCollectionRequest(
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
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
  appsCategory: Option[String] = None)
