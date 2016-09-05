package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.recognition.Weather
import macroid.Ui

class DeveloperUiActions(dom: DeveloperDOM) {

  def initialize(developerJobs: DeveloperJobs): TaskService[Unit] = {
    def clickPreference(onClick: () => Unit) = new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        onClick()
        true
      }
    }

    Ui {
      dom.probablyActivityPreference.
        setOnPreferenceClickListener(clickPreference(() => {
          dom.probablyActivityPreference.setSummary("")
          developerJobs.loadMostProbableActivity.resolveAsync()
        }))
      dom.headphonesPreference.
        setOnPreferenceClickListener(clickPreference(() => {
          dom.headphonesPreference.setSummary("")
          developerJobs.loadHeadphone.resolveAsync()
        }))
      dom.weatherPreference.
        setOnPreferenceClickListener(clickPreference(() => {
          dom.weatherPreference.setSummary("")
          developerJobs.loadWeather.resolveAsync()
        }))
    }.toService
  }

  def setProbablyActivitySummary(summary: String): TaskService[Unit] = Ui {
    dom.probablyActivityPreference.setSummary(summary)
  }.toService

  def setHeadphonesSummary(connected: Boolean): TaskService[Unit] = Ui {
    val summary = s"Headphones ${if (connected) "connected" else "disconnected"}"
    dom.headphonesPreference.setSummary(summary)
  }.toService

  def setWeatherSummary(weather: Weather): TaskService[Unit] = Ui {
    val summary = s"${weather.conditions.headOption getOrElse "No Conditions"} Temp: ${weather.temperatureCelsius} C -  ${weather.temperatureFahrenheit} F"
    dom.weatherPreference.setSummary(summary)
  }.toService

}
