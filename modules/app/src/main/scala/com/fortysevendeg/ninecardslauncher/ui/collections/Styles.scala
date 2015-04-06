package com.fortysevendeg.ninecardslauncher.ui.collections

import android.support.v4.view.ViewPager
import android.support.v7.widget.{CardView, RecyclerView, Toolbar}
import android.text.TextUtils.TruncateAt
import android.view.{Gravity, ViewGroup}
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServicesComponent
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
      vBackgroundColor(persistentServices.getCollectionDetailBackgroundColor())

  def toolbarStyle(implicit appContext: AppContext): Tweak[Toolbar] =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.height_tootlbar_collection_details)) +
      vBackground(R.color.primary)

  def iconStyle(implicit appContext: AppContext): Tweak[ImageView] =
    vWrapContent +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.padding_small)) +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def tabsStyle(implicit appContext: AppContext): Tweak[SlidingTabLayout] =
    vMatchWidth +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.margin_top_tabs_collection_details)) +
      stlDefaultTextColor(persistentServices.getCollectionDetailTextTabDefaultColor()) +
      stlSelectedTextColor(persistentServices.getCollectionDetailTextTabSelectedColor())

  def viewPagerStyle(implicit appContext: AppContext): Tweak[ViewPager] =
    vMatchParent +
      flLayoutMargin(marginTop = resGetDimensionPixelSize(R.dimen.margin_top_pagers_collection_details))

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
      cvCardBackgroundColor(persistentServices.getCollectionDetailCardBackgroundColor())

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
      tvColor(persistentServices.getCollectionDetailTextCardColor()) +
      tvLines(2) +
      tvSizeResource(R.dimen.text_default) +
      tvEllipsize(TruncateAt.END)

}
