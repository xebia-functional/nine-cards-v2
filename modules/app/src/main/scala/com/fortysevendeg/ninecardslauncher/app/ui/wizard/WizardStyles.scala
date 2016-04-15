package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.view.ViewGroup
import android.widget.{Button, ImageView, RadioButton}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

trait WizardStyles {

  def defaultActionStyle(implicit context: ActivityContextWrapper): Tweak[Button] =
    vBackgroundTint(resGetColor(R.color.primary))

  def diveInActionStyle(implicit context: ActivityContextWrapper): Tweak[Button] =
    vInvisible +
      vBackgroundTint(resGetColor(R.color.wizard_background_button_dive_in)) +
      vEnabled(false)

  def radioStyle(implicit context: ActivityContextWrapper): Tweak[RadioButton] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.wizard_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.wizard_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.wizard_pager)
  }

}
