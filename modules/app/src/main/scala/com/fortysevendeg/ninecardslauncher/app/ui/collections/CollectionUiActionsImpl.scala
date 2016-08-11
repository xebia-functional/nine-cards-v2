package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v4.app.{DialogFragment, Fragment}
import android.support.v7.app.AppCompatActivity
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
import com.fortysevendeg.ninecardslauncher.app.ui.collections.decorations.CollectionItemDecoration
import com.fortysevendeg.ninecardslauncher.app.ui.collections.dialog.EditCardDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.CollectionDialog
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToCloseViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullToCloseListener, PullingListener}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.CollectionRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardBackgroundColor, DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.language.postfixOps

trait CollectionUiActionsImpl
  extends CollectionUiActions {

  self: TypedFindView with Contexts[Fragment] =>

  implicit val presenter: CollectionPresenter

  implicit val theme: NineCardsTheme

  implicit val uiContext: UiContext[_]

  val collectionsPresenter: CollectionsPagerPresenter

  val tagDialog = "dialog"

  var statuses = CollectionStatuses()

  lazy val emptyCollectionView = Option(findView(TR.collection_detail_empty))

  lazy val emptyCollectionMessage = Option(findView(TR.collection_empty_message))

  lazy val recyclerView = Option(findView(TR.collection_detail_recycler))

  lazy val pullToCloseView = Option(findView(TR.collection_detail_pull_to_close))

  lazy val paddingSmall = resGetDimensionPixelSize(R.dimen.padding_small)

  lazy val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val messageText = Html.fromHtml(resGetString(R.string.collectionDetailAddCardsMessage))

  override def updateStatus(canScroll: Boolean, sType: ScrollType): Ui[Any] = Ui {
    statuses = statuses.copy(scrollType = sType, canScroll = canScroll)
  }

  override def startReorder(holder: ViewHolder): Ui[Any] = statuses.touchHelper map { th =>
    uiVibrate() ~ Ui(th.startDrag(holder))
  } getOrElse Ui.nop

  override def initialize(
    animateCards: Boolean,
    collection: Collection): Ui[_] = {
    val itemTouchCallback = new ReorderItemTouchHelperCallback(
      accentColor = resGetColor(getIndexColor(collection.themedColorIndex)),
      onChanged = {
        case (ActionStateReordering, _, position) =>
          if (!isPulling()) {
            statuses = statuses.copy(startPositionReorder = position)
            openReorderMode.run
          }
        case (ActionStateIdle, action, position) =>
          if (!isPulling()) {
            action match {
              case ActionRemove =>
                for {
                  adapter <- getAdapter
                  card <- adapter.collection.cards.lift(position)
                } yield {
                  // If we are removing card, first we move the card to the place where we begin the movement
                  adapter.onItemMove(position, statuses.startPositionReorder)
                  collectionsPresenter.removeCard(card)
                }
                // Update the scroll removing one element
                updateScroll(-1)
              case ActionEdit =>
                for {
                  adapter <- getAdapter
                  collection = adapter.collection
                  card <- collection.cards.lift(position)
                } yield {
                  presenter.reorderCard(collection.id, card.id, position)
                  presenter.editCard(collection.id, card.id, card.term)
                }
              case ActionMove =>
                for {
                  adapter <- getAdapter
                  collection = adapter.collection
                  card <- collection.cards.lift(position)
                } yield {
                  presenter.reorderCard(collection.id, card.id, position)
                  presenter.moveToCollection(card)
                }
                updateScroll(-1)
              case NoAction =>
                for {
                  adapter <- getAdapter
                  collection = adapter.collection
                  card <- collection.cards.lift(position)
                } yield presenter.reorderCard(collection.id, card.id, position)
            }
            closeReorderMode.run
          }
      })
    val itemTouchHelper = new ItemTouchHelper(itemTouchCallback)
    recyclerView foreach itemTouchHelper.attachToRecyclerView

    statuses = statuses.copy(touchHelper= Some(itemTouchHelper))

    (recyclerView <~
      vGlobalLayoutListener(view => {
        loadCollection(collection, paddingSmall, spaceMove, animateCards) ~
          uiHandler(startScroll())
      }) <~
      (if (animateCards) nrvEnableAnimation(R.anim.grid_cards_layout_animation) else Tweak.blank)) ~
      (pullToCloseView <~
        pcvListener(PullToCloseListener(
          close = () => collectionsPresenter.close()
        )) <~
        pdvPullingListener(PullingListener(
          start = () => (recyclerView <~ nrvDisableScroll(true)).run,
          end = () => (recyclerView <~ nrvDisableScroll(false)).run,
          scroll = (scroll: Int, close: Boolean) => collectionsPresenter.pullToClose(scroll, statuses.scrollType, close)
        )))
  }

  override def reloadCards(): Ui[Any] = Ui {
    collectionsPresenter.reloadCards(false)
  }

  override def showMessageNotImplemented(): Ui[Any] = Ui(collectionsPresenter.showMessageNotImplemented())

  override def showContactUsError(): Ui[Any] = showMessage(R.string.contactUsError)

  override def showMessageFormFieldError: Ui[Any] = showMessage(R.string.formFieldError)

  override def showEmptyCollection(): Ui[Any] =
    (emptyCollectionMessage <~
      tvText(messageText) <~
      tvColor(theme.get(DrawerTextColor).alpha(0.8f))) ~
      (emptyCollectionView <~ vVisible <~ cvCardBackgroundColor(theme.get(CardBackgroundColor))) ~
      (recyclerView <~ vGone)

  override def bindAnimatedAdapter(animateCards: Boolean, collection: Collection): Ui[Any] =
    (recyclerView <~
      rvAdapter(createAdapter(collection)) <~
      nrvScheduleLayoutAnimation <~
      getScrollListener(spaceMove)).ifUi(animateCards)

  override def moveToCollection(collections: Seq[Collection], card: Card): Ui[Any] =
    fragmentContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        Ui(new CollectionDialog(collections, c => collectionsPresenter.moveToCollection(c, collections.indexWhere(_.id == c), card), () => ()).show())
      case _ => Ui.nop
    }

  override def editCard(collectionId: Int, cardId: Int, cardName: String): Unit =
    showDialog(new EditCardDialogFragment(collectionId, cardId, cardName))

  override def addCards(cards: Seq[Card]): Ui[Any] = getAdapter map { adapter =>
    adapter.addCards(cards)
    updateScroll()
    val emptyCollection = adapter.collection.cards.isEmpty
    if (!emptyCollection) collectionsPresenter.firstItemInCollection()
    resetScroll ~ showData(emptyCollection)
  } getOrElse Ui.nop

  override def removeCard(card: Card): Ui[Any] = getAdapter map { adapter =>
    adapter.removeCard(card)
    updateScroll()
    val emptyCollection = adapter.collection.cards.isEmpty
    if (emptyCollection) collectionsPresenter.emptyCollection()
    resetScroll ~ showData(emptyCollection)
  } getOrElse Ui.nop

  override def reloadCard(card: Card): Ui[Any] = getAdapter map { adapter =>
    adapter.updateCard(card)
    updateScroll()
    resetScroll
  } getOrElse Ui.nop

  override def reloadCards(cards: Seq[Card]): Ui[Any] = getAdapter map { adapter =>
    adapter.updateCards(cards)
    updateScroll()
    resetScroll
  } getOrElse Ui.nop

  override def showData(emptyCollection: Boolean): Ui[_] =
    if (emptyCollection) showEmptyCollection() else showCollection()

  override def isPulling(): Boolean = (pullToCloseView ~> pdvIsPulling()).get getOrElse false

  override def getCurrentCollection(): Option[Collection] = getAdapter map (_.collection)

  private[this] def showDialog(dialog: DialogFragment): Unit = {
    fragmentContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast(message)

  private[this] def showCollection(): Ui[_] =
    (recyclerView <~ vVisible) ~
      (emptyCollectionView <~ vGone)

  private[this] def openReorderMode: Ui[_] = {
    collectionsPresenter.openReorderMode(statuses.scrollType, statuses.canScroll)
    (pullToCloseView <~ pdvEnable(false)) ~
      (recyclerView <~ nrvRegisterScroll(false)) ~
      (Ui(collectionsPresenter.scrollType(ScrollUp)) ~
      (recyclerView <~
        vPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall))).ifUi(statuses.canScroll)
  }

  private[this] def closeReorderMode: Ui[_] =
    (pullToCloseView <~ pdvEnable(true)) ~
      (recyclerView <~
        vPadding(paddingSmall, spaceMove, paddingSmall, paddingSmall) <~
        vScrollBy(0, -Int.MaxValue) <~
        vScrollBy(0, spaceMove)).ifUi(statuses.canScroll) ~
      (recyclerView <~ nrvRegisterScroll(true) <~ nrvResetScroll(spaceMove))

  def resetScroll: Ui[_] =
    recyclerView <~
      getScrollListener(spaceMove)

  def scrollType(newScrollType: ScrollType): Ui[_] = {
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
        val (paddingTop, marginTop) = newScrollType match {
          case ScrollUp => (paddingSmall, paddingDefault)
          case _ => (spaceMove, spaceMove + paddingSmall)
        }
        (recyclerView <~ vPadding(paddingSmall, paddingTop, paddingSmall, paddingSmall)) ~
          (emptyCollectionView <~ vMargin(paddingDefault, marginTop, paddingDefault, paddingDefault))
      case _ => Ui.nop
    }
  }

  def getAdapter: Option[CollectionAdapter] = recyclerView flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: CollectionAdapter) => Some(a)
      case _ => None
    }
  }

  def updateScroll(offset: Int = 0): Unit = getAdapter foreach { adapter =>
    statuses = statuses.updateScroll(adapter.collection.cards.length + offset)
  }

  private[this] def loadCollection(collection: Collection, padding: Int, spaceMove: Int, animateCards: Boolean): Ui[_] = {

    val adapterTweaks = if (!animateCards) {
      rvAdapter(createAdapter(collection)) +
        getScrollListener(spaceMove)
    } else Tweak.blank

    if (statuses.activeFragment && collection.position == 0 && collection.cards.isEmpty)
      collectionsPresenter.emptyCollection()

    recyclerView <~
      rvLayoutManager(new GridLayoutManager(fragmentContextWrapper.application, numInLine)) <~
      rvFixedSize <~
      adapterTweaks <~
      rvAddItemDecoration(new CollectionItemDecoration) <~
      rvItemAnimator(new DefaultItemAnimator)
  }

  private[this] def getScrollListener(spaceMove: Int) =
    nrvCollectionScrollListener(
      scrolled = (scrollY: Int, dx: Int, dy: Int) => {
        val sy = scrollY + dy
        if (statuses.activeFragment && statuses.canScroll && !isPulling()) {
          collectionsPresenter.scrollY(sy, dy)
        }
        sy
      },
      scrollStateChanged = (scrollY: Int, recyclerView: RecyclerView, newState: Int) => {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) collectionsPresenter.startScroll()
        if (statuses.activeFragment &&
          newState == RecyclerView.SCROLL_STATE_IDLE &&
          statuses.canScroll &&
          !isPulling()) {
          val (moveTo, sType) = if (scrollY < spaceMove / 2) (0, ScrollDown) else (spaceMove, ScrollUp)
          if (scrollY < spaceMove && moveTo != scrollY) recyclerView.smoothScrollBy(0, moveTo - scrollY)
          collectionsPresenter.scrollType(sType)
        }
      }
    )

  private[this] def startScroll(): Ui[_] =
    (statuses.canScroll, statuses.scrollType) match {
      case (true, ScrollUp) => recyclerView <~ vScrollBy(0, spaceMove)
      case (true, ScrollDown) => Ui.nop
      case (false, ScrollUp) =>
        (recyclerView <~ vPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall)) ~
          (emptyCollectionView <~ vMargin(paddingDefault, paddingDefault, paddingDefault, paddingDefault))
      case (false, ScrollDown) =>
        (recyclerView <~ vPadding(paddingSmall, spaceMove, paddingSmall, paddingSmall)) ~
          (emptyCollectionView <~ vMargin(paddingDefault, spaceMove + paddingSmall, paddingDefault, paddingDefault))
      case _ => Ui.nop
    }

  private[this] def createAdapter(collection: Collection) = {
    // In Android Design Library 23.0.1 has a problem calculating the height. We have to subtract 25 dp. We should to check this when we'll change to a new version
    val heightCard = recyclerView map (view => (view.getHeight - (25 dp) - (view.getPaddingBottom + view.getPaddingTop)) / numInLine) getOrElse 0
    CollectionAdapter(collection, heightCard)
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
  touchHelper: Option[ItemTouchHelper] = None,
  scrollType: ScrollType = ScrollNo,
  canScroll: Boolean = false,
  activeFragment: Boolean = false,
  startPositionReorder: Int = 0) {

  def updateScroll(length: Int) = copy(canScroll = length > numSpaces)

}