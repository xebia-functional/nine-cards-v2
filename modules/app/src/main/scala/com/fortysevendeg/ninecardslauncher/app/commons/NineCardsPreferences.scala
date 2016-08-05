package com.fortysevendeg.ninecardslauncher.app.commons

import PreferencesKeys._
import PreferencesStates._
import android.content.{Context, SharedPreferences}
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
  def readValue(pref: NineCardsPreferencesValue): T
}

case object ShowClockMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = showClockMoment
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

case object ThemeFile
  extends NineCardsPreferenceValue[String] {
  override val name: String = themeFile
  override val default: String = "theme_light"

  private[this] val themeDark = "theme_dark"
  private[this] val themeLight = "theme_light"

  private[this] def parseThemeJson(prefValue: Int): String = prefValue match {
    case 0 => themeDark
    case 1 => themeLight
    case 2 => themeLight
    case 3 => themeLight
    case _ => throw new IllegalArgumentException(s"Illegal value $prefValue for $themesKey")
  }

  override def readValue(pref: NineCardsPreferencesValue): String = parseThemeJson(pref.getString(name, "0").toInt)
}

class NineCardsPreferencesValue(implicit contextWrapper: ContextWrapper) {

  private[this] def get[T](f: (SharedPreferences) => T) =
    f(PreferenceManager.getDefaultSharedPreferences(contextWrapper.application))

  def getInt(name: String, defaultValue: Int): Int = get(_.getInt(name, defaultValue))

  def getString(name: String, defaultValue: String): String = get(_.getString(name, defaultValue))

  def getBoolean(name: String, defaultValue: Boolean): Boolean = get(_.getBoolean(name, defaultValue))

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
  val themeFile = "theme"
}

object PreferencesStates {

  val namePreferencesState = "NineCardsPreferencesState"

  val momentsState = "NineCardsPreferencesState"

}



