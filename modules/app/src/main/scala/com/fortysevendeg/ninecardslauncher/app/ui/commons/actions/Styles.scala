package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Tweak}

trait Styles {

  def toolbarStyle(color: Int)(implicit contextWrapper: ContextWrapper) = {
    val closeDrawable = new PathMorphDrawable(
      defaultIcon = IconTypes.CLOSE,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
      padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))
    tbNavigationIcon(closeDrawable) +
      tbBackgroundColor(color)
  }

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    rvFixedSize +
      vPadding(padding, padding, padding, padding) +
      vgClipToPadding(false) +
      vOverScrollMode(View.OVER_SCROLL_NEVER)
  }

  def fabButtonMenuStyle(color: Int)(implicit context: ContextWrapper): Tweak[FloatingActionButton] = {
    val iconFabButton = new PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default))
    ivSrc(iconFabButton) +
      fbaColor(color)
  }

}

