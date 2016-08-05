package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.FloatingActionButtonTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerBackgroundColor, DrawerTabsBackgroundColor, NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Tweak}

trait Styles {

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] = rvFixedSize

  def fabButtonMenuStyle(color: Int)(implicit context: ContextWrapper): Tweak[FloatingActionButton] = {
    val iconFabButton = PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default))
    val darkColor = color.dark()
    ivSrc(iconFabButton) +
      fbaColor(color, darkColor)
  }

  def scrollableStyle(implicit context: ContextWrapper, theme: NineCardsTheme) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
      vBackgroundColor(theme.get(DrawerBackgroundColor)) +
      fslColor(theme.get(PrimaryColor), theme.get(DrawerTabsBackgroundColor)) +
      fslMarginRightBarContent(padding)
  }

}

