package com.fortysevendeg.ninecardslauncher.app.ui.collections.styles

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup.LayoutParams._
import android.widget.ImageView.ScaleType
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.DeviceVersion._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.FabButtonTags._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FabItemMenuTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{FabItemMenu, SlidingTabLayout}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.IconsSize
import cards.nine.commons._
import cards.nine.process.commons.models.Card
import cards.nine.process.commons.types._
import cards.nine.process.theme.models._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, ContextWrapper, Tweak}

trait Styles {

  def tabsStyle(implicit theme: NineCardsTheme): Tweak[SlidingTabLayout] =
    stlDefaultTextColor(theme.get(CollectionDetailTextTabDefaultColor)) +
      stlSelectedTextColor(theme.get(CollectionDetailTextTabSelectedColor)) +
      vInvisible

  def titleNameStyle(implicit theme: NineCardsTheme): Tweak[TextView] =
    tvColor(theme.get(CollectionDetailTextTabSelectedColor))

  def selectorStyle(drawable: Drawable)(implicit theme: NineCardsTheme): Tweak[ImageView] =
    ivSrc(drawable)

  def fabButtonApplicationsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.applications, R.drawable.fab_menu_icon_applications, 1)

  def fabButtonRecommendationsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.recommendations, R.drawable.fab_menu_icon_recommendations, 2)

  def fabButtonContactsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.contacts, R.drawable.fab_menu_icon_contact, 3)

  def fabButtonShortcutsStyle(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    fabButton(R.string.shortcuts, R.drawable.fab_menu_icon_shorcut, 4)

  private[this] def fabButton(title: Int, icon: Int, position: Int)(implicit context: ContextWrapper): Tweak[FabItemMenu] =
    vWrapContent +
      fimPopulate(resGetColor(R.color.collection_detail_fab_button_item), icon, title) +
      vGone +
      vSetType(fabButtonItem) +
      vSetPosition(position)

}

trait CollectionAdapterStyles {

  val iconContentHeightRatio = .6f

  val alphaDefault = .1f

  def rootStyle(heightCard: Int)(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[CardView] =
    Tweak[CardView] { view =>
      view.getLayoutParams.height = heightCard
    } +
      cvCardBackgroundColor(theme.get(CardBackgroundColor)) +
      flForeground(createBackground) +
      vDisableHapticFeedback

  private[this] def createBackground(implicit context: ContextWrapper, theme: NineCardsTheme): Drawable = {
    val color = theme.get(CardBackgroundPressedColor)
    Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color)),
        javaNull,
        new ColorDrawable(Color.BLACK.alpha(alphaDefault)))
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), new ColorDrawable(color.alpha(alphaDefault)))
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
        tvColor(theme.get(CardTextColor).alpha(.4f))
      case _ =>
        tvColor(theme.get(CardTextColor))
    }

  def iconCardTransform(card: Card)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) =
    card.cardType match {
      case cardType if cardType.isContact =>
        ivUriContactFromLookup(card.intent.extractLookup(), card.term) +
          vBackground(javaNull) +
          expandLayout +
          ivScaleType(ScaleType.CENTER_CROP)
      case AppCardType => ivSrcByPackageName(card.packageName, card.term)
      case NoInstalledAppCardType =>
        val shape = new ShapeDrawable(new OvalShape)
        shape.getPaint.setColor(theme.get(CardTextColor).alpha(.4f))
        val iconColor = theme.get(CardBackgroundColor)
        ivSrc(R.drawable.icon_card_not_installed) +
          tivDefaultColor(iconColor) +
          tivPressedColor(iconColor) +
          vBackground(shape) +
          reduceLayout +
          ivScaleType(ScaleType.CENTER_INSIDE)
      case _ =>
        ivCardUri(card.imagePath, card.term, circular = true) +
          vBackground(javaNull) +
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

  private[this] def reduceLayout(implicit context: ContextWrapper): Tweak[View] =
    vResize(IconsSize.getIconApp)

}
