package com.fortysevendeg.ninecardslauncher.ui.collections

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable._
import android.support.v4.view.ViewPager
import android.support.v7.widget.{CardView, RecyclerView, Toolbar}
import android.text.TextUtils.TruncateAt
import android.view.{View, Gravity, ViewGroup}
import android.widget.ImageView.ScaleType
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.di.Module
import com.fortysevendeg.ninecardslauncher.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayout
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

trait Styles extends Module {

  def theme(implicit context: ContextWrapper) = persistentServices.theme

  def rootStyle(implicit context: ContextWrapper): Tweak[FrameLayout] =
    vMatchParent +
      vFitsSystemWindows(true) +
      vBackgroundColor(theme.collectionDetailBackgroundColor)

  def toolbarStyle(implicit context: ContextWrapper): Tweak[Toolbar] =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.height_tootlbar_collection_details)) +
      elevation

  def iconContentStyle(implicit context: ContextWrapper): Tweak[FrameLayout] = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_collection_detail)
    lp[ViewGroup](size, size) +
      vBackground(R.drawable.background_icon_collection_detail) +
      Tweak[FrameLayout] {
        view ⇒
          val params = new FrameLayout.LayoutParams(view.getLayoutParams)
          params.setMargins(0, resGetDimensionPixelSize(R.dimen.padding_default), 0, 0)
          params.gravity = Gravity.CENTER_HORIZONTAL
          view.setLayoutParams(params)
      } +
      vPivotX(resGetDimensionPixelSize(R.dimen.pivot_x_icon_collection_detail)) +
      vPivotY(0) +
      elevation

  }

  def iconStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    vMatchParent +
      ivScaleType(ScaleType.CENTER_INSIDE)


  def tabsStyle(implicit context: ContextWrapper): Tweak[SlidingTabLayout] =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.height_tabs_collection_details)) +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.margin_top_tabs_collection_details)) +
      stlDefaultTextColor(theme.collectionDetailTextTabDefaultColor) +
      stlSelectedTextColor(theme.collectionDetailTextTabSelectedColor) +
      elevation

  def viewPagerStyle(implicit context: ContextWrapper): Tweak[ViewPager] =
    vMatchParent +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.margin_top_pagers_collection_details)) +
      elevation


  private def elevation(implicit context: ContextWrapper) = Lollipop.ifSupportedThen {
    vElevation(resGetDimensionPixelSize(R.dimen.elevation_toolbar))
  }.getOrElse(Tweak.blank)

}

trait CollectionFragmentStyles {

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] = {
    val paddingTop = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    vMatchParent +
      vPadding(padding, paddingTop, padding, padding) +
      vgClipToPadding(false) +
      vOverScrollMode(View.OVER_SCROLL_NEVER)
  }

}

trait CollectionAdapterStyles extends Module {

  def theme(implicit context: ContextWrapper) = persistentServices.theme

  def rootStyle(heightCard: Int)(implicit context: ContextWrapper): Tweak[CardView] =
    vContentSizeMatchWidth(heightCard) +
      cvCardBackgroundColor(theme.collectionDetailCardBackgroundColor) +
      flForeground(createBackground)

  private def createBackground(implicit context: ContextWrapper): Drawable = {
    val color = theme.collectionDetailCardBackgroundPressedColor
    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color)),
        null,
        new ColorDrawable(setAlpha(Color.BLACK, 0.1f)))
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), new ColorDrawable(setAlpha(color, 0.1f)))
      states.addState(Array.emptyIntArray, new ColorDrawable(Color.TRANSPARENT))
      states
    }
  }

  def contentStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
    vMatchParent +
      llVertical +
      llGravity(Gravity.CENTER) +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def iconStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_card)
    lp[ViewGroup](size, size)
  }

  def nameStyle(implicit context: ContextWrapper): Tweak[TextView] =
    vMatchWidth +
      vPadding(paddingTop = resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvColor(theme.collectionDetailTextCardColor) +
      tvLines(2) +
      tvSizeResource(R.dimen.text_default) +
      tvEllipsize(TruncateAt.END)

}
