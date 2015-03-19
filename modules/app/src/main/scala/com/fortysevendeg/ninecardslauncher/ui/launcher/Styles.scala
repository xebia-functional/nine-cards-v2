package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.view.Gravity
import android.view.ViewGroup.LayoutParams._
import android.widget.ImageView.ScaleType
import android.widget.{FrameLayout, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
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
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  val workspaceStyle: Tweak[FrameLayout] =
    llMatchWeightVertical

  def searchContentStyle(implicit appContext: AppContext): Tweak[LinearLayout] =
    lp[LinearLayout](MATCH_PARENT, resGetDimensionPixelSize(R.dimen.height_search_box)) +
      llHorizontal +
      llGravity(Gravity.CENTER_VERTICAL) +
      vBackground(R.drawable.search) +
      vPaddings(paddingLeftRight = resGetDimensionPixelSize(R.dimen.padding_large), paddingTopBottom = 0)

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

  val drawerBarContentStyle: Tweak[LinearLayout] =
    vMatchWidth +
      llHorizontal

  def appDrawerStyle(implicit appContext: AppContext): Tweak[TintableImageView] =
    llWrapWeightHorizontal +
      ivSrc(R.drawable.icon_app_drawer) +
      tivPressedColor(R.color.app_drawer_press_tint)

}
