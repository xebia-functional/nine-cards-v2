package cards.nine.app.ui.preferences.animations

import android.os.Bundle
import android.preference.PreferenceFragment
import com.fortysevendeg.ninecardslauncher.R

class AnimationsFragment extends PreferenceFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.animationsPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_animations)
  }

}
