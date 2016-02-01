package com.fortysevendeg.ninecardslauncher.services.drive.models

import java.util.Date

case class DriveServiceFile(
  driveId: String,
  title: String,
  createdDate: Date,
  modifiedDate: Date)