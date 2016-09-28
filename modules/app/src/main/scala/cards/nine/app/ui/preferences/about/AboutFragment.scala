package cards.nine.app.ui.preferences.about

import android.os.Bundle
import android.preference.PreferenceFragment
import com.fortysevendeg.ninecardslauncher2.R

class AboutFragment extends PreferenceFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.aboutTitle)))
    addPreferencesFromResource(R.xml.preferences_about)
  }

}