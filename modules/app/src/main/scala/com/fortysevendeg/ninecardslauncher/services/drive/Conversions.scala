package com.fortysevendeg.ninecardslauncher.services.drive

import com.fortysevendeg.ninecardslauncher.services.drive.models.GoogleDriveFile
import com.google.android.gms.drive.Metadata

trait Conversions {

  def toGoogleDriveFile(metadata: Metadata): GoogleDriveFile =
    GoogleDriveFile(metadata.getTitle, metadata.getDriveId.asDriveFile())

}
