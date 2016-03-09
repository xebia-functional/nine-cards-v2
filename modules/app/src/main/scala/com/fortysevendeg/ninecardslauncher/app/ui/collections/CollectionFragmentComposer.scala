package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v7.widget.{DefaultItemAnimator, GridLayoutManager, RecyclerView}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{ActionStateIdle, ActionStateReordering, ReorderItemTouchHelperCallback}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToCloseViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullingListener, PullToCloseListener, PullToCloseView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.CollectionRecyclerView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.CollectionRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait CollectionFragmentComposer
  extends CollectionFragmentStyles {

  var statuses = CollectionStatuses()

  var scrolledListener: Option[ScrolledListener] = None

  var recyclerView = slot[CollectionRecyclerView]

  var pullToCloseView = slot[PullToCloseView]

  def layout(animateCards: Boolean, onMoveItems: (Int, Int) => Unit)(implicit contextWrapper: ActivityContextWrapper) = {
    val itemTouchCallback = new ReorderItemTouchHelperCallback(
      onChanged = {
        case (ActionStateReordering, position) =>
          statuses = statuses.copy(startPositionReorder = position)
          runUi(openReorderMode)
        case (ActionStateIdle, position) =>
          onMoveItems(statuses.startPositionReorder, position)
          runUi(closeReorderMode)
      })

    getUi(
      l[PullToCloseView](
        w[CollectionRecyclerView] <~ wire(recyclerView) <~ recyclerStyle(animateCards, itemTouchCallback)
      ) <~
        pcvListener(PullToCloseListener(
          close = () => scrolledListener foreach (_.close())
        )) <~
        wire(pullToCloseView) <~
        pdvPullingListener(PullingListener(
          start = () => runUi(recyclerView <~ nrvDisableScroll(true)),
          end = () => runUi(recyclerView <~ nrvDisableScroll(false)),
          scroll = (scroll: Int, close: Boolean) => scrolledListener foreach (_.pullToClose(scroll, statuses.scrollType, close))
        ))
    )
  }

  def initUi(collection: Collection, animateCards: Boolean)(implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) =
    recyclerView <~
      nrvResetPositions <~
      vGlobalLayoutListener(view => {
        val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
        val padding = resGetDimensionPixelSize(R.dimen.padding_small)
        loadCollection(collection, padding, spaceMove, animateCards) ~
          uiHandler(startScroll(padding, spaceMove))
      })

  def openReorderMode(implicit contextWrapper: ActivityContextWrapper): Ui[_] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    scrolledListener foreach (_.openReorderMode(statuses.scrollType))
    (pullToCloseView <~ pdvEnable(false)) ~
      (recyclerView <~
        vPadding(padding, padding, padding, padding) <~
        nrvRegisterScroll(false))
  }

  def closeReorderMode(implicit contextWrapper: ActivityContextWrapper): Ui[_] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    scrolledListener foreach { sl =>
      sl.scrollType(ScrollDown)
      sl.closeReorderMode()
    }
    (pullToCloseView <~ pdvEnable(true)) ~
      (recyclerView <~
        nrvResetScroll <~
        vPadding(padding, spaceMove, padding, padding) <~
        vScrollBy(0, -Int.MaxValue) <~
        nrvRegisterScroll(true))
  }

  def resetScroll(collection: Collection)(implicit contextWrapper: ActivityContextWrapper): Ui[_] =
    recyclerView <~
      getScrollListener(collection.cards.length, resGetDimensionPixelSize(R.dimen.space_moving_collection_details))

  def setAnimatedAdapter(collection: Collection)
    (implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme): Ui[_] = {
    val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    recyclerView <~
      rvAdapter(createAdapter(collection)) <~
      nrvScheduleLayoutAnimation <~
      getScrollListener(collection.cards.length, spaceMove)
  }

  def scrollType(newScrollType: ScrollType)(implicit contextWrapper: ContextWrapper): Ui[_] = {
    val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    (statuses.canScroll, statuses.scrollType) match {
      case (true, s) if s != newScrollType =>
        statuses = statuses.copy(scrollType = newScrollType)
        recyclerView <~
          vScrollBy(0, -Int.MaxValue) <~
          (statuses.scrollType match {
            case ScrollUp => vScrollBy(0, spaceMove)
            case _ => Tweak.blank
          })
      case (false, s) if s != newScrollType =>
        statuses = statuses.copy(scrollType = newScrollType)
        val paddingTop = newScrollType match {
          case ScrollUp => padding
          case _ => spaceMove
        }
        recyclerView <~ vPadding(padding, paddingTop, padding, padding)
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
        if (statuses.activeFragment && cardsCount > numSpaces) {
          scrolledListener foreach (_.scrollY(sy, dy))
        }
        sy
      },
      scrollStateChanged = (scrollY: Int, recyclerView: RecyclerView, newState: Int) => {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) scrolledListener foreach (_.startScroll())
        if (statuses.activeFragment &&
          newState == RecyclerView.SCROLL_STATE_IDLE &&
          cardsCount > numSpaces) {
          scrolledListener foreach { sl =>
            val (moveTo, sType) = if (scrollY < spaceMove / 2) (0, ScrollDown) else (spaceMove, ScrollUp)
            if (scrollY < spaceMove && moveTo != scrollY) recyclerView.smoothScrollBy(0, moveTo - scrollY)
            sl.scrollType(sType)
          }
        }
      }
    )

  private[this] def startScroll(padding: Int, spaceMove: Int)(implicit contextWrapper: ContextWrapper): Ui[_] =
    (statuses.canScroll, statuses.scrollType) match {
      case (true, ScrollUp) => recyclerView <~ vScrollBy(0, spaceMove)
      case (true, ScrollDown) => recyclerView <~ vScrollBy(0, 0)
      case (false, ScrollUp) => recyclerView <~ vPadding(padding, padding, padding, padding)
      case (false, ScrollDown) => recyclerView <~ vPadding(padding, spaceMove, padding, padding)
      case _ => Ui.nop
    }

  private[this] def createAdapter(collection: Collection)
    (implicit contextWrapper: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) = {
    // In Android Design Library 23.0.1 has a problem calculating the height. We have to subtract 25 dp. We should to check this when we'll change to a new version
    val heightCard = recyclerView map (view => (view.getHeight - (25 dp) - (view.getPaddingBottom + view.getPaddingTop)) / numInLine) getOrElse 0
    new CollectionAdapter(collection, heightCard)
  }

}

trait ScrollType

case object ScrollUp extends ScrollType

case object ScrollDown extends ScrollType

case object ScrollNo extends ScrollType

object ScrollType {
  def apply(name: String): ScrollType = name match {
    case n if n == ScrollUp.toString => ScrollUp
    case n if n == ScrollDown.toString => ScrollDown
    case _ => ScrollNo
  }
}

case class CollectionStatuses(
  scrollType: ScrollType = ScrollNo,
  canScroll: Boolean = false,
  activeFragment: Boolean = false,
  startPositionReorder: Int = 0)