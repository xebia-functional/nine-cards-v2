package com.fortysevendeg.ninecardslauncher.services.widgets.utils.impl

import android.app.ActivityManager
import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.widgets.models.Conversions
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.AppWidgetManagerCompat

import scala.collection.JavaConversions._

class AppWidgetManagerImplDefault(implicit contextSupport: ContextSupport)
  extends AppWidgetManagerCompat
  with Conversions{

  lazy val packageManager: PackageManager = contextSupport.getPackageManager

  override def getAllProviders = {
    for {
      appWidgetProviderInfo <- getAppWidgetManager.getInstalledProviders.toSeq
      label = getLabel(appWidgetProviderInfo)
      iconImage = getIconImage(appWidgetProviderInfo)
      previewImageView = getPreviewImage(appWidgetProviderInfo)
      userProfile = getUser(appWidgetProviderInfo)
    } yield toWidget(appWidgetProviderInfo, label, previewImageView, previewImageView, userProfile)
  }

  override def getUser(info: AppWidgetProviderInfo) = {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) Option(android.os.Process.myUserHandle.hashCode)
    else None
  }

  private[this] def getLabel(info: AppWidgetProviderInfo): String = {
    info.label.trim
  }

  private[this] def getIconImage(info: AppWidgetProviderInfo): Drawable = {
    getFullResIcon(info.provider.getPackageName, info.icon)
  }

  private[this] def getFullResIcon(packageName: String, iconId: Int): Drawable = {
    packageManager.getResourcesForApplication(packageName).
      getDrawableForDensity(iconId, getActivityManager.getLauncherLargeIconDensity)
  }

  private[this] def getAppWidgetManager = AppWidgetManager.getInstance(contextSupport.context)

  private[this] def getPreviewImage(info: AppWidgetProviderInfo): Drawable = {
    packageManager.getDrawable(info.provider.getPackageName, info.previewImage, null)
  }

  private[this] def getActivityManager = contextSupport.context.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]
}
