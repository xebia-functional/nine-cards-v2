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
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.ninecardslauncher.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayout
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{AppContext, Tweak}

trait Styles {

  self: PersistentServicesComponent =>

  def rootStyle(implicit appContext: AppContext): Tweak[FrameLayout] =
    vMatchParent +
      vFitsSystemWindows(true) +
      vBackgroundColor(persistentServices.getCollectionDetailBackgroundColor)

  def toolbarStyle(implicit appContext: AppContext): Tweak[Toolbar] =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.height_tootlbar_collection_details)) +
      elevation

  def iconContentStyle(implicit appContext: AppContext): Tweak[FrameLayout] = {
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
      elevation

  }

  def iconStyle(implicit appContext: AppContext): Tweak[ImageView] =
    vMatchParent +
      ivScaleType(ScaleType.CENTER_INSIDE)


  def tabsStyle(implicit appContext: AppContext): Tweak[SlidingTabLayout] =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.height_tabs_collection_details)) +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.margin_top_tabs_collection_details)) +
      stlDefaultTextColor(persistentServices.getCollectionDetailTextTabDefaultColor) +
      stlSelectedTextColor(persistentServices.getCollectionDetailTextTabSelectedColor) +
      elevation

  def viewPagerStyle(implicit appContext: AppContext): Tweak[ViewPager] =
    vMatchParent +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.height_tootlbar_collection_details))


  private val elevation = Lollipop.ifSupportedThen {
    vElevation(resGetDimensionPixelSize(R.dimen.elevation_toolbar))
  }.getOrElse(Tweak.blank)

}

trait CollectionFragmentStyles {

  def recyclerStyle(implicit appContext: AppContext): Tweak[RecyclerView] =
    vMatchParent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_small)) +
      vgClipToPadding(false)

}

trait CollectionAdapterStyles {

  self: PersistentServicesComponent =>

  def rootStyle(heightCard: Int)(implicit appContext: AppContext): Tweak[CardView] =
    vContentSizeMatchWidth(heightCard) +
      cvCardBackgroundColor(persistentServices.getCollectionDetailCardBackgroundColor) +
      flForeground(createBackground)

  private def createBackground(implicit appContext: AppContext): Drawable = {
    val color = persistentServices.getCollectionDetailCardBackgroundPressedColor
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

  def contentStyle(implicit appContext: AppContext): Tweak[LinearLayout] =
    vMatchParent +
      llVertical +
      llGravity(Gravity.CENTER) +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def iconStyle(implicit appContext: AppContext): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_icon_card)
    lp[ViewGroup](size, size)
  }

  def nameStyle(implicit appContext: AppContext): Tweak[TextView] =
    vMatchWidth +
      vPadding(paddingTop = resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvColor(persistentServices.getCollectionDetailTextCardColor) +
      tvLines(2) +
      tvSizeResource(R.dimen.text_default) +
      tvEllipsize(TruncateAt.END)

}
