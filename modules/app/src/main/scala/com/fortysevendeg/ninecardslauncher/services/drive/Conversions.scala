package com.fortysevendeg.ninecardslauncher.services.drive

import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.google.android.gms.drive.Metadata

trait Conversions {

  def toGoogleDriveFile(metadata: Metadata): DriveServiceFile =
    DriveServiceFile(
      driveId = metadata.getDriveId.getResourceId,
      title = metadata.getTitle,
      createdDate = metadata.getCreatedDate,
      modifiedDate = metadata.getModifiedDate)

}
