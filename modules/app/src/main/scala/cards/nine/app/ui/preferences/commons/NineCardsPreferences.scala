package cards.nine.app.ui.preferences.commons

import NineCardsPreferencesValue._
import android.content.{Context, SharedPreferences}
import android.preference.PreferenceManager
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

sealed trait NineCardsPreferences {
  val name: String
}

case object LookFeelPreferences extends NineCardsPreferences {
  override val name: String = "lookFeelKey"
}

case object MomentsPreferences extends NineCardsPreferences {
  override val name: String = "momentKey"
}

case object AppDrawerPreferences extends NineCardsPreferences {
  override val name: String = "appDrawerKey"
}

case object SizesPreferences extends NineCardsPreferences {
  override val name: String = "sizesKey"
}

case object AnimationsPreferences extends NineCardsPreferences {
  override val name: String = "animationsKey"
}

case object DeveloperPreferences extends NineCardsPreferences {
  override val name: String = "developerKey"
}

case object WizardInlinePreferences extends NineCardsPreferences {
  override val name: String = "wizardInlineKey"
}

case object AboutPreferences extends NineCardsPreferences {
  override val name: String = "aboutKey"
}

case object HelpPreferences extends NineCardsPreferences {
  override val name: String = "helpKey"
}

sealed trait NineCardsPreferenceValue[T]
  extends NineCardsPreferences {

  val name: String
  val default: T

  def readValue(implicit contextWrapper: ContextWrapper): T =
    readValueWith(contextWrapper.application)

  def readValueWith(context: Context): T
}

// Moments Preferences

case object ShowClockMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "showClockMoment"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object ShowMicSearchMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "showMicSearchMoment"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object ShowWeatherMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "showWeatherMoment"
  override val default: Boolean = true

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

// Animations Preferences

case object SpeedAnimations
  extends NineCardsPreferenceValue[SpeedAnimationValue] {
  override val name: String = "speed"
  override val default: SpeedAnimationValue = NormalAnimation

  override def readValueWith(context: Context): SpeedAnimationValue =
    SpeedAnimationValue(getString(context, name, default.value))

  def getDuration(implicit contextWrapper: ContextWrapper): Int = {
    resGetInteger(readValue match {
      case NormalAnimation => R.integer.anim_duration_normal
      case SlowAnimation => R.integer.anim_duration_slow
      case FastAnimation => R.integer.anim_duration_fast
    })
  }
}

case object CollectionOpeningAnimations
  extends NineCardsPreferenceValue[CollectionOpeningValue] {
  override val name: String = "collectionOpening"
  override val default: CollectionOpeningValue = CircleOpeningCollectionAnimation

  override def readValueWith(context: Context): CollectionOpeningValue =
    CollectionOpeningValue(getString(context, name, default.value))
}

case object WorkspaceAnimations
  extends NineCardsPreferenceValue[WorkspaceAnimationValue] {
  override val name: String = "workspaceAnimation"
  override val default: WorkspaceAnimationValue = HorizontalSlideWorkspaceAnimation

  override def readValueWith(context: Context): WorkspaceAnimationValue =
    WorkspaceAnimationValue(getString(context, name, default.value))
}

case object WallpaperAnimation
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "wallpaperAnimation"
  override val default: Boolean = true

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

// App Drawer Preferences

case object AppDrawerLongPressAction
  extends NineCardsPreferenceValue[AppDrawerLongPressActionValue] {
  override val name: String = "appDrawerLongPressAction"
  override val default: AppDrawerLongPressActionValue = AppDrawerLongPressActionOpenKeyboard

  override def readValueWith(context: Context): AppDrawerLongPressActionValue =
    AppDrawerLongPressActionValue(getString(context, name, default.value))
}

case object AppDrawerAnimation
  extends NineCardsPreferenceValue[AppDrawerAnimationValue] {
  override val name: String = "appDrawerAnimation"
  override val default: AppDrawerAnimationValue = AppDrawerAnimationCircle

  override def readValueWith(context: Context): AppDrawerAnimationValue =
    AppDrawerAnimationValue(getString(context, name, default.value))
}

case object AppDrawerFavoriteContactsFirst
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "appDrawerFavoriteContacts"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object AppDrawerSelectItemsInScroller
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "appDrawerSelectItemsInScroller"
  override val default: Boolean = true

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

// Look & Feel Preferences

case object Theme
  extends NineCardsPreferenceValue[ThemeValue] {
  override val name: String = "theme"
  override val default: ThemeValue = ThemeLight

  override def readValueWith(context: Context): ThemeValue =
    ThemeValue(getString(context, name, default.value))

  def getThemeFile(implicit contextWrapper: ContextWrapper): String = Theme.readValue match {
    case ThemeLight => "theme_light"
    case ThemeDark => "theme_dark"
  }
}

case object GoogleLogo
  extends NineCardsPreferenceValue[GoogleLogoValue] {
  override val name: String = "googleLogo"
  override val default: GoogleLogoValue = GoogleLogoTheme

  override def readValueWith(context: Context): GoogleLogoValue =
    GoogleLogoValue(getString(context, name, default.value))
}

