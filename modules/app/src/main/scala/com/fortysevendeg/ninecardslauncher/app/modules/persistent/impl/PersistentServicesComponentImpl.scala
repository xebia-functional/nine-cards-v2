package com.fortysevendeg.ninecardslauncher.app.modules.persistent.impl

import com.fortysevendeg.ninecardslauncher.app.modules.persistent.{PersistentServices, PersistentServicesComponent}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import Themes._
import macroid.ContextWrapper

@Deprecated
trait PersistentServicesComponentImpl
  extends PersistentServicesComponent {

  lazy val persistentServices = new PersistentServicesImpl

  class PersistentServicesImpl
    extends PersistentServices {

    val theme = ThemeLight

    override def getSearchBackgroundColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_background_tint_dark)
      case _ => resGetColor(R.color.search_background_tint_light)
    }

    override def getSearchPressedColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_press_tint_dark)
      case _ => resGetColor(R.color.search_press_tint_light)
    }

    override def getSearchGoogleColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_google_tint_dark)
      case _ => resGetColor(R.color.search_google_tint_light)
    }

    override def getSearchIconsColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.search_icons_tint_dark)
      case _ => resGetColor(R.color.search_icons_tint_light)
    }

    override def getAppDrawerPressedColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.app_drawer_press_tint_dark)
      case _ => resGetColor(R.color.app_drawer_press_tint_light)
    }

    override def getCollectionDetailBackgroundColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.collection_detail_background_dark)
      case _ => resGetColor(R.color.collection_detail_background_light)
    }

    override def getCollectionDetailTextCardColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.collection_detail_text_card_dark)
      case _ => resGetColor(R.color.collection_detail_text_card_light)
    }

    override def getCollectionDetailCardBackgroundColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.collection_detail_background_card_dark)
      case _ => resGetColor(R.color.collection_detail_background_card_light)
    }

    override def getCollectionDetailCardBackgroundPressedColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.collection_detail_background_card_pressed_dark)
      case _ => resGetColor(R.color.collection_detail_background_card_pressed_light)
    }

    override def getCollectionDetailTextTabSelectedColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.collection_detail_tab_color_selected_dark)
      case _ => resGetColor(R.color.collection_detail_tab_color_selected_light)
    }

    override def getCollectionDetailTextTabDefaultColor(implicit c: ContextWrapper): Int = theme match {
      case `ThemeDark` => resGetColor(R.color.collection_detail_tab_color_default_dark)
      case _ => resGetColor(R.color.collection_detail_tab_color_default_light)
    }

    override def getIndexColor(index: Int): Int = index match {
      case 0 => R.color.collection_group_1
      case 1 => R.color.collection_group_2
      case 2 => R.color.collection_group_3
      case 3 => R.color.collection_group_4
      case 4 => R.color.collection_group_5
      case 5 => R.color.collection_group_6
      case 6 => R.color.collection_group_7
      case 7 => R.color.collection_group_8
      case _ => R.color.collection_group_9
    }
  }

}

object Themes {
  val ThemeLight = "light"
  val ThemeDark = "dark"
}