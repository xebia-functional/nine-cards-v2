package cards.nine.services.apps.impl

import android.content.Intent
import android.content.pm.{PackageManager, ResolveInfo}
import android.provider.MediaStore
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.{CatchAll, javaNull}
import cards.nine.models.ApplicationData
import cards.nine.models.types.Misc
import cards.nine.services.apps._

import scala.collection.JavaConversions._

class AppsServicesImpl
  extends AppsServices
  with ImplicitsAppsExceptions {

  val androidFeedback = "com.google.android.feedback"
  val androidVending = "com.android.vending"

  override def getInstalledApplications(implicit context: ContextSupport) = TaskService {
    CatchAll[AppsInstalledException] {
      getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_LAUNCHER))
    }
  }

  override def getApplication(packageName: String)(implicit context: ContextSupport) = TaskService {
    CatchAll[AppsInstalledException] {
      val packageManager = context.getPackageManager
      val intent = packageManager.getLaunchIntentForPackage(packageName)
      getApplicationByResolveInfo(packageManager.resolveActivity(intent, 0))
    }
  }

  override def getDefaultApps(implicit context: ContextSupport) = TaskService {
    CatchAll[AppsInstalledException] {

      val phoneApp: Option[ApplicationData] = getAppsByIntent(phoneIntent()).headOption

      val messageApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MESSAGING)).headOption

      val browserApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_BROWSER)).headOption

      val cameraApp: Option[ApplicationData] = getAppsByIntent(cameraIntent()).headOption

      val emailApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_EMAIL)).headOption

      val mapsApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MAPS)).headOption

      val musicApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MUSIC)).headOption

      val galleryApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_GALLERY)).headOption

      val calendarApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_CALENDAR)).headOption

      val marketApp: Option[ApplicationData] = getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MARKET)).headOption

      Seq(phoneApp, messageApp, browserApp, cameraApp, emailApp, mapsApp, musicApp, galleryApp, calendarApp, marketApp).flatten
    }
  }

  private[this] def getAppsByIntent(intent: Intent)(implicit context: ContextSupport): Seq[ApplicationData] = {
    val packageManager = context.getPackageManager
    val apps: Seq[ResolveInfo] = packageManager.queryIntentActivities(intent, 0).toSeq
    apps map getApplicationByResolveInfo
  }

  private[this] def getApplicationByResolveInfo(resolveInfo: ResolveInfo)(implicit context: ContextSupport) = {

    val packageManager = context.getPackageManager
    val packageName = resolveInfo.activityInfo.applicationInfo.packageName
    val className = resolveInfo.activityInfo.name
    val packageInfo = packageManager.getPackageInfo(packageName, 0)

    ApplicationData(
      name = resolveInfo.loadLabel(packageManager).toString,
      packageName = packageName,
      className = className,
      category = Misc,
      dateInstalled = packageInfo.firstInstallTime,
      dateUpdate = packageInfo.lastUpdateTime,
      version = packageInfo.versionCode.toString,
      installedFromGooglePlay = isFromGooglePlay(packageManager, packageName))
  }

  private[this] def isFromGooglePlay(packageManager: PackageManager, packageName: String) = {
    packageManager.getInstallerPackageName(packageName) match {
      case `androidFeedback` => true
      case `androidVending` => true
      case _ => false
    }
  }

   def mainIntentByCategory(category: String): Intent = {
    val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, javaNull)
    mainIntent.addCategory(category)
    mainIntent
  }

   def phoneIntent(): Intent = {
    val intent: Intent = new Intent(Intent.ACTION_DIAL, javaNull)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent
  }

   def cameraIntent(): Intent = {
    val intent: Intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA, javaNull)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent
  }
}
