package com.fortysevendeg.ninecardslauncher.app.commons

import PreferencesKeys._
import PreferencesValuesKeys._
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

sealed trait NineCardsPreferences {
  val name: String
}

case object LookFeelPreferences extends NineCardsPreferences {
  override val name: String = lookFeelKey
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

  def getDuration(implicit contextWrapper: ContextWrapper): Int = {
    resGetInteger(readValue(new NineCardsPreferencesValue) match {
      case NormalAnimation => R.integer.anim_duration_normal
      case SlowAnimation => R.integer.anim_duration_slow
      case FastAnimation => R.integer.anim_duration_fast
    })
  }
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

// Look & Feel Preferences

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

case object GoogleLogo
  extends NineCardsPreferenceValue[GoogleLogoValue] {
  override val name: String = googleLogo
  override val default: GoogleLogoValue = GoogleLogoTheme

  override def readValue(pref: NineCardsPreferencesValue): GoogleLogoValue =
    GoogleLogoValue(pref.getString(name, default.value))
}

case object FontSize
  extends NineCardsPreferenceValue[FontSizeValue] {
  override val name: String = fontsSize
  override val default: FontSizeValue = FontSizeMedium

  override def readValue(pref: NineCardsPreferencesValue): FontSizeValue =
    FontSizeValue(pref.getString(name, default.value))

  def getSize(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue(new NineCardsPreferencesValue) match {
      case FontSizeSmall => R.dimen.text_medium
      case FontSizeMedium => R.dimen.text_default
      case FontSizeLarge => R.dimen.text_large
    })
  }
}

case object IconsSize
  extends NineCardsPreferenceValue[IconsSizeValue] {
  override val name: String = iconsSize
  override val default: IconsSizeValue = IconsSizeMedium

  override def readValue(pref: NineCardsPreferencesValue): IconsSizeValue =
    IconsSizeValue(pref.getString(name, default.value))

  def getIconApp(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue(new NineCardsPreferencesValue) match {
      case IconsSizeSmall => R.dimen.size_icon_app_small
      case IconsSizeMedium => R.dimen.size_icon_app_medium
      case IconsSizeLarge => R.dimen.size_icon_app_large
    })
  }

  def getIconCollection(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue(new NineCardsPreferencesValue) match {
      case IconsSizeSmall => R.dimen.size_group_collection_small
      case IconsSizeMedium => R.dimen.size_group_collection_medium
      case IconsSizeLarge => R.dimen.size_group_collection_large
    })
  }

}

case object CardPadding
  extends NineCardsPreferenceValue[IconsSizeValue] {
  override val name: String = cardPadding
  override val default: IconsSizeValue = IconsSizeMedium

  override def readValue(pref: NineCardsPreferencesValue): IconsSizeValue =
    IconsSizeValue(pref.getString(name, default.value))
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
  val lookFeelKey = "lookFeelKey"
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

  // Look and Feel Keys
  val theme = "theme"
  val googleLogo = "googleLogo"
  val fontsSize = "fontsSize"
  val iconsSize = "iconsSize"
  val cardPadding = "cardPadding"

  // AppDrawer Keys
  val appDrawerLongPressAction = "appDrawerLongPressAction"
  val appDrawerAnimation = "appDrawerAnimation"
  val appDrawerFavoriteContacts = "appDrawerFavoriteContacts"

  // Speed
  val speed = "speed"
  val collectionOpening = "collectionOpening"
  val workspaceAnimation = "workspaceAnimation"
}



