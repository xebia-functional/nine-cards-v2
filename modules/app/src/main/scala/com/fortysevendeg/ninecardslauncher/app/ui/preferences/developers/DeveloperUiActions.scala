package com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers

import android.content.{ClipData, ClipboardManager, Context}
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.types.Misc
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.recognition.Weather
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Ui}

class DeveloperUiActions(dom: DeveloperDOM)(implicit contextWrapper: ContextWrapper) {

  def initialize(developerJobs: DeveloperJobs): TaskService[Unit] = {
    def clickPreference(onClick: () => Unit) = new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        onClick()
        true
      }
    }

    Ui {
      dom.androidTokenPreferences.
        setOnPreferenceClickListener(clickPreference(() => {
          developerJobs.copyAndroidToken.resolveAsync()
        }))
      dom.deviceCloudIdPreferences.
        setOnPreferenceClickListener(clickPreference(() => {
          developerJobs.copyDeviceCloudId.resolveAsync()
        }))
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
      dom.clearCacheImagesPreference.
        setOnPreferenceClickListener(clickPreference(() => {
          developerJobs.clearCacheImages.resolveAsync()
        }))
    }.toService
  }

  def copyToClipboard(maybeText: Option[String]): TaskService[Unit] = (uiShortToast(R.string.devCopiedToClipboard) ~ Ui {
    (Option(contextWrapper.application.getSystemService(Context.CLIPBOARD_SERVICE)), maybeText) match {
      case (Some(manager: ClipboardManager), Some(text)) =>
        val clip = ClipData.newPlainText(text, text)
        manager.setPrimaryClip(clip)
      case _ =>
    }
  }).toService

  def cacheCleared: TaskService[Unit] = uiShortToast(R.string.devCacheCleared).toService

  def setAppsCategorizedSummary(apps: Seq[App]): TaskService[Unit] = Ui {
    val categorizedCount = apps.count(_.category != Misc)
    val total = apps.length
    val summary = resGetString(R.string.devAppsCategorizedSummary, categorizedCount.toString, total.toString)
    dom.appsCategorizedPreferences.setSummary(summary)
  }.toService

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
