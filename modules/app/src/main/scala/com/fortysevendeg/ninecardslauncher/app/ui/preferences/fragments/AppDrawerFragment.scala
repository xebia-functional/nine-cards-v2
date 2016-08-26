package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.os.Bundle
import android.preference.{ListPreference, Preference, PreferenceFragment}
import android.app.Fragment
import android.preference.Preference.OnPreferenceChangeListener
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts

class AppDrawerFragment
  extends PreferenceFragment
  with Contexts[Fragment]
  with FindPreferences {

  lazy val preferenceValues = new NineCardsPreferencesValue

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.appDrawerPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_app_drawer)
  }

  override def onStart(): Unit = {
    super.onStart()
    reloadLongPressActionText(AppDrawerLongPressAction.readValue(preferenceValues).value)
    reloadAnimationText(AppDrawerAnimation.readValue(preferenceValues).value)
    find[ListPreference](AppDrawerLongPressAction).setOnPreferenceChangeListener(new OnPreferenceChangeListener {
      override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
        reloadLongPressActionText(newValue.toString)
        true
      }
    })

    find[ListPreference](AppDrawerAnimation).setOnPreferenceChangeListener(new OnPreferenceChangeListener {
      override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
        reloadAnimationText(newValue.toString)
        true
      }
    })
  }

  private[this] def reloadLongPressActionText(value: String) = {
    val textValue = AppDrawerLongPressActionValue(value) match {
      case AppDrawerLongPressActionOpenKeyboard => resGetString(R.string.appDrawerOpenKeyboard)
      case AppDrawerLongPressActionOpenContacts => resGetString(R.string.appDrawerOpenContacts)
    }
    find[ListPreference](AppDrawerLongPressAction).setSummary(resGetString(R.string.appDrawerLongPressSummary, textValue))
  }

  private[this] def reloadAnimationText(value: String) = {
    val textValue = AppDrawerAnimationValue(value) match {
      case AppDrawerAnimationCircle => resGetString(R.string.appDrawerOpenAnimationReveal)
      case AppDrawerAnimationFade => resGetString(R.string.appDrawerOpenAnimationFade)
    }
    find[ListPreference](AppDrawerAnimation).setSummary(resGetString(R.string.appDrawerOpenAnimationSummary, textValue))
  }

}
