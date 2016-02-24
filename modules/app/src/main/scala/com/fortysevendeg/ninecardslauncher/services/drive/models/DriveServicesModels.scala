package com.fortysevendeg.ninecardslauncher.services.drive.models

import java.util.Date

case class DriveServiceFile(
  googleDriveId: String,
  fileId: Option[String],
  title: String,
  createdDate: Date,
  modifiedDate: Date)