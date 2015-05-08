package com.fortysevendeg.ninecardslauncher.ui.wizard

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
import macroid.{Tweak, AppContext}

trait Styles {

  def rootStyle(implicit appContext: AppContext): Tweak[FrameLayout] =
    vMatchParent +
      vFitsSystemWindows(true)

  def toolbarStyle(implicit appContext: AppContext): Tweak[Toolbar] =
    vMatchWidth +
      elevation

  def loadingRootStyle(implicit appContext: AppContext): Tweak[LinearLayout] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical +
      flLayoutGravity(Gravity.CENTER) +
      llGravity(Gravity.CENTER)

  def contentStyle(implicit appContext: AppContext): Tweak[LinearLayout] =
    vMatchParent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical

  def titleTextStyle(implicit appContext: AppContext): Tweak[TextView] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvSizeResource(R.dimen.text_large)

  def defaultTextStyle(implicit appContext: AppContext): Tweak[TextView] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvSizeResource(R.dimen.text_default)

  def groupStyle(implicit appContext: AppContext): Tweak[RadioGroup] =
    llMatchWeightVertical +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical

  def radioStyle(implicit appContext: AppContext): Tweak[RadioButton] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def actionButtonStyle(implicit appContext: AppContext): Tweak[Button] =
    vMatchWidth +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def endMessageStyle(implicit appContext: AppContext): Tweak[TextView] =
    llMatchWeightVertical +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvSizeResource(R.dimen.text_default) +
      tvGravity(Gravity.CENTER)

  private def elevation(implicit appContext: AppContext) = Lollipop.ifSupportedThen {
    vElevation(resGetDimensionPixelSize(R.dimen.elevation_toolbar))
  }.getOrElse(Tweak.blank)

}
