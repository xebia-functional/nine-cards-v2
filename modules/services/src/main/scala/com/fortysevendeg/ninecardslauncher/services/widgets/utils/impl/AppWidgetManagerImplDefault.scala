package com.fortysevendeg.ninecardslauncher.services.widgets.utils.impl

import android.app.ActivityManager
import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Conversions
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.AppWidgetManagerCompat

import scala.collection.JavaConversions._

class AppWidgetManagerImplDefault(implicit contextSupport: ContextSupport)
  extends AppWidgetManagerCompat
  with Conversions{

  lazy val packageManager: PackageManager = contextSupport.getPackageManager

  override def getAllProviders = getAppWidgetProviderInfo map { appWidgetProviderInfo =>
    val label = getLabel(appWidgetProviderInfo)
    val iconImage = getIconImage(appWidgetProviderInfo)
    val previewImageView = getPreviewImage(appWidgetProviderInfo)
    val userHashCode = getUser(appWidgetProviderInfo)
    toWidget(appWidgetProviderInfo, label, iconImage, previewImageView, userHashCode)
  }

  protected def getAppWidgetProviderInfo = AppWidgetManager.getInstance(contextSupport.context).getInstalledProviders.toSeq

  protected def getLabel(info: AppWidgetProviderInfo) = info.label.trim

  protected def getIconImage(info: AppWidgetProviderInfo) = getFullResIcon(info.provider.getPackageName, info.icon)

  protected def getPreviewImage(info: AppWidgetProviderInfo) = Option(packageManager.getDrawable(info.provider.getPackageName, info.previewImage, javaNull))

  protected def getUser(info: AppWidgetProviderInfo) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) Option(android.os.Process.myUserHandle.hashCode)
    else None

  private[this] def getFullResIcon(packageName: String, iconId: Int) = packageManager.getResourcesForApplication(packageName).
      getDrawableForDensity(iconId, getActivityManager.getLauncherLargeIconDensity)

  private[this] def getActivityManager = contextSupport.context.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]
}
