package com.fortysevendeg.ninecardslauncher.services.widgets.utils.impl

import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable. Drawable
import android.os.{UserHandle, UserManager}
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
      userHandle <- getUserHandle
      appWidgetProviderInfo <- getAppWidgetProviderInfo(userHandle)
    } yield {
      userHandle
      val label = getLabel(appWidgetProviderInfo)
      val iconImage = getIconImage(appWidgetProviderInfo)
      val previewImageView = getPreviewImage(appWidgetProviderInfo)
      val userHashCode = getUser(appWidgetProviderInfo)
      toWidget(appWidgetProviderInfo, label, iconImage, previewImageView, userHashCode)
    }
  }

  protected def getUserHandle = contextSupport.context.getSystemService(Context.USER_SERVICE).asInstanceOf[UserManager].getUserProfiles.toSeq

  protected def getAppWidgetProviderInfo(userHandle: UserHandle) = AppWidgetManager.getInstance(contextSupport.context).getInstalledProvidersForProfile(userHandle).toSeq

  protected def getLabel(implicit info: AppWidgetProviderInfo) = info.loadLabel(packageManager)

  protected def getIconImage(implicit info: AppWidgetProviderInfo) = info.loadIcon(contextSupport.context, 0)

  protected def getPreviewImage(implicit info: AppWidgetProviderInfo) = Option(info.loadPreviewImage(contextSupport.context, 0))

  protected def getUser(implicit info: AppWidgetProviderInfo) = Option(android.os.Process.myUserHandle.hashCode)

}
