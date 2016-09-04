package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.app.Fragment
import android.os.Bundle
import android.preference.{Preference, PreferenceFragment}
import com.fortysevendeg.ninecardslauncher.app.commons.{Headphones, ProbablyActivity, Weather}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.PreferencesJobs
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._

import scalaz.concurrent.Task

class DeveloperFragment
  extends PreferenceFragment
  with Contexts[Fragment]
  with FindPreferences {

  val dom = DeveloperDOM(this)

  lazy val preferencesJobs = new PreferencesJobs(new DeveloperUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.developerPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_dev)

    Task.fork(preferencesJobs.initialize().value).resolveAsync()

  }

}

case class DeveloperDOM(dom: FindPreferences) {

  def probablyActivityPreference = dom.find[Preference](ProbablyActivity)
  def headphonesPreference = dom.find[Preference](Headphones)
  def weatherPreference = dom.find[Preference](Weather)

}