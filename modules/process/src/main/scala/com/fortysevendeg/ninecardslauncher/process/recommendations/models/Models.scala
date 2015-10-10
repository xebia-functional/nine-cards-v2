package com.fortysevendeg.ninecardslauncher.process.recommendations.models

case class RecommendedApp(
  packageName: String,
  title: String,
  icon: Option[String])