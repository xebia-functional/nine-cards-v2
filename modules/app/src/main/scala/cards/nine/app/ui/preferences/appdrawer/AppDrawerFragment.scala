package cards.nine.app.ui.preferences.appdrawer

import android.app.Fragment
import android.os.Bundle
import android.preference.ListPreference
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class AppDrawerFragment
  extends PreferenceChangeListenerFragment
  with Contexts[Fragment]
  with FindPreferences {

  lazy val dom = AppDrawerDOM(this)

  lazy val appDrawerJobs = new AppDrawerJobs(new AppDrawerUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.appDrawerPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_app_drawer)
  }

  override def onStart(): Unit = {
    super.onStart()
    appDrawerJobs.initialize().resolveAsync()
  }

}

case class AppDrawerDOM(dom: FindPreferences) {

  def longPressPreference = dom.find[ListPreference](AppDrawerLongPressAction)
  def animationPreference = dom.find[ListPreference](AppDrawerAnimation)

}
