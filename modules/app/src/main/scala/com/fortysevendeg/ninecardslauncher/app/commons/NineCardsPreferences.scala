package com.fortysevendeg.ninecardslauncher.app.commons

import PreferencesKeys._
import PreferencesStates._
import android.content.Context
import android.preference.PreferenceManager
import macroid.ContextWrapper

sealed trait NineCardsPreferences {
  val name: String
}

case object ThemesPreferences extends NineCardsPreferences {
  override val name: String = themesKey
}

case object MomentsPreferences extends NineCardsPreferences {
  override val name: String = momentKey
}

case object AppDrawerPreferences extends NineCardsPreferences {
  override val name: String = appDrawerKey
}

case object SizesPreferences extends NineCardsPreferences {
  override val name: String = sizesKey
}

case object AnimationsPreferences extends NineCardsPreferences {
  override val name: String = animationsKey
}

case object NewAppPreferences extends NineCardsPreferences {
  override val name: String = newAppKey
}

case object AppInfoPreferences extends NineCardsPreferences {
  override val name: String = appInfoKey
}

case object AboutPreferences extends NineCardsPreferences {
  override val name: String = aboutKey
}

case object HelpPreferences extends NineCardsPreferences {
  override val name: String = helpKey
}

sealed trait NineCardsPreferenceValue[T]
  extends NineCardsPreferences {
  val name: String
  val default: T
}

case object ShowClockMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = showClockMoment
  override val default: Boolean = true
}

class NineCardsPreferencesValue(implicit contextWrapper: ContextWrapper) {

  def getInt(pref: NineCardsPreferenceValue[Int]): Int =
    PreferenceManager.getDefaultSharedPreferences(contextWrapper.application).getString(pref.name, pref.default.toString).toInt

  def getString(pref: NineCardsPreferenceValue[String]): String =
    PreferenceManager.getDefaultSharedPreferences(contextWrapper.application).getString(pref.name, pref.default)

  def getBoolean(pref: NineCardsPreferenceValue[Boolean]): Boolean =
    PreferenceManager.getDefaultSharedPreferences(contextWrapper.application).getBoolean(pref.name, pref.default)

}

class NineCardsPreferencesStatus(implicit contextWrapper: ContextWrapper) {

  private[this] val defaultState = false

  private[this] val preferences = contextWrapper.application.getSharedPreferences(namePreferencesState, Context.MODE_PRIVATE)

  def setMoments(state: Boolean): Unit = {
    val editor = preferences.edit()
    editor.putBoolean(momentsState, state)
    editor.apply()
  }

  def momentsWasChanged: Boolean = preferences.getBoolean(momentsState, defaultState)
}

// This values should be the same that the keys used in XML Preferences
object PreferencesKeys {
  val defaultLauncherKey = "defaultLauncherKey"
  val themesKey = "themesKey"
  val momentKey = "momentKey"
  val appDrawerKey = "appDrawerKey"
  val sizesKey = "sizesKey"
  val animationsKey = "animationsKey"
  val newAppKey = "newAppKey"
  val aboutKey = "aboutKey"
  val helpKey = "helpKey"
  val appInfoKey = "appInfoKey"

  val showClockMoment = "showClockMoment"
}

object PreferencesStates {

  val namePreferencesState = "NineCardsPreferencesState"

  val momentsState = "NineCardsPreferencesState"

}



