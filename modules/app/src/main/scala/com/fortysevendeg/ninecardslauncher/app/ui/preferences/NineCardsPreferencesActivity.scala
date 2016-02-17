package com.fortysevendeg.ninecardslauncher.app.ui.preferences

import java.util

import android.preference.PreferenceActivity
import android.preference.PreferenceActivity.Header
import com.fortysevendeg.ninecardslauncher2.R

class NineCardsPreferencesActivity
  extends PreferenceActivity {

  override def onBuildHeaders(target: util.List[Header]): Unit = {
    super.onBuildHeaders(target)
    loadHeadersFromResource(R.xml.preferences_headers, target)
  }

  override def isValidFragment(fragmentName: String): Boolean = {
    fragmentName.contains("com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments")
  }
}
