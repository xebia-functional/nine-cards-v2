package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v7.widget.{CardView, DefaultItemAnimator, GridLayoutManager, RecyclerView}
import android.view.View
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToCloseViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullToDownListener, PullToCloseListener, PullToCloseView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.CollectionRecyclerView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.CollectionRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, _}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, EmailCardType, PhoneCardType, SmsCardType}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait CollectionFragmentComposer
  extends CollectionFragmentStyles {

  var sType = -1

  var canScroll = false

  var activeFragment = false

  var scrolledListener: Option[ScrolledListener] = None

  var recyclerView = slot[CollectionRecyclerView]

  def layout(animateCards: Boolean)(implicit contextWrapper: ActivityContextWrapper) = getUi(
    l[PullToCloseView](
      w[CollectionRecyclerView] <~ wire(recyclerView) <~ recyclerStyle(animateCards)
    ) <~ pcvListener(PullToCloseListener(
      close = () => scrolledListener foreach (_.close())
    )) <~ pdvListener(PullToDownListener(
      startPulling = () => runUi(recyclerView <~ nrvDisableScroll(true)),
      endPulling = () => runUi(recyclerView <~ nrvDisableScroll(false)),
      scroll = (scroll: Int, close: Boolean) => scrolledListener foreach (_.pullToClose(scroll, sType, close))
    ))
  )

  def initUi(collection: Collection, animateCards: Boolean)(implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) =
    recyclerView <~
      nrvResetPositions <~
      vGlobalLayoutListener(view => {
        val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
        val padding = resGetDimensionPixelSize(R.dimen.padding_small)
        loadCollection(collection, padding, spaceMove, animateCards) ~
          uiHandler(startScroll(padding, spaceMove))
      })

  def resetScroll(collection: Collection)(implicit contextWrapper: ActivityContextWrapper) =
    recyclerView <~
      getScrollListener(collection.cards.length, resGetDimensionPixelSize(R.dimen.space_moving_collection_details))

  def setAnimatedAdapter(collection: Collection)
    (implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) = {
    val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    recyclerView <~
      rvAdapter(createAdapter(collection)) <~
      nrvScheduleLayoutAnimation <~
      getScrollListener(collection.cards.length, spaceMove)
  }

  def scrollType(newSType: Int)(implicit contextWrapper: ContextWrapper): Ui[_] = {
    val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    (canScroll, sType) match {
      case (scroll, s) if s != newSType && scroll =>
        sType = newSType
        recyclerView <~
          vScrollBy(0, -Int.MaxValue) <~
          (if (sType == ScrollType.up) vScrollBy(0, spaceMove) else Tweak.blank)
      case (_, s) if s != newSType =>
        sType = newSType
        recyclerView <~ vPadding(padding, if (newSType == ScrollType.up) padding else spaceMove, padding, padding)
      case _ => Ui.nop
    }
  }

  def getAdapter: Option[CollectionAdapter] = recyclerView flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: CollectionAdapter) => Some(a)
      case _ => None
    }
  }

  private[this] def loadCollection(collection: Collection, padding: Int, spaceMove: Int, animateCards: Boolean)
    (implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme): Ui[_] = {

    val adapterTweaks = if (!animateCards) {
      rvAdapter(createAdapter(collection)) +
        getScrollListener(collection.cards.length, spaceMove)
    } else Tweak.blank

    recyclerView <~
      rvLayoutManager(new GridLayoutManager(contextWrapper.application, numInLine)) <~
      rvFixedSize <~
      adapterTweaks <~
      rvAddItemDecoration(new CollectionItemDecorator) <~
      rvItemAnimator(new DefaultItemAnimator)
  }

  private[this] def getScrollListener(cardsCount: Int, spaceMove: Int)(implicit contextWrapper: ActivityContextWrapper) =
    nrvCollectionScrollListener(
      scrolled = (scrollY: Int, dx: Int, dy: Int) => {
        val sy = scrollY + dy
        if (activeFragment && cardsCount > numSpaces) {
          scrolledListener foreach (_.scrollY(sy, dy))
        }
        sy
      },
      scrollStateChanged = (scrollY: Int, recyclerView: RecyclerView, newState: Int) => {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) scrolledListener foreach (_.startScroll())
        if (activeFragment &&
          newState == RecyclerView.SCROLL_STATE_IDLE &&
          cardsCount > numSpaces) {
          scrolledListener foreach { sl =>
            val (moveTo, sType) = if (scrollY < spaceMove / 2) (0, ScrollType.down) else (spaceMove, ScrollType.up)
            (scrollY, moveTo, sType) match {
              case (y, move, st) if y < spaceMove && moveTo != scrollY =>
                sl.scrollType(sType)
                recyclerView.smoothScrollBy(0, moveTo - scrollY)
              case _ =>
            }
            sl.scrollType(sType)
          }
        }
      }
    )

  private[this] def startScroll(padding: Int, spaceMove: Int)(implicit contextWrapper: ContextWrapper): Ui[_] =
    (canScroll, sType) match {
      case (true, s) => recyclerView <~ vScrollBy(0, if (s == ScrollType.up) spaceMove else 0)
      case (_, s) => recyclerView <~ vPadding(padding, if (s == ScrollType.up) padding else spaceMove, padding, padding)
    }

  private[this] def createAdapter(collection: Collection)
    (implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) = {
    // In Android Design Library 23.0.1 has a problem calculating the height. We have to subtract 25 dp. We should to check this when we'll change to a new version
    val heightCard = recyclerView map (view => (view.getHeight - (25 dp) - (view.getPaddingBottom + view.getPaddingTop)) / numInLine) getOrElse 0
    new CollectionAdapter(collection, heightCard)
  }

}

case class ViewHolderCollectionAdapter(content: CardView, heightCard: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with CollectionAdapterStyles
  with TypedFindView {

  lazy val iconContent = Option(findView(TR.card_icon_content))

  lazy val icon = Option(findView(TR.card_icon))

  lazy val name = Option(findView(TR.card_text))

  lazy val badge = Option(findView(TR.card_badge))

  runUi(
    (content <~ rootStyle(heightCard)) ~
      (iconContent <~ iconContentStyle(heightCard)))

  def bind(card: Card, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    (icon <~ iconCardTransform(card)) ~
      (name <~ tvText(card.term)) ~
      (content <~ vTag2(position)) ~
      (badge <~ (getBadge(card.cardType) map {
        ivSrc(_) + vVisible
      } getOrElse vGone)) ~
      (name <~ nameStyle(card.cardType))

  private[this] def getBadge(cardType: CardType): Option[Int] = cardType match {
    case PhoneCardType => Option(R.drawable.badge_phone)
    case SmsCardType => Option(R.drawable.badge_sms)
    case EmailCardType => Option(R.drawable.badge_email)
    case _ => None
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
