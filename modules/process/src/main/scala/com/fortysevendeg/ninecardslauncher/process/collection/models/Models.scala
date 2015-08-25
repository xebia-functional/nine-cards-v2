package com.fortysevendeg.ninecardslauncher.process.collection.models

case class Collection(
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
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card] = Seq.empty) extends Serializable

case class AddCollectionRequest(
   name: String,
   collectionType: String,
   icon: String,
   themedColorIndex: Int,
   appsCategory: Option[String] = None) extends Serializable

case class DeleteCollectionRequest(
   id: Int)

case class ReorderCollectionRequest(
  position: Int,
  newPosition: Int)

case class EditCollectionRequest(
  id: Int,
  name: String,
  appsCategory: Option[String] = None)

case class Card(
  id: Int,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: NineCardIntent,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None) extends Serializable

case class AddCardRequest(
  collectionId: Int,
  term: String,
  packageName: Option[String],
  intent: NineCardIntent,
  imagePath: String)

case class DeleteCardRequest(
  collectionId: Int,
  cardId: Int)

case class ReorderCardRequest(
  collectionId: Int,
  cardId: Int,
  newPosition: Int)

case class EditCardRequest(
  collectionId: Int,
  cardId: Int,
  name: String)
