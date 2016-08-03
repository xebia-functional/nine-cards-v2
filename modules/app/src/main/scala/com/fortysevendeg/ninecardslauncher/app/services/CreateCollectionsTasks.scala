package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageCollection
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{DockAppException, _}
import com.fortysevendeg.ninecardslauncher.process.moment.MomentException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.google.android.gms.common.api.GoogleApiClient
import play.api.libs.json.Json

trait CreateCollectionsTasks
  extends Conversions
  with NineCardIntentConversions {

  self: CreateCollectionService
    with ImplicitsDeviceException =>

  val dockAppsSize = 4

  def createNewConfiguration(
    client: GoogleApiClient,
    deviceToken: Option[String]): ServiceDef2[Seq[Collection], ResetException with AppException with ContactException with CollectionException with DockAppException with MomentException with UserException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ = setProcess(GettingAppsProcess)
      _ <- di.deviceProcess.generateDockApps(dockAppsSize)
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(LoadingConfigProcess)
      contacts <- di.deviceProcess.getFavoriteContacts
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
      momentCollections <- di.momentProcess.createMoments
      moments <- di.momentProcess.getMoments
      savedDevice <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
        collections = momentCollections.map(collection => toCloudStorageCollection(collection, collection.moment)),
        moments = moments.filter(_.collectionId.isEmpty) map toCloudStorageMoment)
      _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId, deviceToken)
    } yield collections ++ momentCollections
  }

  def loadConfiguration(
    client: GoogleApiClient,
    deviceToken: Option[String],
    cloudId: String): ServiceDef2[Seq[Collection], ResetException with AppException with CloudStorageProcessException with CollectionException with DockAppException with MomentException with UserException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ <- di.deviceProcess.generateDockApps(dockAppsSize)
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(GettingAppsProcess)
      device <- cloudStorageProcess.getCloudStorageDevice(cloudId)
      _ = setProcess(LoadingConfigProcess)
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(device.data.collections))
      momentSeq = device.data.moments map (_ map toMoment) getOrElse Seq.empty
      _ <- di.momentProcess.saveMoments(momentSeq)
      _ <- di.userProcess.updateUserDevice(device.data.deviceName, device.cloudId, deviceToken)
    } yield collections
  }

}
