package cards.nine.app.ui.preferences.lookandfeel

import android.os.Bundle
import cards.nine.app.ui.preferences.commons.PreferenceChangeListenerFragment
import com.fortysevendeg.ninecardslauncher.R

class LookFeelFragment extends PreferenceChangeListenerFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    withActivity { activity =>
      Option(activity.getActionBar) foreach (_.setTitle(getString(R.string.lookFeelPrefTitle)))
    }
    addPreferencesFromResource(R.xml.preferences_lookfeel)
  }

}
