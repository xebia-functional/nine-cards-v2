package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.content.Intent
import android.content.pm.{PackageManager, ResolveInfo}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.apps._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

import scala.collection.JavaConversions._
import scalaz.concurrent.Task

class AppsServicesImpl
  extends AppsServices
  with ImplicitsAppsExceptions {

  val androidFeedback = "com.google.android.feedback"
  val androidVending = "com.android.vending"

  override def getInstalledApplications(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[AppsInstalledException] {
        val packageManager = context.getPackageManager
        val apps: Seq[ResolveInfo] = packageManager.queryIntentActivities(categoryLauncherIntent(), 0).toSeq
        apps map getApplicationByResolveInfo
      }
    }
  }

  override def getApplication(packageName: String)(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[AppsInstalledException] {
        val packageManager = context.getPackageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        getApplicationByResolveInfo(packageManager.resolveActivity(intent, 0))
      }
    }
  }

  private[this] def getApplicationByResolveInfo(resolveInfo: ResolveInfo)(implicit context: ContextSupport) = {

    val packageManager = context.getPackageManager
    val packageName = resolveInfo.activityInfo.applicationInfo.packageName
    val className = resolveInfo.activityInfo.name
    val packageInfo = packageManager.getPackageInfo(packageName, 0)

    Application(
      name = resolveInfo.loadLabel(packageManager).toString,
      packageName = packageName,
      className = className,
      resourceIcon = resolveInfo.activityInfo.icon,
      colorPrimary = "", // TODO Implement in ticket 9C-272
      dateInstalled = packageInfo.firstInstallTime,
      dateUpdate = packageInfo.lastUpdateTime,
      version = packageInfo.versionCode.toString,
      installedFromGooglePlay = isFromGooglePlay(packageManager, packageName))
  }

  private[this] def isFromGooglePlay(packageManager: PackageManager, packageName: String) = {
    packageManager.getInstallerPackageName(packageName) match {
      case `androidFeedback` => true
      case `androidVending` => true
      case _  => false
    }
  }

  protected def categoryLauncherIntent(): Intent = {
    val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    mainIntent
  }
}
