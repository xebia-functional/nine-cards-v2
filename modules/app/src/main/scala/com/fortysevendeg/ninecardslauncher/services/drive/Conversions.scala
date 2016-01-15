package com.fortysevendeg.ninecardslauncher.services.drive

import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.google.android.gms.drive.{DriveFile, Metadata}

trait Conversions {

  def toGoogleDriveFile(metadata: Metadata): DriveServiceFile =
    DriveServiceFile(metadata.getDriveId.getResourceId, metadata.getTitle)

  def toGoogleDriveFile(title: String, driveFile: DriveFile): DriveServiceFile =
    DriveServiceFile(driveFile.getDriveId.getResourceId, title)

}
