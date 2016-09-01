package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.os.Bundle
import com.fortysevendeg.ninecardslauncher2.R

class LookFeelFragment
  extends PreferenceChangeListenerFragment {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    withActivity { activity =>
      Option(activity.getActionBar) foreach(_.setTitle(getString(R.string.lookFeelPrefTitle)))
    }
    addPreferencesFromResource(R.xml.preferences_lookfeel)
  }

}
