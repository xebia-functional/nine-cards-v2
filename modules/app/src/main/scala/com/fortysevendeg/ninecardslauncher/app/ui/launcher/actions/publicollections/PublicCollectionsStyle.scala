package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.view.Gravity
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

trait PublicCollectionsStyle {

  def tabButtonStyle(text: Int)(implicit contextWrapper: ContextWrapper) = {
    val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
    val paddingLarge = resGetDimensionPixelSize(R.dimen.padding_large)
    val paddingSmall = resGetDimensionPixelSize(R.dimen.padding_small)
    vWrapContent +
      tvText(text) +
      tvNormalMedium +
      tvSizeResource(R.dimen.text_large) +
      tvGravity(Gravity.CENTER_VERTICAL) +
      tvColorResource(R.color.tab_public_collection_dialog) +
      vPadding(paddingTop = paddingDefault, paddingBottom = paddingDefault, paddingRight = paddingLarge) +
      tvDrawablePadding(paddingSmall) +
      tvCompoundDrawablesWithIntrinsicBoundsResources(right = R.drawable.tab_menu_indicator)
  }

}
