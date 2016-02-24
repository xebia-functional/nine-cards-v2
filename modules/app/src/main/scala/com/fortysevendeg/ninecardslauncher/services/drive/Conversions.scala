package com.fortysevendeg.ninecardslauncher.services.drive

import com.fortysevendeg.ninecardslauncher.services.drive.impl.DriveServicesImpl._
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.google.android.gms.drive.Metadata

trait Conversions {

  def toGoogleDriveFile(metadata: Metadata): DriveServiceFile =
    DriveServiceFile(
      googleDriveId = metadata.getDriveId.getResourceId,
      fileId = Option(metadata.getCustomProperties.get(propertyFileId)),
      title = metadata.getTitle,
      createdDate = metadata.getCreatedDate,
      modifiedDate = metadata.getModifiedDate)

}
