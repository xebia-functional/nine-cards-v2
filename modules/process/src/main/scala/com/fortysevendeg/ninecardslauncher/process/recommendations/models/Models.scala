package com.fortysevendeg.ninecardslauncher.process.recommendations.models

case class RecommendedApp(
  packageName: String,
  title: String,
  icon: Option[String],
  downloads: String,
  starts: Double,
  description: Option[String],
  free: Boolean,
  screenshots: Seq[String])