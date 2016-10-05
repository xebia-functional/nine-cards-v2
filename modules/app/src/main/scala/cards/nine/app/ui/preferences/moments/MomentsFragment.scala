package cards.nine.app.ui.preferences.moments

import android.os.Bundle
import cards.nine.app.ui.preferences.commons.PreferenceChangeListenerFragment
import com.fortysevendeg.ninecardslauncher2.R

class MomentsFragment
  extends PreferenceChangeListenerFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    withActivity { activity =>
      Option(activity.getActionBar) foreach(_.setTitle(getString(R.string.momentsPrefTitle)))
    }
    addPreferencesFromResource(R.xml.preferences_moments)

  }

}
