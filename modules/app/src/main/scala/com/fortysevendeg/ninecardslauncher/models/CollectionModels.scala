package com.fortysevendeg.ninecardslauncher.models

import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntent

import scala.util.{Failure, Success, Try}

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
  cards: Seq[Card] = Seq.empty) extends Serializable

case class Card(
  id: Int,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  `type`: String,
  intent: NineCardIntent,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None) extends Serializable

case class CacheCategory(
  id: Int,
  packageName: String,
  category: String,
  starRating: Double,
  numDownloads: String,
  ratingsCount: Int,
  commentCount: Int) extends Serializable


