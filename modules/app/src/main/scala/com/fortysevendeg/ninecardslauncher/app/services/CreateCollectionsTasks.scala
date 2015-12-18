package com.fortysevendeg.ninecardslauncher.app.services

import java.io.File

import android.content.Context
import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.device.{DockAppException, _}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserCollection
import com.fortysevendeg.ninecardslauncher2.R
import play.api.libs.json.Json
import rapture.core.Answer

import scalaz.concurrent.Task

trait CreateCollectionsTasks
  extends Conversions
  with NineCardIntentConversions {

  self: CreateCollectionService
    with ImplicitsDeviceException =>

  val defaultPackageName = "com.fortysevendeg.ninecardslauncher2"
  val defaultClassName = "com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherActivity"

  lazy val packagesForDockApps = Seq(
    Seq(("com.google.android.talk", "com.google.android.talk.SigningInActivity")),
    Seq(("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail")),
    Seq(("com.android.chrome", "com.google.android.apps.chrome.Main")),
    Seq(
      ("com.google.android.GoogleCamera", "com.android.camera.CameraLauncher"),
      ("com.oneplus.camera", "com.oneplus.camera.OPCameraActivity"))
  )

  def createNewConfiguration: ServiceDef2[Seq[Collection], ResetException with AppException with ContactException with CollectionException with DockAppException] =
    for {
      - <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ = setProcess(GettingAppsProcess)
      _ <- generateDockApps()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(LoadingConfigProcess)
      contacts <- di.deviceProcess.getFavoriteContacts
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromUnformedItems(toSeqUnformedApp(apps), toSeqUnformedContact(contacts))
    } yield collections

   def loadConfiguration(deviceId: String): ServiceDef2[Seq[Collection], ResetException with AppException with CreateBitmapException with UserConfigException with CollectionException with DockAppException] =
    for {
      - <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.saveInstalledApps
      _ <- generateDockApps()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ = setProcess(GettingAppsProcess)
      userCollections <- di.userConfigProcess.getUserCollection(deviceId)
      _ = setProcess(LoadingConfigProcess)
      bitmaps <- di.deviceProcess.createBitmapsFromPackages(getAppsNotInstalled(apps, userCollections))
      _ = setProcess(CreatingCollectionsProcess)
      collections <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(userCollections))
    } yield collections

  private[this] def getAppsNotInstalled(apps: Seq[App], userCollections: Seq[UserCollection]): Seq[String] = {
    val intents = userCollections flatMap (_.items map (item => Json.parse(item.intent).as[NineCardIntent]))
    intents flatMap {
      _.extractPackageName() flatMap { pn =>
        if (!apps.exists(_.packageName == pn)) Option(pn) else None
      }
    }
  }

  private[this] def generateDockApps() =  Service {
    val tasks = packagesForDockApps.indices map { position =>
      val maybeApp = packagesForDockApps(position) find { app =>
        val (packageName, className) = app
        val imagePath = getImagePath(packageName, className)
        new File(imagePath).exists()
      }
      val (packageName, className) = maybeApp getOrElse((defaultPackageName, defaultClassName))
      val intent = toNineCardIntent(packageName, className)
      val imagePath = getImagePath(packageName, className)
      di.deviceProcess.saveDockApp(packageName, intent, imagePath, position).run
    }
    Task.gatherUnordered(tasks) map (c => CatchAll[DockAppException](c.collect { case Answer(r) => r}))
  }

  // TODO We should move ResourceUtils methods to ContextSupport and remove this method
  private[this] def getImagePath(packageName: String, className: String): String ={
    val dirPath = getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE).getPath
    s"$dirPath/${packageName.toLowerCase.replace(".", "_")}_${className.toLowerCase.replace(".", "_")}"
  }

}
