package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v4.app.Fragment
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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.CollectionDialog
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToCloseViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullToCloseListener, PullingListener}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.CollectionRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.CardPadding
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardBackgroundColor, DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

import scala.language.postfixOps

trait CollectionUiActionsImpl
  extends CollectionUiActions {

  self: TypedFindView with Contexts[Fragment] =>

  implicit val presenter: CollectionPresenter

  implicit val theme: NineCardsTheme

  implicit val uiContext: UiContext[_]

  implicit val collectionsPagerPresenter: CollectionsPagerPresenter

  var statuses = CollectionStatuses()

  lazy val emptyCollectionView = findView(TR.collection_detail_empty)

  lazy val emptyCollectionMessage = findView(TR.collection_empty_message)

  lazy val recyclerView = findView(TR.collection_detail_recycler)

  lazy val pullToCloseView = findView(TR.collection_detail_pull_to_close)

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
      onChanged = {
        case (ActionStateReordering, position) =>
          openReorderMode.run
        case (ActionStateIdle, position) =>
          for {
            adapter <- getAdapter
            collection = adapter.collection
            card <- collection.cards.lift(position)
          } yield presenter.reorderCard(collection.id, card.id, position)
          closeReorderMode(position).run
      })
    val itemTouchHelper = new ItemTouchHelper(itemTouchCallback)
    itemTouchHelper.attachToRecyclerView(recyclerView)

    statuses = statuses.copy(touchHelper= Some(itemTouchHelper))

    (recyclerView <~
      vGlobalLayoutListener(view => {
        loadCollection(collection, paddingSmall, spaceMove, animateCards) ~
          uiHandler(startScroll())
      }) <~
      rvAddOnScrollListener(
        scrolled = (dx, dy) => {
          collectionsPagerPresenter.scrollY(dy)
        },
        scrollStateChanged = (newState) => {
          if (statuses.activeFragment && newState == RecyclerView.SCROLL_STATE_IDLE && !isPulling) {
            collectionsPagerPresenter.scrollIdle()
          }
        }) <~
      (if (animateCards) nrvEnableAnimation(R.anim.grid_cards_layout_animation) else Tweak.blank)) ~
      (pullToCloseView <~
        pcvListener(PullToCloseListener(
          close = () => collectionsPagerPresenter.close()
        )) <~
        pdvPullingListener(PullingListener(
          start = () => (recyclerView <~ nrvDisableScroll(true)).run,
          end = () => (recyclerView <~ nrvDisableScroll(false)).run,
          scroll = (scroll: Int, close: Boolean) => collectionsPagerPresenter.pullToClose(scroll, statuses.scrollType, close)
        )))
  }

  override def reloadCards(): Ui[Any] = Ui {
    collectionsPagerPresenter.reloadCards(false)
  }

  override def showMessageNotImplemented(): Ui[Any] = Ui(collectionsPagerPresenter.showMessageNotImplemented())

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
      nrvScheduleLayoutAnimation).ifUi(animateCards)

  override def moveToCollection(collections: Seq[Collection]): Ui[Any] =
    Ui(new CollectionDialog(collections,
      c => collectionsPagerPresenter.moveToCollection(c, collections.indexWhere(_.id == c)),
      () => ()).show())

  override def addCards(cards: Seq[Card]): Ui[Any] = getAdapter map { adapter =>
    adapter.addCards(cards)
    updateScroll()
    val emptyCollection = adapter.collection.cards.isEmpty
    if (!emptyCollection) collectionsPagerPresenter.firstItemInCollection()
    showData(emptyCollection)
  } getOrElse Ui.nop

  override def removeCards(cards: Seq[Card]): Ui[Any] = getAdapter map { adapter =>
    adapter.removeCards(cards)
    updateScroll()
    val emptyCollection = adapter.collection.cards.isEmpty
    if (emptyCollection) collectionsPagerPresenter.emptyCollection()
    showData(emptyCollection)
  } getOrElse Ui.nop

  override def reloadCard(card: Card): Ui[Any] = getAdapter map { adapter =>
    Ui {
      adapter.updateCard(card)
      updateScroll()
    }
  } getOrElse Ui.nop

  override def reloadCards(cards: Seq[Card]): Ui[Any] = getAdapter map { adapter =>
    Ui {
      adapter.updateCards(cards)
      updateScroll()
    }
  } getOrElse Ui.nop

  override def showData(emptyCollection: Boolean): Ui[_] =
    if (emptyCollection) showEmptyCollection() else showCollection()

  override def changeScrollType(scrollType: ScrollType, scrollY: Int) = {
    val dy = if (scrollType == ScrollUp) spaceMove + scrollY else scrollY
    recyclerView <~ rvSmoothScrollBy(dy = dy)
  }

  override def isPulling: Boolean = (pullToCloseView ~> pdvIsPulling()).get

  override def getCurrentCollection: Option[Collection] = getAdapter map (_.collection)

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast2(message)

  private[this] def showCollection(): Ui[_] =
    (recyclerView <~ vVisible) ~
      (emptyCollectionView <~ vGone)

  private[this] def openReorderMode: Ui[_] = {
    collectionsPagerPresenter.openReorderMode(statuses.scrollType, statuses.canScroll)
    pullToCloseView <~ pdvEnable(false)
  }

  private[this] def closeReorderMode(position: Int): Ui[_] = {
    collectionsPagerPresenter.closeReorderMode(position)
    pullToCloseView <~ pdvEnable(true)
  }

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

  def getAdapter: Option[CollectionAdapter] = recyclerView.getAdapter match {
    case a: CollectionAdapter => Some(a)
    case _ => None
  }

  def updateScroll(offset: Int = 0): Unit = getAdapter foreach { adapter =>
    statuses = statuses.updateScroll(adapter.collection.cards.length + offset)
  }

  private[this] def loadCollection(collection: Collection, padding: Int, spaceMove: Int, animateCards: Boolean): Ui[_] = {

    val adapterTweaks = if (!animateCards) {
      rvAdapter(createAdapter(collection))
    } else Tweak.blank

    if (statuses.activeFragment && collection.position == 0 && collection.cards.isEmpty)
      collectionsPagerPresenter.emptyCollection()

    recyclerView <~
      rvLayoutManager(new GridLayoutManager(fragmentContextWrapper.application, numInLine)) <~
      rvFixedSize <~
      adapterTweaks <~
      rvAddItemDecoration(new CollectionItemDecoration) <~
      rvItemAnimator(new DefaultItemAnimator)
  }

