package com.fortysevendeg.ninecardslauncher.process.device.models

case class AppItem(
  name: String,
  packageName: String,
  imagePath: String,
  intent: String,
  category: Option[String] = None,
  starRating: Double = .0,
  numDownloads: Option[String] = None,
  ratingsCount: Int = 0,
  commentCount: Int = 0,
  micros: Int = 0)
