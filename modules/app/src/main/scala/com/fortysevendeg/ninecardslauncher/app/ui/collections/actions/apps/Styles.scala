package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v7.widget.RecyclerView
import android.view.View
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Tweak}

trait Styles {

  def toolbarStyle(implicit contextWrapper: ContextWrapper) = {
    val closeDrawable = new PathMorphDrawable(
      defaultIcon = IconTypes.CLOSE,
      defaultStroke = resGetDimensionPixelSize(R.dimen.default_stroke),
      padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))
    tbTitle(R.string.applications) +
      tbNavigationIcon(closeDrawable)
  }

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    rvFixedSize +
      vPadding(padding, padding, padding, padding) +
      vgClipToPadding(false) +
      vOverScrollMode(View.OVER_SCROLL_NEVER)
  }

}

