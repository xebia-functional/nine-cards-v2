package cards.nine.services.apps.impl

import android.content.Intent
import android.content.pm.{PackageManager, ResolveInfo}
import android.provider.MediaStore
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
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

  override def getInstalledApplications(implicit context: ContextSupport) =
    getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_LAUNCHER))

  override def getApplication(packageName: String)(implicit context: ContextSupport) =
    for {
      pm <- packageManagerTask
      maybeIntent <- catchAll(Option(pm.getLaunchIntentForPackage(packageName)))
      maybeResolveInfo <- catchAll(maybeIntent flatMap (intent => Option(pm.resolveActivity(intent, 0))))
      applicationData <- (maybeIntent, maybeResolveInfo) match {
        case (Some(_), Some(resolveInfo)) => catchAll(getApplicationByResolveInfo(pm, resolveInfo))
        case (None, _) => TaskService.left(AppsInstalledException(s"Received a null intent for package $packageName"))
        case (_, None) => TaskService.left(AppsInstalledException(s"Received a null resolve info for package $packageName"))
      }
    } yield applicationData

  override def getDefaultApps(implicit context: ContextSupport) =
    for {
      phoneApp <- getAppsByIntent(phoneIntent()).map(_.headOption)
      messageApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MESSAGING)).map(_.headOption)
      browserApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_BROWSER)).map(_.headOption)
      cameraApp <- getAppsByIntent(cameraIntent()).map(_.headOption)
      emailApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_EMAIL)).map(_.headOption)
      mapsApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MAPS)).map(_.headOption)
      musicApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MUSIC)).map(_.headOption)
      galleryApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_GALLERY)).map(_.headOption)
      calendarApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_CALENDAR)).map(_.headOption)
      marketApp <- getAppsByIntent(mainIntentByCategory(Intent.CATEGORY_APP_MARKET)).map(_.headOption)
    } yield Seq(phoneApp, messageApp, browserApp, cameraApp, emailApp, mapsApp, musicApp, galleryApp, calendarApp, marketApp).flatten

  private[this] def getAppsByIntent(intent: Intent)(implicit context: ContextSupport): TaskService[Seq[ApplicationData]] =
    for {
      pm <- packageManagerTask
      appsData <- catchAll(pm.queryIntentActivities(intent, 0).toSeq map (getApplicationByResolveInfo(pm, _)))
    } yield appsData.filterNot(_.packageName == context.getPackageName)

  private[this] def getApplicationByResolveInfo(pm: PackageManager, resolveInfo: ResolveInfo)(implicit context: ContextSupport) = {
    val packageName = resolveInfo.activityInfo.applicationInfo.packageName
    val className = resolveInfo.activityInfo.name
    val packageInfo = pm.getPackageInfo(packageName, 0)

    ApplicationData(
      name = resolveInfo.loadLabel(pm).toString,
      packageName = packageName,
      className = className,
      category = Misc,
      dateInstalled = packageInfo.firstInstallTime,
      dateUpdated = packageInfo.lastUpdateTime,
      version = packageInfo.versionCode.toString,
      installedFromGooglePlay = isFromGooglePlay(pm, packageName))
  }

  private[this] def packageManagerTask(implicit context: ContextSupport) =
    TaskService(CatchAll[AppsInstalledException](context.getPackageManager))

  private[this] def catchAll[T](f: => T) = TaskService(CatchAll[AppsInstalledException](f))

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
