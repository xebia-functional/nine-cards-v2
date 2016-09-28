package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import android.app.Fragment
import android.os.Bundle
import android.preference.{Preference, PreferenceFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts

class DeveloperFragment
  extends PreferenceFragment
  with Contexts[Fragment]
  with FindPreferences {

  lazy val dom = DeveloperDOM(this)

  lazy val preferencesJobs = new DeveloperJobs(new DeveloperUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.developerPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_dev)

    preferencesJobs.initialize().resolveAsync()

  }

}

case class DeveloperDOM(dom: FindPreferences) {

  def appsCategorizedPreferences = dom.find[Preference](AppsCategorized)
  def androidTokenPreferences = dom.find[Preference](AndroidToken)
  def deviceCloudIdPreferences = dom.find[Preference](DeviceCloudId)
  def probablyActivityPreference = dom.find[Preference](ProbablyActivity)
  def headphonesPreference = dom.find[Preference](Headphones)
  def locationPreference = dom.find[Preference](Location)
  def weatherPreference = dom.find[Preference](Weather)
  def clearCacheImagesPreference = dom.find[Preference](ClearCacheImages)

}