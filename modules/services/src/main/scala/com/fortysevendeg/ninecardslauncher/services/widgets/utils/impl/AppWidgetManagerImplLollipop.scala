package com.fortysevendeg.ninecardslauncher.services.widgets.utils.impl

import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable. Drawable
import android.os.UserManager
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.widgets.models.{Conversions, Widget}
import com.fortysevendeg.ninecardslauncher.services.widgets.utils.AppWidgetManagerCompat

import scala.collection.JavaConversions._

class AppWidgetManagerImplLollipop(implicit contextSupport: ContextSupport)
  extends AppWidgetManagerCompat
  with Conversions {

  lazy val packageManager: PackageManager = contextSupport.getPackageManager

  override def getAllProviders: Seq[Widget] = {
    for {
      user <- getUserManager.getUserProfiles.toSeq
      appWidgetProviderInfo <- getAppWidgetManager.getInstalledProvidersForProfile(user).toSeq
      label = getLabel(appWidgetProviderInfo)
      iconImage = getIconImage(appWidgetProviderInfo)
      previewImageView = getPreviewImage(appWidgetProviderInfo)
      userProfile = getUser(appWidgetProviderInfo)
    } yield toWidget(appWidgetProviderInfo, label, iconImage, previewImageView, userProfile)
  }

  override def getUser(info: AppWidgetProviderInfo) = Option(android.os.Process.myUserHandle.hashCode)

  private[this] def getLabel(info: AppWidgetProviderInfo): String = {
    info.loadLabel(packageManager)
  }

  private[this] def getIconImage(info: AppWidgetProviderInfo): Drawable = {
    info.loadIcon(contextSupport.context, 0)
  }

  private[this] def getPreviewImage(info: AppWidgetProviderInfo): Drawable = {
    info.loadPreviewImage(contextSupport.context, 0)
  }

  private[this] def getAppWidgetManager = AppWidgetManager.getInstance(contextSupport.context)

  private[this] def getUserManager = contextSupport.context.getSystemService(Context.USER_SERVICE).asInstanceOf[UserManager]

}
