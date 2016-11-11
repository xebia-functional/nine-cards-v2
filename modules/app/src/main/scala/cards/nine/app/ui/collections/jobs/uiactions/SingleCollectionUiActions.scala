package cards.nine.app.ui.collections.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.{DefaultItemAnimator, GridLayoutManager, RecyclerView}
import android.text.Html
import cards.nine.app.ui.collections.CollectionAdapter
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.decorations.CollectionItemDecoration
import cards.nine.app.ui.commons.Constants._
import macroid.extras.UIActionsExtras._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiContext}
import cards.nine.app.ui.components.commons._
import cards.nine.app.ui.components.layouts.tweaks.PullToCloseViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.app.ui.components.layouts.{PullToCloseListener, PullingListener}
import cards.nine.app.ui.components.widgets.tweaks.CollectionRecyclerViewTweaks._
import cards.nine.app.ui.preferences.commons.CardPadding
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.theme.{CardBackgroundColor, DrawerTextColor}
import cards.nine.models.{Card, Collection, NineCardsTheme}
import macroid.extras.CardViewTweaks._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.language.postfixOps

class SingleCollectionUiActions(val dom: SingleCollectionDOM, listener: SingleCollectionUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  case class SingleCollectionStatuses(
    touchHelper: Option[ItemTouchHelper] = None,
    scrollType: ScrollType = ScrollDown,
    canScroll: Boolean = false,
    activeFragment: Boolean = false) {

    def updateScroll(length: Int) = copy(canScroll = length > numSpaces)

  }

  var singleCollectionStatuses = SingleCollectionStatuses()

  implicit def theme: NineCardsTheme = statuses.theme

  lazy val paddingSmall = resGetDimensionPixelSize(R.dimen.padding_small)

  lazy val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val messageText = Html.fromHtml(resGetString(R.string.collectionDetailAddCardsMessage))

  def updateStatus(canScroll: Boolean, sType: ScrollType): TaskService[Unit] = TaskService.right {
    singleCollectionStatuses = singleCollectionStatuses.copy(scrollType = sType, canScroll = canScroll)
  }

  def startReorder(holder: ViewHolder): TaskService[Unit] = (singleCollectionStatuses.touchHelper match {
    case Some(th) => uiVibrate() ~ Ui(th.startDrag(holder))
    case _ => Ui.nop
  }).toService

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
            } yield listener.reorderCard(collection.id, card.id, position)
            closeReorderMode(position).run
        })
      val itemTouchHelper = new ItemTouchHelper(itemTouchCallback)
      itemTouchHelper.attachToRecyclerView(dom.recyclerView)

      singleCollectionStatuses = singleCollectionStatuses.copy(touchHelper = Some(itemTouchHelper))
    } ~
      (dom.recyclerView <~
      vGlobalLayoutListener(view => {
        loadCollection(collection, paddingSmall, spaceMove, animateCards) ~
          uiHandler(startScroll())
      }) <~
      rvAddOnScrollListener(
        scrolled = (dx, dy) => {
          listener.scrollY(dy)
        },
        scrollStateChanged = (newState) => {
          val isDragging = newState == RecyclerView.SCROLL_STATE_DRAGGING
          val isIdle = singleCollectionStatuses.activeFragment && newState == RecyclerView.SCROLL_STATE_IDLE && !dom.isPulling
          listener.scrollStateChanged(isDragging, isIdle)
        }) <~
      (if (animateCards) nrvEnableAnimation(R.anim.grid_cards_layout_animation) else Tweak.blank)) ~
      (dom.pullToCloseView <~
        pcvListener(PullToCloseListener(
          close = () => listener.close()
        )) <~
        pdvPullingListener(PullingListener(
          start = () => (dom.recyclerView <~ nrvDisableScroll(true)).run,
          end = () => (dom.recyclerView <~ nrvDisableScroll(false)).run,
          scroll = (scroll: Int, close: Boolean) => listener.pullToClose(scroll, singleCollectionStatuses.scrollType, close)
        )))).toService
  }

  def reloadCards(): TaskService[Unit] = Ui(listener.reloadCards()).toService

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
      onCollection = c => listener.moveToCollection(c, collections.indexWhere(_.id == c)))
  }.toService

  def addCards(cards: Seq[Card]): TaskService[Unit] =
    (Ui {
      dom.getAdapter foreach { adapter =>
        adapter.addCards(cards)
        updateScroll()
      }
    } ~
      showList() ~
      Ui(listener.firstItemInCollection())).toService

  def removeCards(cards: Seq[Card]): TaskService[Unit] =
    (Ui {
      dom.getAdapter foreach { adapter =>
        adapter.removeCards(cards)
        val couldScroll = singleCollectionStatuses.canScroll
        updateScroll()
        if (couldScroll && !singleCollectionStatuses.canScroll && singleCollectionStatuses.scrollType == ScrollUp) {
          singleCollectionStatuses = singleCollectionStatuses.copy(scrollType = ScrollDown)
          listener.forceScrollType(ScrollDown)
        }
      }
    } ~
      {
        val emptyCollection = dom.getAdapter exists(_.collection.cards.isEmpty)
        if (emptyCollection) {
          listener.emptyCollection()
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
    ((singleCollectionStatuses.canScroll, singleCollectionStatuses.scrollType) match {
      case (true, s) if s != newScrollType =>
        singleCollectionStatuses = singleCollectionStatuses.copy(scrollType = newScrollType)
        dom.recyclerView <~
          vScrollBy(0, -Int.MaxValue) <~
          (singleCollectionStatuses.scrollType match {
            case ScrollUp => vScrollBy(0, spaceMove)
            case _ => Tweak.blank
          })
      case (false, s) if s != newScrollType =>
        singleCollectionStatuses = singleCollectionStatuses.copy(scrollType = newScrollType)
        val (paddingTop, marginTop) = newScrollType match {
          case ScrollUp => (paddingSmall, paddingDefault)
          case _ => (spaceMove, spaceMove + paddingSmall)
        }
        (dom.recyclerView <~ vScrollBy(0, -Int.MaxValue) <~ vPadding(paddingSmall, paddingTop, paddingSmall, paddingSmall)) ~
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
    singleCollectionStatuses = singleCollectionStatuses.updateScroll(adapter.collection.cards.length)
  }

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast(message)

  private[this] def openReorderMode: Ui[_] = {
    listener.openReorderMode(singleCollectionStatuses.scrollType, singleCollectionStatuses.canScroll)
    dom.pullToCloseView <~ pdvEnable(false)
  }

  private[this] def closeReorderMode(position: Int): Ui[_] = {
    listener.closeReorderMode(position)
    dom.pullToCloseView <~ pdvEnable(true)
  }

  private[this] def loadCollection(collection: Collection, padding: Int, spaceMove: Int, animateCards: Boolean): Ui[_] = {

    val adapterTweaks = if (!animateCards) {
      rvAdapter(createAdapter(collection))
    } else Tweak.blank

    if (singleCollectionStatuses.activeFragment && collection.position == 0 && collection.cards.isEmpty)
      listener.emptyCollection()

    dom.recyclerView <~
      rvLayoutManager(new GridLayoutManager(activityContextWrapper.application, numInLine)) <~
      rvFixedSize <~
      adapterTweaks <~
      rvAddItemDecoration(new CollectionItemDecoration) <~
      rvItemAnimator(new DefaultItemAnimator)
  }

  private[this] def startScroll(): Ui[_] =
    (singleCollectionStatuses.canScroll, singleCollectionStatuses.scrollType) match {
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
    CollectionAdapter(collection, heightCard, listener.performCard, listener.startReorderCards)
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