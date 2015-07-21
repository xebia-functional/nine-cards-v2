package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Tweak, ContextWrapper}

trait Styles {

  def rootStyle(implicit context: ContextWrapper): Tweak[FrameLayout] =
    vMatchParent +
      vFitsSystemWindows(true)

  def toolbarStyle(implicit context: ContextWrapper): Tweak[Toolbar] =
    vMatchWidth +
      elevation

  def loadingRootStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical +
      flLayoutGravity(Gravity.CENTER) +
      llGravity(Gravity.CENTER)

  def contentStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
    vMatchParent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical

  def titleTextStyle(implicit context: ContextWrapper): Tweak[TextView] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvSizeResource(R.dimen.text_large)

  def defaultTextStyle(implicit context: ContextWrapper): Tweak[TextView] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvSizeResource(R.dimen.text_default)

  def groupStyle(implicit context: ContextWrapper): Tweak[RadioGroup] =
    llMatchWeightVertical +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical

  def radioStyle(implicit context: ContextWrapper): Tweak[RadioButton] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def actionButtonStyle(implicit context: ContextWrapper): Tweak[Button] =
    vMatchWidth +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def endMessageStyle(implicit context: ContextWrapper): Tweak[TextView] =
    llMatchWeightVertical +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvSizeResource(R.dimen.text_default) +
      tvGravity(Gravity.CENTER)

  private def elevation(implicit context: ContextWrapper) = Lollipop.ifSupportedThen {
    vElevation(resGetDimensionPixelSize(R.dimen.elevation_toolbar))
  }.getOrElse(Tweak.blank)

}
