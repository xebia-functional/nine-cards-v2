package com.fortysevendeg.ninecardslauncher.modules.theme

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

sealed trait Theme {

  val searchBackgroundColorResource: Int
  val searchIconsColorResource: Int
  val searchGoogleColorResource: Int
  val searchPressedColorResource: Int
  val appDrawerPressedColorResource: Int
  val collectionDetailBackgroundColorResource: Int
  val collectionDetailCardBackgroundColorResource: Int
  val collectionDetailCardBackgroundPressedColorResource: Int
  val collectionDetailTextCardColorResource: Int
  val collectionDetailTextTabSelectedColorResource: Int
  val collectionDetailTextTabDefaultColorResource: Int

  def searchBackgroundColor(implicit context: ContextWrapper): Int =
    resGetColor(searchBackgroundColorResource)

  def searchIconsColor(implicit context: ContextWrapper): Int =
    resGetColor(searchIconsColorResource)

  def searchGoogleColor(implicit context: ContextWrapper): Int =
    resGetColor(searchGoogleColorResource)

  def searchPressedColor(implicit context: ContextWrapper): Int =
    resGetColor(searchPressedColorResource)
  def appDrawerPressedColor(implicit context: ContextWrapper): Int =
    resGetColor(appDrawerPressedColorResource)

  def collectionDetailBackgroundColor(implicit context: ContextWrapper): Int =
    resGetColor(collectionDetailBackgroundColorResource)

  def collectionDetailCardBackgroundColor(implicit context: ContextWrapper): Int =
    resGetColor(collectionDetailCardBackgroundColorResource)

  def collectionDetailCardBackgroundPressedColor(implicit context: ContextWrapper): Int =
    resGetColor(collectionDetailCardBackgroundPressedColorResource)

  def collectionDetailTextCardColor(implicit context: ContextWrapper): Int =
    resGetColor(collectionDetailTextCardColorResource)

  def collectionDetailTextTabSelectedColor(implicit context: ContextWrapper): Int =
    resGetColor(collectionDetailTextTabSelectedColorResource)

  def collectionDetailTextTabDefaultColor(implicit context: ContextWrapper): Int =
    resGetColor(collectionDetailTextTabDefaultColorResource)

}

case object ThemeDark extends Theme {

  override val searchBackgroundColorResource: Int = R.color.search_background_tint_dark

  override val searchPressedColorResource: Int = R.color.search_press_tint_dark

  override val searchGoogleColorResource: Int = R.color.search_google_tint_dark

  override val searchIconsColorResource: Int = R.color.search_icons_tint_dark

  override val appDrawerPressedColorResource: Int = R.color.app_drawer_press_tint_dark

  override val collectionDetailBackgroundColorResource: Int = R.color.collection_detail_background_dark

  override val collectionDetailTextCardColorResource: Int = R.color.collection_detail_text_card_dark

  override val collectionDetailCardBackgroundColorResource: Int = R.color.collection_detail_background_card_dark

  override val collectionDetailCardBackgroundPressedColorResource: Int = R.color.collection_detail_background_card_pressed_dark

  override val collectionDetailTextTabSelectedColorResource: Int = R.color.collection_detail_tab_color_selected_dark

  override val collectionDetailTextTabDefaultColorResource: Int = R.color.collection_detail_tab_color_default_dark

}

case object ThemeLight extends Theme {

  override val searchBackgroundColorResource: Int = R.color.search_background_tint_light

  override val searchPressedColorResource: Int = R.color.search_press_tint_light

  override val searchGoogleColorResource: Int = R.color.search_google_tint_light

  override val searchIconsColorResource: Int = R.color.search_icons_tint_light

  override val appDrawerPressedColorResource: Int = R.color.app_drawer_press_tint_light

  override val collectionDetailBackgroundColorResource: Int = R.color.collection_detail_background_light

  override val collectionDetailTextCardColorResource: Int = R.color.collection_detail_text_card_light

  override val collectionDetailCardBackgroundColorResource: Int = R.color.collection_detail_background_card_light

  override val collectionDetailCardBackgroundPressedColorResource: Int = R.color.collection_detail_background_card_pressed_light

  override val collectionDetailTextTabSelectedColorResource: Int = R.color.collection_detail_tab_color_selected_light

  override val collectionDetailTextTabDefaultColorResource: Int = R.color.collection_detail_tab_color_default_light

}
