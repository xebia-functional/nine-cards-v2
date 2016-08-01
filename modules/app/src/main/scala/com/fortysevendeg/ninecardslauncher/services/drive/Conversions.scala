package com.fortysevendeg.ninecardslauncher.services.drive

import com.fortysevendeg.ninecardslauncher.services.drive.impl.DriveServicesImpl._
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFileSummary
import com.google.android.gms.drive.Metadata

trait Conversions {

  def toGoogleDriveFileSummary(metadata: Metadata): DriveServiceFileSummary =
    DriveServiceFileSummary(
      uuid = metadata.getCustomProperties.get(propertyUUID),
      deviceId = Option(metadata.getCustomProperties.get(propertyDeviceId)),
      title = metadata.getTitle,
      createdDate = metadata.getCreatedDate,
      modifiedDate = metadata.getModifiedDate)

  def toGoogleDriveFileSummary(uuid: String, metadata: Metadata): DriveServiceFileSummary =
    DriveServiceFileSummary(
      uuid = uuid,
      deviceId = Option(metadata.getCustomProperties.get(propertyDeviceId)),
      title = metadata.getTitle,
      createdDate = metadata.getCreatedDate,
      modifiedDate = metadata.getModifiedDate)

}
