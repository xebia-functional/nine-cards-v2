package cards.nine.app.ui.preferences.analytics

import android.os.Bundle
import cards.nine.app.ui.preferences.commons.PreferenceChangeListenerFragment
import com.fortysevendeg.ninecardslauncher.R

class AnalyticsFragment extends PreferenceChangeListenerFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    withActivity { activity =>
      Option(activity.getActionBar) foreach (_.setTitle(getString(R.string.analyticsPrefTitle)))
    }
    addPreferencesFromResource(R.xml.preferences_analytics)
  }

}
