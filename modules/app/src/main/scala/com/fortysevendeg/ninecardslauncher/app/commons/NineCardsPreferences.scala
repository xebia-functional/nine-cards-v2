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

// Animations Preferences

case object SpeedAnimations
  extends NineCardsPreferenceValue[SpeedAnimationValue] {
  override val name: String = speed
  override val default: SpeedAnimationValue = NormalAnimation

  override def readValue(pref: NineCardsPreferencesValue): SpeedAnimationValue =
    SpeedAnimationValue(pref.getString(name, default.value))
}

case object CollectionOpeningAnimations
  extends NineCardsPreferenceValue[CollectionOpeningValue] {
  override val name: String = collectionOpening
  override val default: CollectionOpeningValue = CircleOpeningCollectionAnimation

  override def readValue(pref: NineCardsPreferencesValue): CollectionOpeningValue =
    CollectionOpeningValue(pref.getString(name, default.value))
}

case object WorkspaceAnimations
  extends NineCardsPreferenceValue[WorkspaceAnimationValue] {
  override val name: String = workspaceAnimation
  override val default: WorkspaceAnimationValue = HorizontalSlideWorkspaceAnimation

  override def readValue(pref: NineCardsPreferencesValue): WorkspaceAnimationValue =
    WorkspaceAnimationValue(pref.getString(name, default.value))
}

// App Drawer Preferences

case object AppDrawerLongPressAction
  extends NineCardsPreferenceValue[AppDrawerLongPressActionValue] {
  override val name: String = appDrawerLongPressAction
  override val default: AppDrawerLongPressActionValue = AppDrawerLongPressActionOpenKeyboard

  override def readValue(pref: NineCardsPreferencesValue): AppDrawerLongPressActionValue =
    AppDrawerLongPressActionValue(pref.getString(name, default.value))
}

case object AppDrawerAnimation
  extends NineCardsPreferenceValue[AppDrawerAnimationValue] {
  override val name: String = appDrawerAnimation
  override val default: AppDrawerAnimationValue = AppDrawerAnimationCircle

  override def readValue(pref: NineCardsPreferencesValue): AppDrawerAnimationValue =
    AppDrawerAnimationValue(pref.getString(name, default.value))
}

case object AppDrawerFavoriteContactsFirst
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = appDrawerFavoriteContacts
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

// Theme Preferences

case object Theme
  extends NineCardsPreferenceValue[ThemeValue] {
  override val name: String = theme
  override val default: ThemeValue = ThemeLight

  override def readValue(pref: NineCardsPreferencesValue): ThemeValue =
    ThemeValue(pref.getString(name, default.value))

  def getThemeFile(pref: NineCardsPreferencesValue): String = Theme.readValue(pref) match {
    case ThemeLight => "theme_light"
    case ThemeDark => "theme_dark"
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
  val theme = "theme"

  // AppDrawer Keys
  val appDrawerLongPressAction = "appDrawerLongPressAction"
  val appDrawerAnimation = "appDrawerAnimation"
  val appDrawerFavoriteContacts = "appDrawerFavoriteContacts"

  // Speed
  val speed = "speed"
  val collectionOpening = "collectionOpening"
  val workspaceAnimation = "workspaceAnimation"
}



