package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup.LayoutParams._
import android.widget.ImageView.ScaleType
import android.widget.{FrameLayout, TextView}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.FabButtonTags._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ColorsUtils, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FabItemMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.CollectionRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{FabItemMenu, CollectionRecyclerView, SlidingTabLayout}
import com.fortysevendeg.ninecardslauncher.process.collection.models.Card
import com.fortysevendeg.ninecardslauncher.process.types._
import CardType._
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, ContextWrapper, Tweak}

trait Styles {

  def rootStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[FrameLayout] =
    vBackgroundColor(theme.get(CollectionDetailBackgroundColor))

  def tabsStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[SlidingTabLayout] =
    stlDefaultTextColor(theme.get(CollectionDetailTextTabDefaultColor)) +
      stlSelectedTextColor(theme.get(CollectionDetailTextTabSelectedColor))

  def fabButtonApplicationsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.applications, R.drawable.fab_menu_icon_applications, 1)

  def fabButtonRecommendationsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.recommendations, R.drawable.fab_menu_icon_recommendations, 2)

  def fabButtonContactsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.contacts, R.drawable.fab_menu_icon_contact, 3)

  def fabButtonShortcutsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.shortcuts, R.drawable.fab_menu_icon_shorcut, 4)

  private[this] def fabButton(title: Int, icon: Int, tag: Int)(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    vWrapContent +
      fimBackgroundColor(resGetColor(R.color.collection_detail_fab_button_item)) +
      fimTitle(resGetString(title)) +
      fimSrc(icon) +
      vGone +
      vTag(R.id.`type`, fabButtonItem) +
      vTag2(R.id.fab_menu_position, tag)

}

trait CollectionFragmentStyles {

  def recyclerStyle(animateCards: Boolean)(implicit context: ContextWrapper): Tweak[CollectionRecyclerView] = {
    val paddingTop = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    vMatchParent +
      vPadding(padding, paddingTop, padding, padding) +
      vgClipToPadding(false) +
      vOverScrollMode(View.OVER_SCROLL_NEVER) +
      (if (animateCards) nrvEnableAnimation(R.anim.grid_cards_layout_animation) else Tweak.blank)
  }

}

trait CollectionAdapterStyles {

  val iconContentHeightRatio = .6f

  val alphaDefault = .1f

  val colorAllNotInstalled = ColorsUtils.setAlpha(Color.BLACK, .2f)

  def rootStyle(heightCard: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[CardView] =
    Tweak[CardView] { view =>
      view.getLayoutParams.height = heightCard
    } +
      cvCardBackgroundColor(theme.get(CollectionDetailCardBackgroundColor)) +
      flForeground(createBackground)

  private[this] def createBackground(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable = {
    val color = theme.get(CollectionDetailCardBackgroundPressedColor)
    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color)),
        null,
        new ColorDrawable(setAlpha(Color.BLACK, alphaDefault)))
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), new ColorDrawable(setAlpha(color, alphaDefault)))
      states.addState(Array.emptyIntArray, new ColorDrawable(Color.TRANSPARENT))
      states
    }
  }

  def iconContentStyle(heightCard: Int)(implicit context: ContextWrapper): Tweak[FrameLayout] =
    Tweak[FrameLayout] { view =>
      view.getLayoutParams.height = (heightCard * iconContentHeightRatio).toInt
    }

  def nameStyle(cardType: CardType)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TextView] =
    cardType match {
      case NoInstalledAppCardType =>
        tvColor(colorAllNotInstalled)
      case _ =>
        tvColor(theme.get(CollectionDetailTextCardColor))
    }

  def iconCardTransform(card: Card)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]) =
    card.cardType match {
      case PhoneCardType | SmsCardType | EmailCardType =>
        ivUriContact(card.imagePath, card.term) +
          vBackground(null) +
          expandLayout +
          ivScaleType(ScaleType.CENTER_CROP)
      case NoInstalledAppCardType =>
        val shape = new ShapeDrawable(new OvalShape)
        shape.getPaint.setColor(colorAllNotInstalled)
        ivSrc(R.drawable.icon_card_not_installed) +
          vBackground(shape) +
          reduceLayout +
          ivScaleType(ScaleType.CENTER_INSIDE)
      case _ =>
        ivCardUri(card.imagePath, card.term, circular = true) +
          vBackground(null) +
          reduceLayout +
          ivScaleType(ScaleType.FIT_CENTER)
    }

  private[this] def expandLayout(implicit context: ContextWrapper): Tweak[View] = Tweak[View] {
    view =>
      val params = view.getLayoutParams
      params.height = MATCH_PARENT
      params.width = MATCH_PARENT
      view.requestLayout()
  }

  private[this] def reduceLayout(implicit context: ContextWrapper): Tweak[View] = Tweak[View] {
    view =>
      val size = resGetDimensionPixelSize(R.dimen.size_icon_card)
      val params = view.getLayoutParams
      params.height = size
      params.width = size
      view.requestLayout()
  }

}
