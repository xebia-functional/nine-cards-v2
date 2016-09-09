package com.fortysevendeg.ninecardslauncher.services.analytics

case class AnalyticEvent(
  screen: String,
  category: String,
  action: String,
  label: Option[String] = None,
  value: Option[Long] = None)
