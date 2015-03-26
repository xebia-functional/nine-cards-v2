package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.text.TextUtils.TruncateAt
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, ViewGroup}
import android.widget.ImageView.ScaleType
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.ui.components.TintableImageView
import com.fortysevendeg.ninecardslauncher.ui.components.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Tweak}

trait Styles {

  def rootStyle(implicit appContext: AppContext): Tweak[LinearLayout] =
    vMatchParent +
      llVertical +
      vFitsSystemWindows(true)

  val workspaceStyle: Tweak[FrameLayout] =
    llMatchWeightVertical

  def searchContentStyle(implicit appContext: AppContext): Tweak[LinearLayout] = {
    val margin = resGetDimensionPixelSize(R.dimen.padding_default)
    lp[LinearLayout](MATCH_PARENT, resGetDimensionPixelSize(R.dimen.height_search_box)) +
      llHorizontal +
      llLayoutMargin(margin, margin, margin, margin) +
      llGravity(Gravity.CENTER_VERTICAL) +
      vBackground(R.drawable.search) +
      vPaddings(paddingLeftRight = resGetDimensionPixelSize(R.dimen.padding_large), paddingTopBottom = 0)
  }

  def burgerButtonStyle(implicit appContext: AppContext): Tweak[TintableImageView] =
    vWrapContent +
      ivSrc(R.drawable.icon_menu_search) +
      tivDefaultColor(R.color.search_icons_tint) +
      tivPressedColor(R.color.search_press_tint)

  def googleButtonStyle(implicit appContext: AppContext): Tweak[TintableImageView] =
    llWrapWeightHorizontal +
      ivSrc(R.drawable.logo_google) +
      ivScaleType(ScaleType.FIT_START) +
      vPaddings(paddingLeftRight = resGetDimensionPixelSize(R.dimen.padding_large),
        paddingTopBottom = resGetDimensionPixelSize(R.dimen.padding_default)) +
      tivDefaultColor(R.color.search_google_tint) +
      tivPressedColor(R.color.search_press_tint)

  def micButtonStyle(implicit appContext: AppContext): Tweak[TintableImageView] =
    vWrapContent +
      ivSrc(R.drawable.icon_mic_search) +
      tivDefaultColor(R.color.search_icons_tint) +
      tivPressedColor(R.color.search_press_tint)

  def drawerBarContentStyle(implicit appContext: AppContext): Tweak[LinearLayout] = {
    val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
    val paddingBottom = resGetDimensionPixelSize(R.dimen.padding_large)
    vMatchWidth +
      llHorizontal +
      vPadding(paddingDefault, paddingDefault, paddingDefault, paddingBottom) +
      vgClipToPadding(false)
  }

  def appDrawerContentStyle(): Tweak[FrameLayout] = llWrapWeightHorizontal

  def appDrawerStyle(implicit appContext: AppContext): Tweak[TintableImageView] = {
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_pressed)
    vWrapContent +
      flLayoutGravity(Gravity.CENTER) +
      ivSrc(R.drawable.icon_app_drawer) +
      (Lollipop ifSupportedThen {
        vStateListAnimator(R.anim.elevation_transition) +
          vPaddings(elevation) +
          vCircleOutlineProvider(elevation)
      } getOrElse tivPressedColor(R.color.app_drawer_press_tint))
  }
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

  def iconStyle(implicit appContext: AppContext): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_group_collection)
    lp[ViewGroup](size, size) +
      (Lollipop ifSupportedThen vElevation(resGetDimensionPixelSize(R.dimen.elevation_default)) getOrElse Tweak.blank)
  }

  def nameStyle(implicit appContext: AppContext): Tweak[TextView] = {
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