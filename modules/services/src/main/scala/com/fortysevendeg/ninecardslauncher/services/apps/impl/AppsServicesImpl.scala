package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.R.attr
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

        apps map {
          resolveInfo => getApplicationByPackageName(resolveInfo.activityInfo.applicationInfo.packageName)
        }
      }
    }
  }

  override def getApplication(packageName: String)(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[AppsInstalledException] {
        getApplicationByPackageName(packageName)
      }
    }
  }

  private[this] def getApplicationByPackageName(packageName: String)(implicit context: ContextSupport) = {
    val packageManager = context.getPackageManager
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    val resources = packageManager.getResourcesForApplication(packageInfo.applicationInfo)

    Application(
      name = packageInfo.applicationInfo.name,
      packageName = packageInfo.packageName,
      className = packageInfo.applicationInfo.className,
      resourceIcon = packageInfo.applicationInfo.icon,
      colorPrimary = resources.getColor(attr.colorPrimary).toString,
      dateInstalled = packageInfo.firstInstallTime,
      dateUpdate = packageInfo.lastUpdateTime,
      version = packageInfo.versionCode.toString,
      installedFromGooglePlay = isFromGooglePlay(packageManager, packageInfo.packageName))
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
