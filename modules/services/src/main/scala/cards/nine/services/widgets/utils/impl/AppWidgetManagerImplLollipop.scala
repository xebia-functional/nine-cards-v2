package cards.nine.services.widgets.utils.impl

import android.appwidget.{AppWidgetManager, AppWidgetProviderInfo}
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable. Drawable
import android.os.{UserHandle, UserManager}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.services.widgets.models.{Conversions, Widget}
import cards.nine.services.widgets.utils.AppWidgetManagerCompat

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
      val label = getLabel(appWidgetProviderInfo)
      val userHashCode = getUser(appWidgetProviderInfo)
      toWidget(appWidgetProviderInfo, label, userHashCode)
    }
  }

  protected def getUserHandle = contextSupport.context.getSystemService(Context.USER_SERVICE).asInstanceOf[UserManager].getUserProfiles.toSeq

  protected def getAppWidgetProviderInfo(userHandle: UserHandle) = AppWidgetManager.getInstance(contextSupport.context).getInstalledProvidersForProfile(userHandle).toSeq

  protected def getLabel(implicit info: AppWidgetProviderInfo) = info.loadLabel(packageManager)

  protected def getUser(implicit info: AppWidgetProviderInfo) = Option(android.os.Process.myUserHandle.hashCode)

}
