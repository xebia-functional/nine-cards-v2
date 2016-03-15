package com.fortysevendeg.ninecardslauncher.app.services

import java.io.File

import android.content.pm.ResolveInfo
import android.content.{Context, Intent}
import android.util.Log
import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageCollection
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{DockAppException, _}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import play.api.libs.json.Json
import rapture.core.Answer

import scala.collection.JavaConverters._
import scalaz.concurrent.Task

trait CreateCollectionsTasks
  extends Conversions
  with NineCardIntentConversions {

  self: CreateCollectionService
    with ImplicitsDeviceException =>

  val dockAppsSize = 4

  def createNewConfiguration: ServiceDef2[Seq[Collection], ResetException with AppException with ContactException with CollectionException with DockAppException] =
    for {
      - <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ = setProcess(GettingAppsProcess)
      _ <- di.deviceProcess.generateDockApps(dockAppsSize)
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(LoadingConfigProcess)
      contacts <- di.deviceProcess.getFavoriteContacts
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
    } yield collections

   def loadConfiguration(
     client: GoogleApiClient,
     account: String,
     deviceId: String): ServiceDef2[Seq[Collection], ResetException with AppException with CreateBitmapException with CloudStorageProcessException with CollectionException with DockAppException] = {
     val cloudStorageProcess = di.createCloudStorageProcess(client, account)
     for {
       - <- di.deviceProcess.resetSavedItems()
       _ <- di.deviceProcess.saveInstalledApps
       _ <- di.deviceProcess.generateDockApps(dockAppsSize)
       apps <- di.deviceProcess.getSavedApps(GetByName)
       _ = setProcess(GettingAppsProcess)
       cloudStorageDevice <- cloudStorageProcess.getCloudStorageDeviceByAndroidId(deviceId)
       _ = setProcess(LoadingConfigProcess)
       bitmaps <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(apps, cloudStorageDevice.collections))
       _ = setProcess(CreatingCollectionsProcess)
       collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(cloudStorageDevice.collections))
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
