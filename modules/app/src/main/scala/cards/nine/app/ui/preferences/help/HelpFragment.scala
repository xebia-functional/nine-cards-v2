package cards.nine.app.ui.preferences.help

import android.os.Bundle
import android.preference.PreferenceFragment
import com.fortysevendeg.ninecardslauncher.R

class HelpFragment extends PreferenceFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.helpTitle)))
    addPreferencesFromResource(R.xml.preferences_help)
  }

}