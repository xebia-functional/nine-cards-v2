package com.fortysevendeg.ninecardslauncher.process.drive

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.drive.models.{CloudStorageDevice, CloudStorageResource}

trait CloudStorageProcess {

  def getCloudStorageDevices(): ServiceDef2[Seq[CloudStorageResource], CloudStorageProcessException]

  def getCloudStorageDevice(cloudStorageResourceId: String): ServiceDef2[CloudStorageDevice, CloudStorageProcessException]

  def createOrUpdateCloudStorageDevice(cloudStorageDevice: CloudStorageDevice): ServiceDef2[Unit, CloudStorageProcessException]

}
