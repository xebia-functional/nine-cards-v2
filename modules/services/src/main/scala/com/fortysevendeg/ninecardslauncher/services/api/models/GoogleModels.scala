package com.fortysevendeg.ninecardslauncher.services.api.models

case class GoogleDevice(
  name: String,
  deviceId: String,
  secretToken: String,
  permissions: Seq[String])

case class GooglePlayApp(
  docid: String,
  title: String,
  creator: String,
  descriptionHtml: Option[String],
  icon: Option[String],
  background: Option[String],
  screenshots: Seq[String],
  video: Option[String],
  details: GooglePlayDetails,
  offer: Seq[GooglePlayOffer],
  aggregateRating: GooglePlayAggregateRating)

case class GooglePlayDetails(
  appDetails: GooglePlayAppDetails)

case class GooglePlayAppDetails(
  appCategory: Seq[String],
  numDownloads: String,
  developerEmail: Option[String],
  developerName: Option[String],
  developerWebsite: Option[String],
  versionCode: Option[Int],
  versionString: Option[String],
  appType: Option[String],
  permission: Seq[String])

case class GooglePlayOffer(
  formattedAmount: String,
  micros: Long)

case class GooglePlayAggregateRating(
  ratingsCount: Int,
  commentCount: Option[Int],
  oneStarRatings: Int,
  twoStarRatings: Int,
  threeStarRatings: Int,
  fourStarRatings: Int,
  fiveStarRatings: Int,
  starRating: Double)

