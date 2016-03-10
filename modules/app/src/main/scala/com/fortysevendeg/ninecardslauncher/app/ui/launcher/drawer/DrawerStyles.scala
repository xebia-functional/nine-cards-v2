package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import android.widget.{FrameLayout, ImageView}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

trait DrawerStyles {

  def contentStyle = vGone

  def scrollableStyle(color: Int)(implicit context: ContextWrapper, theme: NineCardsTheme) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    vBackgroundBoxWorkspace(color = theme.get(SearchBackgroundColor), horizontalPadding = padding) +
      fslColor(color)
  }

  def appDrawerMainStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] = {
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_pressed)
    Lollipop ifSupportedThen {
      vStateListAnimator(R.anim.elevation_transition) +
        vPaddings(elevation) +
        vCircleOutlineProvider(elevation)
    } getOrElse tivPressedColor(theme.get(AppDrawerPressedColor))
  }

  def recyclerStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[RecyclerView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    rvFixedSize +
      vPaddings(padding) +
      vgClipToPadding(false) +
      vOverScrollMode(View.OVER_SCROLL_NEVER)
  }

  def paginationDrawerItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val margin = resGetDimensionPixelSize(R.dimen.margin_pager_drawer)
    val size = resGetDimensionPixelSize(R.dimen.drawer_size_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.drawer_pager)
  }

  def screenAnimationStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[FrameLayout] =
    vMatchParent +
      vGone

}
