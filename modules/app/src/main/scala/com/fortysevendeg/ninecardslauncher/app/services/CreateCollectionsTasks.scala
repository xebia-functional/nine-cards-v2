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
import play.api.libs.json.Json
import rapture.core.Answer

import scala.collection.JavaConverters._
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

   def loadConfiguration(
     client: GoogleApiClient,
     account: String,
     deviceId: String): ServiceDef2[Seq[Collection], ResetException with AppException with CreateBitmapException with CloudStorageProcessException with CollectionException with DockAppException] = {
     val cloudStorageProcess = di.createCloudStorageProcess(client, account)
     for {
       - <- di.deviceProcess.resetSavedItems()
       _ <- di.deviceProcess.saveInstalledApps
       _ <- generateDockApps()
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

  private[this] def generateDockApps() =  Service {
    val tasks = packagesForDockApps.indices map { position =>

//      val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
//      mainIntent.addCategory(Intent.CATEGORY_HOME)
//      mainIntent.addCategory(Intent.CATEGORY_DEFAULT)
//
//      val pkgAppsList: util.List[ResolveInfo] = getPackageManager.queryIntentActivities(mainIntent, 0)
//
//      Log.d("9Cards", s"pkgAppsList: $pkgAppsList")
//
//      val listAppInfo : util.ArrayList[ApplicationInfo] = new util.ArrayList[ApplicationInfo]()
//      for(ResolveInfo info: pkgAppsList) {
//        listAppInfo.add(info.activityInfo.applicationInfo)
//      }
//

//      val filter: IntentFilter = new IntentFilter(Intent.ACTION_MAIN)
//      filter.addCategory(Intent.CATEGORY_APP_BROWSER)
//      filter.addCategory(Intent.CATEGORY_BROWSABLE)

//      listFilters.add(filter)

//      val outFilters: util.ArrayList[IntentFilter] = new util.ArrayList[IntentFilter]()
//      val outActivities: util.ArrayList[ComponentName] = new util.ArrayList[ComponentName]()
//
//      getPackageManager.getPreferredActivities(outFilters, outActivities, javaNull)
//
//      Log.d("9Cards", s"outActivities: $outActivities")
//
//      val mapOut: Seq[(IntentFilter, ComponentName)] = (outFilters.asScala zip outActivities.asScala).toSeq
//
//      Log.d("9Cards", s"mapOut: $mapOut")
//
//      mapOut map { item =>
//        val (filter, component) = item
////        if (filter.hasAction(Intent.ACTION_CALL))
//        val countActions: Int = filter.countActions
//        val countCategories: Int = filter.countCategories
//
//        val packageName: String = component.getPackageName
//        val activityName: String = component.getClassName
//
//        Log.d("9Cards", s"package: $packageName")
//        Log.d("9Cards", s"activity: $activityName")
//        Log.d("9Cards", s"countActions: $countActions")
//        Log.d("9Cards", s"countCategories: $countCategories")
//        (0 until countActions) map { i =>
//          Log.d("9Cards", s"action: ${filter.getAction(i)}")
//        }
//        (0 until countCategories) map { i =>
//          Log.d("9Cards", s"category: ${filter.getCategory(i)}")
//        }
//
//        Log.d("9Cards", s"----------------------------------------------------")
////        else if (filter.hasAction(Intent.ACTION_MAIN) && filter.hasCategory(Intent.CATEGORY_HOME))
////          Log.d("9Cards", s"activity: $activity")
////        else if (filter.hasAction(Intent.ACTION_MAIN) && filter.hasCategory(Intent.CATEGORY_APP_MESSAGING))
////          Log.d("9Cards", s"activity: $activity")
//      }


      val messageIntent: Intent = new Intent("android.media.action.STILL_IMAGE_CAMERA")
      messageIntent.addCategory(Intent.CATEGORY_DEFAULT)
      messageIntent.addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK |
          Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

//      val message: ResolveInfo = getPackageManager.resolveActivity(messageIntent, 0)
      val messageList: List[ResolveInfo] = getPackageManager.queryIntentActivities(messageIntent, 0).asScala.toList


//      Log.d("9Cards", s"messageActivityInfo: ${message.resolvePackageName} --- messagePackageName: ${message.activityInfo.packageName}")
      Log.d("9Cards", s"messageList: ${messageList}")

//      val browserIntent: Intent = new Intent(Intent.ACTION_VIEW, null)
////      browserIntent.addCategory(Intent.CATEGORY_APP_BROWSER)
//      browserIntent.addCategory(Intent.CATEGORY_BROWSABLE)
//
//      val browser: ResolveInfo = getPackageManager.resolveActivity(browserIntent, 0)
//
//      Log.d("9Cards", s"browserActivityInfo: ${browser.activityInfo.name} --- browserPackageName: ${browser.activityInfo.packageName}")
//
//
//      val cameraIntent: Intent = new Intent("android.media.action.STILL_IMAGE_CAMERA")
//      cameraIntent.addCategory(Intent.CATEGORY_DEFAULT)
//
//      val camera: ResolveInfo = getPackageManager.resolveActivity(cameraIntent, 0)
//
//      Log.d("9Cards", s"cameraActivityInfo: ${camera.activityInfo.name} --- cameraPackageName: ${camera.activityInfo.packageName}")
//
//
//      val phoneIntent: Intent = new Intent(Intent.ACTION_CALL, null)
//      phoneIntent.addCategory(Intent.CATEGORY_DEFAULT)
//
//      val phone: ResolveInfo = getPackageManager.resolveActivity(phoneIntent, 0)
//      val phoneList: List[ResolveInfo] = getPackageManager.queryIntentActivities(phoneIntent, 0).asScala.toList
//
//      Log.d("9Cards", s"phoneList: ${phoneList}")
//      Log.d("9Cards", s"phoneActivityInfo: ${phone.activityInfo.name} --- phonePackageName: ${phone.activityInfo.packageName}")


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
