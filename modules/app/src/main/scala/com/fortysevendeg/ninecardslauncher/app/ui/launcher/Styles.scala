package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.support.design.widget.FloatingActionButton
import android.text.TextUtils.TruncateAt
import android.view.{View, Gravity, ViewGroup}
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.FabItemMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{IconTypes, PathMorphDrawable, FabItemMenu, TintableImageView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}
import LauncherTags._

trait Styles {

  def searchContentStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[LinearLayout] =
    vBackgroundColorFilter(theme.get(SearchBackgroundColor))

  def burgerButtonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))

  def googleButtonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivDefaultColor(theme.get(SearchGoogleColor)) +
      tivPressedColor(theme.get(SearchPressedColor))

  def micButtonStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivDefaultColor(theme.get(SearchIconsColor)) +
      tivPressedColor(theme.get(SearchPressedColor))

  def drawerAppStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] = {
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_pressed)
    Lollipop ifSupportedThen {
      vStateListAnimator(R.anim.elevation_transition) +
        vPaddings(elevation) +
        vCircleOutlineProvider(elevation)
    } getOrElse tivPressedColor(theme.get(AppDrawerPressedColor))
  }

  def drawerItemStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] =
    tivPressedColor(theme.get(AppDrawerPressedColor)) +
      vTag(R.id.`type`, LauncherTags.app)

  def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val margin = resGetDimensionPixelSize(R.dimen.margin_pager_collection)
    vWrapContent +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.workspaces_pager)
  }

  def fabButtonCreateCollectionStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    vWrapContent +
      fimBackgroundColor(resGetColor(R.color.collection_fab_button_item_create_new_collection)) +
      fimTitle(resGetString(R.string.create_new_collection)) +
      fimSrc(R.drawable.fab_menu_icon_create_new_collection) +
      vGone +
      vTag(R.id.`type`, fabButton) +
      vTag(R.id.fab_menu_position, "1")

  def fabButtonMyCollectionsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    vWrapContent +
      fimBackgroundColor(resGetColor(R.color.collection_fab_button_item_my_collections)) +
      fimTitle(resGetString(R.string.my_collections)) +
      fimSrc(R.drawable.fab_menu_icon_my_collections) +
      vGone +
      vTag(R.id.`type`, fabButton) +
      vTag(R.id.fab_menu_position, "2")

  def fabButtonPublicCollectionStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    vWrapContent +
      fimBackgroundColor(resGetColor(R.color.collection_fab_button_item_public_collection)) +
      fimTitle(resGetString(R.string.public_collections)) +
      fimSrc(R.drawable.fab_menu_icon_public_collections) +
      vGone +
      vTag(R.id.`type`, fabButton) +
      vTag(R.id.fab_menu_position, "3")

}

trait CollectionsGroupStyle {

  val collectionGridStyle: Tweak[GridLayout] =
    vMatchParent

}

trait CollectionItemStyle {

  val collectionItemStyle: Tweak[LinearLayout] =
    vWrapContent +
      llVertical +
      llGravity(Gravity.CENTER) +
      flLayoutGravity(Gravity.CENTER)

  def iconStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_group_collection)
    lp[ViewGroup](size, size) +
      (Lollipop ifSupportedThen vElevation(resGetDimensionPixelSize(R.dimen.elevation_default)) getOrElse Tweak.blank)
  }

  def nameStyle(implicit context: ContextWrapper): Tweak[TextView] = {
    val displacement = resGetDimensionPixelSize(R.dimen.shadow_displacement_default)
    val radius = resGetDimensionPixelSize(R.dimen.shadow_radius_default)
    vWrapContent +
      vPadding(paddingTop = resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvColorResource(R.color.collection_group_name) +
      tvSizeResource(R.dimen.text_default) +
      tvLines(2) +
      tvEllipsize(TruncateAt.END) +
      tvGravity(Gravity.CENTER_HORIZONTAL) +
      tvShadowLayer(radius, displacement, displacement, resGetColor(R.color.shadow_default))
  }

}

object LauncherTags {
  val app = "app"
  val fabButton = "fab_button"
  val open = "open"
  val close = "close"
}