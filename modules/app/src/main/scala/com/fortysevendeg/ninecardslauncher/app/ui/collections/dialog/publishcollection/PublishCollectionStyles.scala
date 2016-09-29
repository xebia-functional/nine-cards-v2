package com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog.publishcollection

import android.view.{View, ViewGroup}
import android.widget.TextView
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.styles.CommonStyles
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import cards.nine.process.theme.models.{CardBackgroundColor, DrawerIconColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak}

trait PublishCollectionStyles extends CommonStyles {

  def dialogBackgroundStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    vBackgroundColor(theme.get(CardBackgroundColor))

  def iconStyle(alpha: Float = 1f)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    vBackgroundColor(theme.get(DrawerIconColor).alpha(alpha))

  def paginationItemStyle(implicit context: ContextWrapper): Tweak[TintableImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.publish_collection_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.publish_collection_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.publish_collection_wizard_pager)
  }

  def spinnerStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    titleTextStyle +
      tvSizeResource(R.dimen.text_xlarge) +
      tvColor(theme.get(DrawerIconColor).alpha(0.3f))

}