case object FontSize
  extends NineCardsPreferenceValue[FontSizeValue] {
  override val name: String = "fontsSize"
  override val default: FontSizeValue = FontSizeMedium

  override def readValueWith(context: Context): FontSizeValue =
    FontSizeValue(getString(context, name, default.value))

  def getSizeResource(implicit contextWrapper: ContextWrapper): Int = {
    readValue match {
      case FontSizeSmall => R.dimen.text_medium
      case FontSizeMedium => R.dimen.text_default
      case FontSizeLarge => R.dimen.text_large
    }
  }

  def getTitleSizeResource(implicit contextWrapper: ContextWrapper): Int = {
    readValue match {
      case FontSizeSmall => R.dimen.text_large
      case FontSizeMedium => R.dimen.text_xlarge
      case FontSizeLarge => R.dimen.text_xxlarge
    }
  }

  def getContactSizeResource(implicit contextWrapper: ContextWrapper): Int = {
    readValue match {
      case FontSizeSmall => R.dimen.text_default
      case FontSizeMedium => R.dimen.text_large
      case FontSizeLarge => R.dimen.text_xlarge
    }
  }

}

case object IconsSize
  extends NineCardsPreferenceValue[IconsSizeValue] {
  override val name: String = "iconsSize"
  override val default: IconsSizeValue = IconsSizeMedium

  override def readValueWith(context: Context): IconsSizeValue =
    IconsSizeValue(getString(context, name, default.value))

  def getIconApp(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue match {
      case IconsSizeSmall => R.dimen.size_icon_app_small
      case IconsSizeMedium => R.dimen.size_icon_app_medium
      case IconsSizeLarge => R.dimen.size_icon_app_large
    })
  }

  def getIconCollection(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue match {
      case IconsSizeSmall => R.dimen.size_group_collection_small
      case IconsSizeMedium => R.dimen.size_group_collection_medium
      case IconsSizeLarge => R.dimen.size_group_collection_large
    })
  }

}

case object CardPadding
  extends NineCardsPreferenceValue[IconsSizeValue] {
  override val name: String = "cardPadding"
  override val default: IconsSizeValue = IconsSizeMedium

  override def readValueWith(context: Context): IconsSizeValue =
    IconsSizeValue(getString(context, name, default.value))

  def getPadding(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue match {
      case IconsSizeSmall => R.dimen.card_padding_small
      case IconsSizeMedium => R.dimen.card_padding_medium
      case IconsSizeLarge => R.dimen.card_padding_large
    })
  }
}

// Developer Preferences

case object IsDeveloper
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "isDeveloper"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)

  def convertToDeveloper(implicit contextWrapper: ContextWrapper): Unit =
    setBoolean(contextWrapper.application, name, value = true)
}

case object AppsCategorized
  extends NineCardsPreferenceValue[String] {
  override val name: String = "appsCategorized"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object AndroidToken
  extends NineCardsPreferenceValue[String] {
  override val name: String = "androidToken"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object DeviceCloudId
  extends NineCardsPreferenceValue[String] {
  override val name: String = "deviceCloudId"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object CurrentDensity
  extends NineCardsPreferenceValue[String] {
  override val name: String = "currentDensity"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object ProbablyActivity
  extends NineCardsPreferenceValue[String] {
  override val name: String = "probablyActivity"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object Headphones
  extends NineCardsPreferenceValue[String] {
  override val name: String = "headphones"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object Location
  extends NineCardsPreferenceValue[String] {
  override val name: String = "location"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object Weather
  extends NineCardsPreferenceValue[String] {
  override val name: String = "weather"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object ClearCacheImages
  extends NineCardsPreferenceValue[String] {
  override val name: String = "clearCacheImages"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object ShowPositionInCards
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "showPositionInCards"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object ShowPrintInfoOptionInAccounts
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "showPrintInfoOptionInAccounts"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object OverrideBackendV2Url
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "overrideBackendV2Url"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object BackendV2Url
  extends NineCardsPreferenceValue[String] {
  override val name: String = "backendV2Url"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

case object V1EmptyDeviceWizard
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "v1EmptyDeviceWizard"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object GoogleDriveEmptyDeviceWizard
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "googleDriveEmptyDeviceWizard"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object IsStethoActive
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = "isStethoActive"
  override val default: Boolean = false

  override def readValueWith(context: Context): Boolean = getBoolean(context, name, default)
}

case object RestartApplication
  extends NineCardsPreferenceValue[String] {
  override val name: String = "restartApplication"
  override val default: String = ""

  override def readValueWith(context: Context): String = getString(context, name, default)
}

// Commons

object NineCardsPreferencesValue {

  private[this] def get[T](context: Context, f: (SharedPreferences) => T) =
    f(PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext))

  def getInt(context: Context, name: String, defaultValue: Int): Int =
    get(context, _.getInt(name, defaultValue))

  def setInt(context: Context, name: String, value: Int): Unit =
    get(context, _.edit().putInt(name, value).apply())

  def getString(context: Context, name: String, defaultValue: String): String =
    get(context, _.getString(name, defaultValue))

  def setString(context: Context, name: String, value: String): Unit =
    get(context, _.edit().putString(name, value).apply())

  def getBoolean(context: Context, name: String, defaultValue: Boolean): Boolean =
    get(context, _.getBoolean(name, defaultValue))

  def setBoolean(context: Context, name: String, value: Boolean): Unit =
    get(context, _.edit().putBoolean(name, value).apply())

}




