package cards.nine.app.ui.preferences.developers

import android.app.Fragment
import android.os.Bundle
import android.preference.{PreferenceCategory, PreferenceFragment}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class AppsListFragment
  extends PreferenceFragment
  with Contexts[Fragment]
  with FindPreferences {

  lazy val dom = AppsListDOM(this)

  lazy val appsListJobs = new AppsListJobs(new AppsListUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.devAppsListTitle)))
    addPreferencesFromResource(R.xml.preferences_apps_list)

    appsListJobs.initialize().resolveAsync()
  }

}

case class AppsListDOM(dom: FindPreferences) {

  def appsListPreferenceCategory = dom.findByName[PreferenceCategory]("appsList")
}