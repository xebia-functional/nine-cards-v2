package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDeviceSummary
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile

trait Conversions {

  def toDriveDevice(driveServiceFile: DriveServiceFile, deviceId: String): CloudStorageDeviceSummary =
    CloudStorageDeviceSummary(
      resourceId = driveServiceFile.googleDriveId,
      title = driveServiceFile.title,
      createdDate = driveServiceFile.createdDate,
      modifiedDate = driveServiceFile.modifiedDate,
      currentDevice = driveServiceFile.fileId contains deviceId)
}
