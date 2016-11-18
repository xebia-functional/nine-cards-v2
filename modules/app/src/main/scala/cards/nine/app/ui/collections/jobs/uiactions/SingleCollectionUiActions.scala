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
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiContext}
import cards.nine.app.ui.components.commons._
import cards.nine.app.ui.components.dialogs.CollectionDialog
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
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.CardViewTweaks._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewTweaks._

import scala.language.postfixOps

class SingleCollectionUiActions(val dom: SingleCollectionDOM, listener: SingleCollectionUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  case class SingleCollectionStatuses(
    touchHelper: Option[ItemTouchHelper] = None,
    activeFragment: Boolean = false)

  val tagDialog = "dialog"

  var singleCollectionStatuses = SingleCollectionStatuses()

  implicit def theme: NineCardsTheme = statuses.theme

  lazy val messageText = Html.fromHtml(resGetString(R.string.collectionDetailAddCardsMessage))

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
        loadCollection(collection, animateCards)
      }) <~
      rvAddOnScrollListener(
        scrolled = (_, _) => {},
        scrollStateChanged = (newState) => {
          val isDragging = newState == RecyclerView.SCROLL_STATE_DRAGGING
          listener.scrollStateChanged(isDragging)
        }) <~
      (if (animateCards) nrvEnableAnimation(R.anim.grid_cards_layout_animation) else Tweak.blank)) ~
      (dom.pullToCloseView <~
        pcvListener(PullToCloseListener(
          close = () => listener.close()
        )) <~
        pdvPullingListener(PullingListener(
          start = () => (dom.recyclerView <~ nrvDisableScroll(true)).run,
          end = () => (dom.recyclerView <~ nrvDisableScroll(false)).run,
          scroll = (scroll: Int, close: Boolean) => listener.pullToClose(scroll, close)
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
    val momentDialog = new CollectionDialog(
      collections filterNot(c => dom.getCurrentCollection.map(_.id).contains(c.id)),
      c => listener.moveToCollection(c, collections.indexWhere(_.id == c)),
      () => ())
    momentDialog.show(fragmentManagerContext.manager, tagDialog)
  }.toService

  def addCards(cards: Seq[Card]): TaskService[Unit] =
    (Ui {
      dom.getAdapter foreach (_.addCards(cards))
    } ~
      showList() ~
      Ui(listener.firstItemInCollection())).toService

  def removeCards(cards: Seq[Card]): TaskService[Unit] =
    (Ui {
      dom.getAdapter foreach (_.removeCards(cards))
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
    dom.getAdapter foreach (_.updateCard(card))
  }.toService

  def reloadCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    dom.getAdapter foreach(_.updateCards(cards))
  }.toService

  def showData(emptyCollection: Boolean): TaskService[Unit] = (if (emptyCollection)
    showEmptyMessage()
  else
    showList()).toService

  private[this] def showEmptyMessage(): Ui[Any] =
    (dom.emptyCollectionMessage <~
      tvText(messageText) <~
      tvColor(statuses.theme.get(DrawerTextColor).alpha(0.8f))) ~
      (dom.emptyCollectionView <~ vVisible <~ cvCardBackgroundColor(statuses.theme.get(CardBackgroundColor))) ~
      (dom.recyclerView <~ vGone)

  private[this] def showList(): Ui[Any] =
    (dom.recyclerView <~ vVisible) ~
      (dom.emptyCollectionView <~ vGone)

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast(message)

  private[this] def openReorderMode: Ui[_] = {
    listener.openReorderMode()
    dom.pullToCloseView <~ pdvEnable(false)
  }

  private[this] def closeReorderMode(position: Int): Ui[_] = {
    listener.closeReorderMode(position)
    dom.pullToCloseView <~ pdvEnable(true)
  }

  private[this] def loadCollection(collection: Collection, animateCards: Boolean): Ui[_] = {

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

  private[this] def createAdapter(collection: Collection) = {
    val heightCard = {
      val allPadding = (CardPadding.getPadding * 2) * 3
      (dom.recyclerView.getHeight - allPadding - (dom.recyclerView.getPaddingBottom + dom.recyclerView.getPaddingTop)) / numInLine
    }
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