//  private[this] def getScrollListener(spaceMove: Int) =
//    nrvCollectionScrollListener(
//      scrolled = (scrollY: Int, dx: Int, dy: Int) => {
//        val sy = scrollY + dy
//        if (statuses.activeFragment && statuses.canScroll && !isPulling) {
//          collectionsPresenter.scrollY(sy, dy)
//        }
//        sy
//      },
//      scrollStateChanged = (scrollY: Int, recyclerView: RecyclerView, newState: Int) => {
//        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) collectionsPresenter.startScroll()
//        if (statuses.activeFragment &&
//          newState == RecyclerView.SCROLL_STATE_IDLE &&
//          statuses.canScroll &&
//          !isPulling) {
//          val (moveTo, sType) = if (scrollY < spaceMove / 2) (0, ScrollDown) else (spaceMove, ScrollUp)
//          if (scrollY < spaceMove && moveTo != scrollY) recyclerView.smoothScrollBy(0, moveTo - scrollY)
//          collectionsPresenter.scrollType(sType)
//        }
//      }
//    )

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
    val heightCard = {
      val allPadding = (CardPadding.getPadding * 2) * 3
      (recyclerView.getHeight - allPadding - (recyclerView.getPaddingBottom + recyclerView.getPaddingTop)) / numInLine
    }
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
  activeFragment: Boolean = false) {

  def updateScroll(length: Int) = copy(canScroll = length > numSpaces)

}