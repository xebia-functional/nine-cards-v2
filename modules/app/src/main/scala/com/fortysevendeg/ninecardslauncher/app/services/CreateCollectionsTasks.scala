package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageCollection
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{DockAppException, _}
import com.fortysevendeg.ninecardslauncher.process.moment.MomentException
import com.google.android.gms.common.api.GoogleApiClient
import play.api.libs.json.Json

trait CreateCollectionsTasks
  extends Conversions
  with NineCardIntentConversions {

  self: CreateCollectionService
    with ImplicitsDeviceException =>

  val dockAppsSize = 4

  def createNewConfiguration: ServiceDef2[Seq[Collection], ResetException with AppException with ContactException with CollectionException with DockAppException with MomentException] =
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
    } yield collections ++ momentCollections

  def loadConfiguration(
   client: GoogleApiClient,
   deviceId: String): ServiceDef2[Seq[Collection], ResetException with AppException with CreateBitmapException with CloudStorageProcessException with CollectionException with DockAppException with MomentException] = {
   val cloudStorageProcess = di.createCloudStorageProcess(client)
   for {
     _ <- di.deviceProcess.resetSavedItems()
     _ <- di.deviceProcess.saveInstalledApps
     _ <- di.deviceProcess.generateDockApps(dockAppsSize)
     apps <- di.deviceProcess.getSavedApps(GetByName)
     _ = setProcess(GettingAppsProcess)
     cloudStorageDevice <- cloudStorageProcess.getCloudStorageDeviceByAndroidId(deviceId)
     _ = setProcess(LoadingConfigProcess)
     bitmaps <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(apps, cloudStorageDevice.collections))
     _ = setProcess(CreatingCollectionsProcess)
     collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(cloudStorageDevice.collections))
     momentSeq = cloudStorageDevice.moments map (_ map toMoment) getOrElse Seq.empty
     _ <- di.momentProcess.saveMoments(momentSeq)
   } yield collections
  }

  private[this] def getAppsNotInstalled(apps: Seq[App], collections: Seq[CloudStorageCollection]): Seq[String] = {
    val intents = collections flatMap (_.items map (item => Json.parse(item.intent).as[NineCardIntent]))
    intents flatMap {
      _.extractPackageName() flatMap { pn =>
        if (!apps.exists(_.packageName == pn)) Option(pn) else None
      }
    }
  }
}
