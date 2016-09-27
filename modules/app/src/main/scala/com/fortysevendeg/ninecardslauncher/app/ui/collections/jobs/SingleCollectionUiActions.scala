package com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.{DefaultItemAnimator, GridLayoutManager, RecyclerView}
import android.text.Html
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.decorations.CollectionItemDecoration
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{AppUtils, ImplicitsUiExceptions, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToCloseViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullToCloseListener, PullingListener}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.CollectionRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.CardPadding
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardBackgroundColor, DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

import scala.language.postfixOps

class SingleCollectionUiActions(dom: SingleCollectionDOM with SingleCollectionUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  var statuses = SingleCollectionStatuses()

  lazy val paddingSmall = resGetDimensionPixelSize(R.dimen.padding_small)

  lazy val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val messageText = Html.fromHtml(resGetString(R.string.collectionDetailAddCardsMessage))

  def loadTheme(theme: NineCardsTheme): TaskService[Unit] = TaskService.right {
    statuses = statuses.copy(theme = theme)
  }

  def updateStatus(canScroll: Boolean, sType: ScrollType): TaskService[Unit] = TaskService.right {
    statuses = statuses.copy(scrollType = sType, canScroll = canScroll)
  }

  def startReorder(holder: ViewHolder): TaskService[Unit] = (statuses.touchHelper map { th =>
    uiVibrate() ~ Ui(th.startDrag(holder))
  } getOrElse Ui.nop).toService

  def initialize(
    animateCards: Boolean,
    collection: Collection): TaskService[Unit] = {
    (Ui {
      val itemTouchCallback = new ReorderItemTouchHelperCallback(
        onChanged = {
          case (ActionStateReordering, position) =>
            openReorderMode.run
          case (ActionStateIdle, position) =>
            for {
              adapter <- dom.getAdapter
              collection = adapter.collection
              card <- collection.cards.lift(position)
            } yield dom.reorderCard(collection.id, card.id, position)
            closeReorderMode(position).run
        })
      val itemTouchHelper = new ItemTouchHelper(itemTouchCallback)
      itemTouchHelper.attachToRecyclerView(dom.recyclerView)

      statuses = statuses.copy(touchHelper = Some(itemTouchHelper))
    } ~
      (dom.recyclerView <~
      vGlobalLayoutListener(view => {
        loadCollection(collection, paddingSmall, spaceMove, animateCards) ~
          uiHandler(startScroll())
      }) <~
      rvAddOnScrollListener(
        scrolled = (dx, dy) => {
          dom.scrollY(dy)
        },
        scrollStateChanged = (newState) => {
          val isDragging = newState == RecyclerView.SCROLL_STATE_DRAGGING
          val isIdle = statuses.activeFragment && newState == RecyclerView.SCROLL_STATE_IDLE && !dom.isPulling
          dom.scrollStateChanged(isDragging, isIdle)
        }) <~
      (if (animateCards) nrvEnableAnimation(R.anim.grid_cards_layout_animation) else Tweak.blank)) ~
      (dom.pullToCloseView <~
        pcvListener(PullToCloseListener(
          close = () => dom.close()
        )) <~
        pdvPullingListener(PullingListener(
          start = () => (dom.recyclerView <~ nrvDisableScroll(true)).run,
          end = () => (dom.recyclerView <~ nrvDisableScroll(false)).run,
          scroll = (scroll: Int, close: Boolean) => dom.pullToClose(scroll, statuses.scrollType, close)
        )))).toService
  }

  def reloadCards(): TaskService[Unit] = Ui(dom.reloadCards()).toService

  def isToolbarPulling: TaskService[Boolean] = TaskService.right(dom.isPulling)

  def getCurrentCollection: TaskService[Option[Collection]] = TaskService.right(dom.getCurrentCollection)

  def showContactUsError(): TaskService[Unit] = showMessage(R.string.contactUsError).toService

  def showMessageFormFieldError: TaskService[Unit] = showMessage(R.string.formFieldError).toService

  def showEmptyCollection(): TaskService[Unit] =
    ((dom.emptyCollectionMessage <~
      tvText(messageText) <~
      tvColor(statuses.theme.get(DrawerTextColor).alpha(0.8f))) ~
      (dom.emptyCollectionView <~ vVisible <~ cvCardBackgroundColor(statuses.theme.get(CardBackgroundColor))) ~
      (dom.recyclerView <~ vGone)).toService

  def bindAnimatedAdapter(animateCards: Boolean, collection: Collection): TaskService[Unit] =
    (dom.recyclerView <~
      rvAdapter(createAdapter(collection)) <~
      nrvScheduleLayoutAnimation).ifUi(animateCards).toService

  def moveToCollection(collections: Seq[Collection]): TaskService[Unit] = Ui {
    implicit val theme: NineCardsTheme = statuses.theme
    dom.showCollectionDialog(
      moments = collections filterNot(c => dom.getCurrentCollection.map(_.id).contains(c.id)),
      onCollection = c => dom.moveToCollection(c, collections.indexWhere(_.id == c)))
  }.toService

  def addCards(cards: Seq[Card]): TaskService[Unit] =
    (Ui {
      dom.getAdapter foreach { adapter =>
        adapter.addCards(cards)
        updateScroll()
      }
    } ~
      showList() ~
      Ui(dom.firstItemInCollection())).toService

  def removeCards(cards: Seq[Card]): TaskService[Unit] =
    (Ui {
      dom.getAdapter foreach { adapter =>
        adapter.removeCards(cards)
        val couldScroll = statuses.canScroll
        updateScroll()
        if (couldScroll && !statuses.canScroll && statuses.scrollType == ScrollUp) {
          statuses = statuses.copy(scrollType = ScrollDown)
          dom.forceScrollType(ScrollDown)
        }
      }
    } ~
      {
        val emptyCollection = dom.getAdapter exists(_.collection.cards.isEmpty)
        if (emptyCollection) {
          dom.emptyCollection()
          showEmptyMessage()
        } else {
          Ui.nop
        }
      }).toService

  def reloadCard(card: Card): TaskService[Unit] = Ui {
    dom.getAdapter foreach { adapter =>
      adapter.updateCard(card)
      updateScroll()
    }
  }.toService

  def reloadCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    dom.getAdapter foreach { adapter =>
      adapter.updateCards(cards)
      updateScroll()
    }
  }.toService

  def showData(emptyCollection: Boolean): TaskService[Unit] = (if (emptyCollection)
    showEmptyMessage()
  else
    showList()).toService

  def updateVerticalScroll(scrollY: Int): TaskService[Unit] =
    (dom.recyclerView <~ rvScrollBy(dy = scrollY)).toService

  def scrollType(newScrollType: ScrollType): TaskService[Unit] =
    ((statuses.canScroll, statuses.scrollType) match {
      case (true, s) if s != newScrollType =>
        statuses = statuses.copy(scrollType = newScrollType)
        dom.recyclerView <~
          vScrollBy(0, -Int.MaxValue) <~
          (statuses.scrollType match {
            case ScrollUp => vScrollBy(0, spaceMove)
            case _ => Tweak.blank
          })
      case (false, s) if s != newScrollType =>
        statuses = statuses.copy(scrollType = newScrollType)
        val (paddingTop, marginTop) = newScrollType match {
          case ScrollUp => (paddingSmall, paddingDefault)
          case _ => (spaceMove, spaceMove + paddingSmall)
        }
        (dom.recyclerView <~ vPadding(paddingSmall, paddingTop, paddingSmall, paddingSmall)) ~
          (dom.emptyCollectionView <~ vMargin(paddingDefault, marginTop, paddingDefault, paddingDefault))
      case _ => Ui.nop
    }).toService

  private[this] def showEmptyMessage(): Ui[Any] =
    (dom.emptyCollectionMessage <~
      tvText(messageText) <~
      tvColor(statuses.theme.get(DrawerTextColor).alpha(0.8f))) ~
      (dom.emptyCollectionView <~ vVisible <~ cvCardBackgroundColor(statuses.theme.get(CardBackgroundColor))) ~
      (dom.recyclerView <~ vGone)

  private[this] def showList(): Ui[Any] =
    (dom.recyclerView <~ vVisible) ~
      (dom.emptyCollectionView <~ vGone)

  private[this] def updateScroll(): Unit = dom.getAdapter foreach { adapter =>
    statuses = statuses.updateScroll(adapter.collection.cards.length)
  }

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast2(message)

  private[this] def openReorderMode: Ui[_] = {
    dom.openReorderMode(statuses.scrollType, statuses.canScroll)
    dom.pullToCloseView <~ pdvEnable(false)
  }

  private[this] def closeReorderMode(position: Int): Ui[_] = {
    dom.closeReorderMode(position)
    dom.pullToCloseView <~ pdvEnable(true)
  }

  private[this] def loadCollection(collection: Collection, padding: Int, spaceMove: Int, animateCards: Boolean): Ui[_] = {

    val adapterTweaks = if (!animateCards) {
      rvAdapter(createAdapter(collection))
    } else Tweak.blank

    if (statuses.activeFragment && collection.position == 0 && collection.cards.isEmpty)
      dom.emptyCollection()

    dom.recyclerView <~
      rvLayoutManager(new GridLayoutManager(activityContextWrapper.application, numInLine)) <~
      rvFixedSize <~
      adapterTweaks <~
      rvAddItemDecoration(new CollectionItemDecoration) <~
      rvItemAnimator(new DefaultItemAnimator)
  }

  private[this] def startScroll(): Ui[_] =
    (statuses.canScroll, statuses.scrollType) match {
      case (true, ScrollUp) => dom.recyclerView <~ vScrollBy(0, spaceMove)
      case (true, ScrollDown) => Ui.nop
      case (false, ScrollUp) =>
        (dom.recyclerView <~ vPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall)) ~
          (dom.emptyCollectionView <~ vMargin(paddingDefault, paddingDefault, paddingDefault, paddingDefault))
      case (false, ScrollDown) =>
        (dom.recyclerView <~ vPadding(paddingSmall, spaceMove, paddingSmall, paddingSmall)) ~
          (dom.emptyCollectionView <~ vMargin(paddingDefault, spaceMove + paddingSmall, paddingDefault, paddingDefault))
      case _ => Ui.nop
    }

  private[this] def createAdapter(collection: Collection) = {
    val heightCard = {
      val allPadding = (CardPadding.getPadding * 2) * 3
      (dom.recyclerView.getHeight - allPadding - (dom.recyclerView.getPaddingBottom + dom.recyclerView.getPaddingTop)) / numInLine
    }
    implicit val theme: NineCardsTheme = statuses.theme
    CollectionAdapter(collection, heightCard, dom.performCard, dom.startReorderCards)
  }

}

trait ScrollType

case object ScrollUp extends ScrollType

case object ScrollDown extends ScrollType

object ScrollType {
  def apply(name: String): ScrollType = name match {
    case n if n == ScrollUp.toString => ScrollUp
    case _ => ScrollDown
  }
}

case class SingleCollectionStatuses(
  theme: NineCardsTheme = AppUtils.getDefaultTheme,
  touchHelper: Option[ItemTouchHelper] = None,
  scrollType: ScrollType = ScrollDown,
  canScroll: Boolean = false,
  activeFragment: Boolean = false) {

  def updateScroll(length: Int) = copy(canScroll = length > numSpaces)

}