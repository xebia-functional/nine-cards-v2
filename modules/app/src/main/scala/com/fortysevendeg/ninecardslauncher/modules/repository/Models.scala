package com.fortysevendeg.ninecardslauncher.modules.repository

case class Collection(
  id: Int,
  position: Int,
  name: String,
  `type`: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean,
  cards: Seq[Card]) extends Serializable

case class Card(
  id: Int,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  `type`: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None) extends Serializable
