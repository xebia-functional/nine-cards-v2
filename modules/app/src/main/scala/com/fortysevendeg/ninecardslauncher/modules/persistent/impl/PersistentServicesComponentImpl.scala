package com.fortysevendeg.ninecardslauncher.modules.persistent.impl

import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.modules.persistent.{PersistentServices, PersistentServicesComponent}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import Themes._

trait PersistentServicesComponentImpl
  extends PersistentServicesComponent {

  self : AppContextProvider =>

  lazy val persistentServices = new PersistentServicesImpl

  class PersistentServicesImpl
    extends PersistentServices {

    val theme = ThemeLight

    override def getSearchBackgroundColor(): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_background_tint_dark)
      case _ => resGetColor(R.color.search_background_tint_light)
    }

    override def getSearchPressedColor(): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_press_tint_dark)
      case _ => resGetColor(R.color.search_press_tint_light)
    }

    override def getSearchGoogleColor(): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_google_tint_dark)
      case _ => resGetColor(R.color.search_google_tint_light)
    }

    override def getSearchIconsColor(): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_icons_tint_dark)
      case _ => resGetColor(R.color.search_icons_tint_light)
    }

    override def getAppDrawerPressedColor(): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.app_drawer_press_tint_dark)
      case _ => resGetColor(R.color.app_drawer_press_tint_light)
    }

  }

}

object Themes {
  val ThemeLight = "light"
  val ThemeDark = "dark"
}