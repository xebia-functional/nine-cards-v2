package com.fortysevendeg.ninecardslauncher.app.commons

import PreferencesKeys._

sealed trait NineCardsPreferences {
  val name: String
}

case object DefaultLauncherPreferences extends NineCardsPreferences {
  override val name: String = defaultLauncherKey
}

case object ThemesPreferences extends NineCardsPreferences {
  override val name: String = themesKey
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

case object AboutPreferences extends NineCardsPreferences {
  override val name: String = aboutKey
}

case object HelpPreferences extends NineCardsPreferences {
  override val name: String = helpKey
}

object NineCardsPreferences {

  val preferences = Seq(ThemesPreferences, AppDrawerPreferences, SizesPreferences, AnimationsPreferences, NewAppPreferences, AboutPreferences, HelpPreferences)

  def apply(name: String): NineCardsPreferences = preferences find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}

// This values should be the same that the keys used in preferences_headers.xml
object PreferencesKeys {
  val defaultLauncherKey = "defaultLauncherKey"
  val themesKey = "themesKey"
  val appDrawerKey = "appDrawerKey"
  val sizesKey = "sizesKey"
  val animationsKey = "animationsKey"
  val newAppKey = "newAppKey"
  val aboutKey = "aboutKey"
  val helpKey = "helpKey"
}



