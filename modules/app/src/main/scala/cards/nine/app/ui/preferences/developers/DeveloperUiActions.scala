package cards.nine.app.ui.preferences.developers

import android.content.{ClipData, ClipboardManager, Context}
import android.preference.Preference
import android.preference.Preference.{OnPreferenceChangeListener, OnPreferenceClickListener}
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.Misc
import cards.nine.models.{ApplicationData, Location, WeatherState}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Ui}

class DeveloperUiActions(dom: DeveloperDOM)(implicit contextWrapper: ContextWrapper) {

  def initialize(developerJobs: DeveloperJobs): TaskService[Unit] = {
    def clickPreference(onClick: () => Unit) = new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        onClick()
        true
      }
    }

    def changePreference(onChange: (Preference, scala.Any) => Unit) = new OnPreferenceChangeListener {
      override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
        onChange(preference, newValue)
        true
      }
    }

    val density = contextWrapper.bestAvailable.getResources.getDisplayMetrics.density
    val densityString = density match {
      case d if d <= 0.75 => "LDPI"
      case d if d <= 1.0 => "MDPI"
      case d if d <= 1.5 => "HDPI"
      case d if d <= 2.0 => "XHDPI"
      case d if d <= 3.0 => "XXHDPI"
      case d if d <= 4.0 => "XXXHDPI"
      case d => d.toString
    }

    val densityDpi = contextWrapper.bestAvailable.getResources.getDisplayMetrics.densityDpi

    dom.currentDensityPreferences.setSummary(s"$densityString ($density) - $densityDpi dp")

    Ui {
      dom.backendV2UrlPreference.
        setOnPreferenceChangeListener(changePreference((p, v) => {
          p.setSummary(v.toString)
        }))
      dom.overrideBackendV2UrlPreference.
        setOnPreferenceChangeListener(changePreference((p, v) => {
          enableBackendV2Url(v.asInstanceOf[Boolean]).resolveAsync()
        }))
      dom.isStethoActivePreference.
        setOnPreferenceChangeListener(changePreference((p, v) => {
          setStethoTitle(v.asInstanceOf[Boolean]).resolveAsync()
        }))
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
      dom.locationPreference.
        setOnPreferenceClickListener(clickPreference(() => {
          dom.locationPreference.setSummary("")
          developerJobs.loadLocation.resolveAsync()
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
      dom.restartApplicationPreference.
        setOnPreferenceClickListener(clickPreference(() => {
          developerJobs.restartApplication.resolveAsync()
        }))
    }.toService
  }

  def copyToClipboard(maybeText: Option[String]): TaskService[Unit] = (uiShortToast2(R.string.devCopiedToClipboard) ~ Ui {
    (Option(contextWrapper.application.getSystemService(Context.CLIPBOARD_SERVICE)), maybeText) match {
      case (Some(manager: ClipboardManager), Some(text)) =>
        val clip = ClipData.newPlainText(text, text)
        manager.setPrimaryClip(clip)
      case _ =>
    }
  }).toService

  def cacheCleared: TaskService[Unit] = uiShortToast2(R.string.devCacheCleared).toService

  def setAppsCategorizedSummary(apps: Seq[ApplicationData]): TaskService[Unit] = Ui {
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

  def setLocationSummary(location: Location): TaskService[Unit] = Ui {
    val summary =
      s"""${location.addressLines.mkString(", ")}
         |(${location.latitude}, ${location.longitude})
         |${location.countryName.getOrElse("<No country>")} (${location.countryCode.getOrElse("-")})""".stripMargin
    dom.locationPreference.setSummary(summary)
  }.toService

  def setWeatherSummary(weather: WeatherState): TaskService[Unit] = Ui {
    val summary = s"${weather.conditions.headOption getOrElse "No Conditions"} Temp: ${weather.temperatureCelsius} C -  ${weather.temperatureFahrenheit} F"
    dom.weatherPreference.setSummary(summary)
  }.toService

  def enableBackendV2Url(enable: Boolean): TaskService[Unit] = Ui {
    dom.backendV2UrlPreference.setEnabled(enable)
  }.toService

  def setBackendV2UrlSummary(backendV2Url: String): TaskService[Unit] = Ui {
    dom.backendV2UrlPreference.setSummary(backendV2Url)
  }.toService

  def setStethoTitle(enabled: Boolean): TaskService[Unit] = Ui {
    val title = if (enabled) R.string.devIsStethoActiveTrue else R.string.devIsStethoActiveFalse
    dom.isStethoActivePreference.setTitle(title)
  }.toService

}
