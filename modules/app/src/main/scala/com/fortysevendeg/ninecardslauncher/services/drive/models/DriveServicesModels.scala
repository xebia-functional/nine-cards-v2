package com.fortysevendeg.ninecardslauncher.services.drive.models

import java.util.Date

case class DriveServiceFile(
  googleDriveId: String,
  uuid: String,
  deviceId: Option[String],
  title: String,
  createdDate: Date,
  modifiedDate: Date)