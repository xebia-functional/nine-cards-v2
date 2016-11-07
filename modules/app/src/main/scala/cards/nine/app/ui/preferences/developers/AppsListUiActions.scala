package cards.nine.app.ui.preferences.developers

import android.graphics.drawable.BitmapDrawable
import android.preference.Preference
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.models.ApplicationData
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.util.{Success, Try}

class AppsListUiActions(dom: AppsListDOM)(implicit contextWrapper: ContextWrapper) {

  def loadApps(apps: Seq[ApplicationData]): TaskService[Unit] = Ui {
    val packageManager = contextWrapper.bestAvailable.getPackageManager
    apps foreach { app =>
      val preference = new Preference(contextWrapper.bestAvailable)
      preference.setTitle(app.name)
      preference.setSummary(s"${app.category.getName} (${app.packageName})")
      Try {
        packageManager.getApplicationIcon(app.packageName).asInstanceOf[BitmapDrawable]
      } match {
        case Success(drawable) => preference.setIcon(drawable)
        case _ => preference.setIcon(R.drawable.ic_launcher)
      }
      dom.appsListPreferenceCategory.addPreference(preference)
    }
  }.toService

}
