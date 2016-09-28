package com.fortysevendeg.ninecardslauncher.services.apps.models

case class Application(
  name: String,
  packageName: String,
  className: String,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)