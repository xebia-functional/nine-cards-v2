package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.google.GoogleServiceClient
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.cloud.models._

trait CloudStorageProcess {

  /**
    * Creates the cloud storage API client
    * The ContextSupport should have an original Context of type CloudStorageClientListener
    * @param account the email for the client
    * @return the GoogleAPIClient
    */
  def createCloudStorageClient(account: String)(implicit contextSupport: ContextSupport): TaskService[GoogleServiceClient]

  /**
    * Transform a sequence of devices into a tuple containing a possible configuration for the actual device and a list
    * with the remaining elements
    * @param client the google API client
    * @param devices sequence of devices
    * @return tuple of an optional CloudStorageDeviceSummary and a list of CloudStorageDeviceSummary
    * @throws CloudStorageProcessException if the service throws an error
    */
  def prepareForActualDevice[T <: CloudStorageResource](
    client: GoogleServiceClient,
    devices: Seq[T])(implicit context: ContextSupport): TaskService[(Option[T], Seq[T])]

  /**
    * Return a sequence of `CloudStorageResource` filtered by device type
    * @param client the google API client
    * @return sequence of `CloudStorageResource`
    * @throws CloudStorageProcessException if the service throws an error
    */
  def getCloudStorageDevices(
    client: GoogleServiceClient)(implicit context: ContextSupport): TaskService[Seq[CloudStorageDeviceSummary]]

  /**
    * Fetch a `CloudStorageDevice` by his id
    * @param cloudStorageResourceId google drive identifier
    * @return the `CloudStorageDevice`
    * @throws CloudStorageProcessException if the device not exists or the service throws an error
    */
  def getCloudStorageDevice(
    client: GoogleServiceClient,
    cloudStorageResourceId: String): TaskService[CloudStorageDevice]

  /**
    * Fetch the raw content of a device by his id
    * @param client the google API client
    * @param cloudStorageResourceId google drive identifier
    * @return the `RawCloudStorageDevice`
    * @throws CloudStorageProcessException if the device not exists or the service throws an error
    */
  def getRawCloudStorageDevice(
    client: GoogleServiceClient,
    cloudStorageResourceId: String): TaskService[RawCloudStorageDevice]

  /**
    * Create or update a device in the cloud
    * @param client the google API client
    * @param maybeCloudId None if a new device should be created. Some(id) for updating an existing device
    * @param cloudStorageDevice the device to create or update
    * @return the saved device
    * @throws CloudStorageProcessException if the services throws an error
    */
  def createOrUpdateCloudStorageDevice(
    client: GoogleServiceClient,
    maybeCloudId: Option[String],
    cloudStorageDevice: CloudStorageDeviceData): TaskService[CloudStorageDevice]

  /**
    * Create or update a device the collections, moments and dockApps using as actual devices
    * @param client the google API client
    * @param collections the collections to be overwritten in the actual devices
    * @param moments the moments to be overwritten in the actual devices
    * @param dockApps the dockApps to be overwritten in the actual devices
    * @return the saved device
    * @throws CloudStorageProcessException if the services throws an error
    */
  def createOrUpdateActualCloudStorageDevice(
    client: GoogleServiceClient,
    collections: Seq[CloudStorageCollection],
    moments: Seq[CloudStorageMoment],
    dockApps: Seq[CloudStorageDockApp])(implicit context: ContextSupport): TaskService[CloudStorageDevice]

  /**
    * Delete a `CloudStorageDevice` by his id
    * @param client the google API client
    * @param cloudId identifier of the device
    * @return Unit
    * @throws CloudStorageProcessException if the device not exists or the service throws an error
    */
  def deleteCloudStorageDevice(
    client: GoogleServiceClient,
    cloudId: String): TaskService[Unit]

}

object CloudStorageProcess {

  val actualDocumentVersion = 1

}