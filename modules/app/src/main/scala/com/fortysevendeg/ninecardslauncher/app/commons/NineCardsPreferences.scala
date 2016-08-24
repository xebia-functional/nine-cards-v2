package com.fortysevendeg.ninecardslauncher.app.commons

import PreferencesKeys._
import PreferencesValuesKeys._
import android.content.SharedPreferences
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

// Moments Preferences

case object ShowClockMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = showClockMoment
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

// App Drawer Preferences

case object AppDrawerLongPressAction
  extends NineCardsPreferenceValue[AppDrawerLongPressActionValue] {
  override val name: String = appDrawerLongPressAction
  override val default: AppDrawerLongPressActionValue = AppDrawerLongPressActionOpenKeyboard

  override def readValue(pref: NineCardsPreferencesValue): AppDrawerLongPressActionValue =
    AppDrawerLongPressActionValue(pref.getString(name, default.name))
}

case object AppDrawerAnimation
  extends NineCardsPreferenceValue[AppDrawerAnimationValue] {
  override val name: String = appDrawerAnimation
  override val default: AppDrawerAnimationValue = AppDrawerAnimationCircle

  override def readValue(pref: NineCardsPreferencesValue): AppDrawerAnimationValue =
    AppDrawerAnimationValue(pref.getString(name, default.name))
}

case object AppDrawerFavoriteContactsFirst
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = appDrawerFavoriteContacts
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

// Theme Preferences

case object ThemeFile
  extends NineCardsPreferenceValue[String] {

  private[this] val themeDark = "theme_dark"
  private[this] val themeLight = "theme_light"
  private[this] val defaultValue = "1"

  override val name: String = themeFile
  override val default: String = themeLight

  private[this] def parseThemeJson(prefValue: Int): Option[String] = prefValue match {
    case 0 => Some(themeDark)
    case 1 => Some(themeLight)
    case _ => None
  }

  override def readValue(pref: NineCardsPreferencesValue): String =
    parseThemeJson(pref.getString(name, defaultValue).toInt) match {
      case Some(s) => s
      case _ => default
    }
}
// Commons

class NineCardsPreferencesValue(implicit contextWrapper: ContextWrapper) {

  private[this] def get[T](f: (SharedPreferences) => T) =
    f(PreferenceManager.getDefaultSharedPreferences(contextWrapper.application))

  def getInt(name: String, defaultValue: Int): Int = get(_.getInt(name, defaultValue))

  def getString(name: String, defaultValue: String): String = get(_.getString(name, defaultValue))

  def getBoolean(name: String, defaultValue: Boolean): Boolean = get(_.getBoolean(name, defaultValue))

}

// This values should be the same that the keys used in XML preferences_headers
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

}

// Values for all preference keys used for values
object PreferencesValuesKeys {
  // Moment keys
  val showClockMoment = "showClockMoment"

  // Theme Keys
  val themeFile = "theme"

  // AppDrawer Keys
  val appDrawerLongPressAction = "appDrawerLongPressAction"
  val appDrawerAnimation = "appDrawerAnimation"
  val appDrawerFavoriteContacts = "appDrawerFavoriteContacts"
}



