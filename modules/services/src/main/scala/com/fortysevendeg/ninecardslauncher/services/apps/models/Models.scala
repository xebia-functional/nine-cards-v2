package com.fortysevendeg.ninecardslauncher.services.apps.models

case class Application(
  name: String,
  packageName: String,
  className: String,
  resourceIcon: Int,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)