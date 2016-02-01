package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageDevice, CloudStorageResource}

trait CloudStorageProcess {

  /**
    * Return a sequence of `CloudStorageResource` filtered by device type
    * @return sequence of `CloudStorageResource`
    * @throws CloudStorageProcessException if the service throws an error
    */
  def getCloudStorageDevices(): ServiceDef2[Seq[CloudStorageResource], CloudStorageProcessException]

  /**
    * Fetch a `CloudStorageDevice` by his id
    * @param cloudStorageResourceId identifier of the devices
    * @return the `CloudStorageDevice`
    * @throws CloudStorageProcessException if the device not exists or the service throws an error
    */
  def getCloudStorageDevice(cloudStorageResourceId: String): ServiceDef2[CloudStorageDevice, CloudStorageProcessException]

  /**
    * Create or update a device in the cloud
    * @param cloudStorageDevice the device to create or update
    * @throws CloudStorageProcessException if the services throws an error
    */
  def createOrUpdateCloudStorageDevice(cloudStorageDevice: CloudStorageDevice): ServiceDef2[Unit, CloudStorageProcessException]

}

object CloudStorageProcess {

  val actualDocumentVersion = 1

}