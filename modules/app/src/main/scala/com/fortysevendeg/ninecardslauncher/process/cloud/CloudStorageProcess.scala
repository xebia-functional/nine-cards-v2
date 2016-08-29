package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.cloud.models._

trait CloudStorageProcess {

  /**
    * Transform a sequence of devices into a tuple containing a possible configuration for the actual device and a list
    * with the remaining elements
    * @param devices sequence of devices
    * @return tuple of an optional CloudStorageDeviceSummary and a list of CloudStorageDeviceSummary
    * @throws CloudStorageProcessException if the service throws an error
    */
  def prepareForActualDevice[T <: CloudStorageResource](devices: Seq[T])(implicit context: ContextSupport): TaskService[(Option[T], Seq[T])]

  /**
    * Return a sequence of `CloudStorageResource` filtered by device type
    * @return sequence of `CloudStorageResource`
    * @throws CloudStorageProcessException if the service throws an error
    */
  def getCloudStorageDevices(implicit context: ContextSupport): TaskService[Seq[CloudStorageDeviceSummary]]

  /**
    * Fetch a `CloudStorageDevice` by his id
    * @param cloudStorageResourceId google drive identifier
    * @return the `CloudStorageDevice`
    * @throws CloudStorageProcessException if the device not exists or the service throws an error
    */
  def getCloudStorageDevice(cloudStorageResourceId: String): TaskService[CloudStorageDevice]

  /**
    * Create a new device in the cloud
    * @param cloudStorageDevice the device to create or update
    * @return the saved device
    * @throws CloudStorageProcessException if the services throws an error
    */
  def createCloudStorageDevice(cloudStorageDevice: CloudStorageDeviceData): TaskService[CloudStorageDevice]

  /**
    * Create or update a device the collections, moments and dockApps using as actual devices
    * @param collections the collections to be overwritten in the actual devices
    * @param moments the moments to be overwritten in the actual devices
    * @param dockApps the dockApps to be overwritten in the actual devices
    * @return the saved device
    * @throws CloudStorageProcessException if the services throws an error
    */
  def createOrUpdateActualCloudStorageDevice(collections: Seq[CloudStorageCollection], moments: Seq[CloudStorageMoment], dockApps: Seq[CloudStorageDockApp])(implicit context: ContextSupport): TaskService[CloudStorageDevice]

  /**
    * Delete a `CloudStorageDevice` by his id
    * @param cloudId identifier of the device
    * @return Unit
    * @throws CloudStorageProcessException if the device not exists or the service throws an error
    */
  def deleteCloudStorageDevice(cloudId: String): TaskService[Unit]

}

object CloudStorageProcess {

  val actualDocumentVersion = 1

}