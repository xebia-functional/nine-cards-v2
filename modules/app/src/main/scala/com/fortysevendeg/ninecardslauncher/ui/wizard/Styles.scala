package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.support.v7.widget.Toolbar
import android.widget.{Button, RadioButton, RadioGroup, FrameLayout}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Tweak, AppContext}

trait Styles {

  def rootStyle(implicit appContext: AppContext): Tweak[FrameLayout] =
    vMatchParent +
      vFitsSystemWindows(true)

  def toolbarStyle(implicit appContext: AppContext): Tweak[Toolbar] =
    vMatchWidth +
      elevation

  def contentUserStyle(implicit appContext: AppContext): Tweak[RadioGroup] =
    vMatchParent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical

  def userGroupStyle(implicit appContext: AppContext): Tweak[RadioGroup] =
    llMatchWeightVertical +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      llVertical

  def userRadioStyle(implicit appContext: AppContext): Tweak[RadioButton] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def selectUserButtonStyle(implicit appContext: AppContext): Tweak[Button] =
    vMatchWidth +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
      tvText(R.string.buttonContinue)

  private def elevation(implicit appContext: AppContext) = Lollipop.ifSupportedThen {
    vElevation(resGetDimensionPixelSize(R.dimen.elevation_toolbar))
  }.getOrElse(Tweak.blank)

}
