package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog

import android.view.ViewGroup
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{Tweak, ContextWrapper}

trait PublishCollectionStyles {

  def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.publish_collection_wizard_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.publish_collection_wizard_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.publish_collection_wizard_pager)
  }

}
