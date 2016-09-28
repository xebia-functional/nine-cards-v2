package com.fortysevendeg.ninecardslauncher.services.drive.models

import java.util.Date

case class DriveServiceFileSummary(
  uuid: String,
  deviceId: Option[String],
  title: String,
  createdDate: Date,
  modifiedDate: Date)

case class DriveServiceFile(
  summary: DriveServiceFileSummary,
  content: String)