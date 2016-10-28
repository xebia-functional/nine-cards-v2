package cards.nine.services.widgets.utils.impl

import android.annotation.SuppressLint
import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.os.{UserHandle, UserManager}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.models.{AppWidget, Conversions}
import cards.nine.services.widgets.utils.AppWidgetManagerCompat

import scala.collection.JavaConversions._

class AppWidgetManagerImplLollipop(implicit contextSupport: ContextSupport)
  extends AppWidgetManagerCompat
  with Conversions {

  lazy val packageManager: PackageManager = contextSupport.getPackageManager

  override def getAllProviders: Seq[AppWidget] = {
    for {
      userHandle <- getUserHandle
      appWidgetProviderInfo <- getAppWidgetProviderInfo(userHandle)
    } yield {
      val label = getLabel(appWidgetProviderInfo)
      val userHashCode = getUser(appWidgetProviderInfo)
      toWidget(appWidgetProviderInfo, label, userHashCode)
    }
  }

  @SuppressLint(Array("NewApi"))
  protected def getUserHandle = contextSupport.context.getSystemService(Context.USER_SERVICE).asInstanceOf[UserManager].getUserProfiles.toSeq

  @SuppressLint(Array("NewApi"))
  protected def getAppWidgetProviderInfo(userHandle: UserHandle) = AppWidgetManager.getInstance(contextSupport.context).getInstalledProvidersForProfile(userHandle).toSeq

  @SuppressLint(Array("NewApi"))
  protected def getLabel(implicit info: AppWidgetProviderInfo) = info.loadLabel(packageManager)

  @SuppressLint(Array("NewApi"))
  protected def getUser(implicit info: AppWidgetProviderInfo) = Option(android.os.Process.myUserHandle.hashCode)

}
