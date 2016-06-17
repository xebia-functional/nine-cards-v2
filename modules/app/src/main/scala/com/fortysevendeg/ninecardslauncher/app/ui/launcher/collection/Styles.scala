package com.fortysevendeg.ninecardslauncher.app.ui.launcher.collection

import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.WorkSpaceItemMenu
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceItemMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Tweak}

trait Styles {

  def searchContentStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[LinearLayout] =
    vBackgroundBoxWorkspace(theme.get(SearchBackgroundColor))

  def burgerButtonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))

  def googleButtonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivDefaultColor(theme.get(SearchGoogleColor)) +
      tivPressedColor(theme.get(SearchPressedColor))

  def micButtonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))

  def menuAvatarStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vCircleOutlineProvider()
    } getOrElse Tweak.blank

  def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val margin = resGetDimensionPixelSize(R.dimen.margin_pager_collection)
    vWrapContent +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.workspaces_pager)
  }

  def workspaceButtonWallpaperStyle(implicit context: ContextWrapper): Tweak[WorkSpaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_wallpaper),
      R.drawable.fab_menu_icon_create_new_collection,
      R.string.wallpaperTitle)

  def workspaceButtonWidgetsStyle(implicit context: ContextWrapper): Tweak[WorkSpaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_widgets),
      R.drawable.fab_menu_icon_create_new_collection,
      R.string.widgetsTitle)

  def workspaceButtonSettingsStyle(implicit context: ContextWrapper): Tweak[WorkSpaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_settings),
      R.drawable.fab_menu_icon_create_new_collection,
      R.string.nineCardsSettingsTitle)

  def workspaceButtonCreateCollectionStyle(implicit context: ContextWrapper): Tweak[WorkSpaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_create_new_collection),
      R.drawable.fab_menu_icon_create_new_collection,
      R.string.createNewCollection)

  def workspaceButtonMyCollectionsStyle(implicit context: ContextWrapper): Tweak[WorkSpaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_my_collections),
      R.drawable.fab_menu_icon_my_collections,
      R.string.myCollections)

  def workspaceButtonPublicCollectionStyle(implicit context: ContextWrapper): Tweak[WorkSpaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_public_collection),
      R.drawable.fab_menu_icon_public_collections,
      R.string.publicCollections)

}
