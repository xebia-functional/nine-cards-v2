package com.fortysevendeg.ninecardslauncher.services.apps.models

case class Application(
  name: String,
  packageName: String,
  className: String,
  resourceIcon: Int,
  colorPrimary: String,
  dateInstalled: Double,
  dateUpdate: Double,
  version: String,
  installedFromGooglePlay: Boolean)