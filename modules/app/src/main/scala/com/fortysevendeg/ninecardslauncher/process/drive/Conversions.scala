package com.fortysevendeg.ninecardslauncher.process.drive

import com.fortysevendeg.ninecardslauncher.process.drive.models.CloudStorageResource
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile

trait Conversions {

  def toDriveDevice(driveServiceFile: DriveServiceFile): CloudStorageResource =
    CloudStorageResource(driveServiceFile.driveId, driveServiceFile.title)

}
