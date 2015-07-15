package com.fortysevendeg.ninecardslauncher.process.device.models

case class AppCategorized(
  name: String,
  packageName: String,
  className: String,
  imagePath: String,
  category: Option[String] = None,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  ratingsCount: Option[Int] = None,
  commentCount: Option[Int] = None)
