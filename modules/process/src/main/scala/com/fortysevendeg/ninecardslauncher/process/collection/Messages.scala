package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent

case class CollectionProcessConfig(
  namesCategories: Map[String, String])

case class AddCollectionRequest(
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None)

case class AddCardRequest(
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: NineCardIntent,
  imagePath: String)

case class EditCollectionRequest(
  name: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None)
