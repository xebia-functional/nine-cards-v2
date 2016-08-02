package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import android.text.TextUtils.TruncateAt
import android.view.{Gravity, ViewGroup}
import android.widget.{ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

trait WidgetsStyles {

  def contentMenuItemStyle: Tweak[LinearLayout] =
    vMatchWidth +
      llHorizontal +
      llGravity(Gravity.CENTER_VERTICAL)

  def iconMenuItemStyle(packageName: String, name: String)
    (implicit contextWrapper: ContextWrapper, uiContext: UiContext[_]): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_widget_icon)
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    lp[ViewGroup](size, size) +
      vPaddings(padding) +
      ivSrcByPackageName(Some(packageName), name)
  }

  def textMenuItemStyle(name: String)(implicit contextWrapper: ContextWrapper): Tweak[TextView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    llWrapWeightHorizontal +
      tvColorResource(R.color.widgets_text) +
      vPadding(paddingLeft = padding) +
      tvText(name) +
      tvLines(1) +
      tvNormalMedium +
      tvEllipsize(TruncateAt.END) +
      tvSizeResource(R.dimen.text_large)
  }

}
