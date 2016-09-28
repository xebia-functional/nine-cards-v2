package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionFragment._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs.{ScrollType, _}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.PhoneCardType
import com.fortysevendeg.ninecardslauncher.process.intents.LauncherExecutorProcessPermissionException
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{TR, _}
import macroid.Contexts

import scala.language.postfixOps

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with ContextSupportProvider
  with UiExtensions
  with TypedFindView
  with SingleCollectionDOM
  with SingleCollectionUiListener { self =>

  val badActivityMessage = "CollectionFragment only can be loaded in CollectionsDetailsActivity"

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(self)

  lazy val actions = new SingleCollectionUiActions(self)

  lazy val singleCollectionJobs = new SingleCollectionJobs(
    animateCards = getBoolean(Seq(getArguments), keyAnimateCards, default = false),
    maybeCollection = Option(getSerialize[Collection](Seq(getArguments), keyCollection, javaNull)),
    actions = actions)

  lazy val groupCollectionsJobs: GroupCollectionsJobs = getActivity match {
    case activity: CollectionsDetailsActivity => activity.groupCollectionsJobs
    case _ => throw new IllegalArgumentException(badActivityMessage)
  }

  lazy val toolbarJobs: ToolbarJobs = getActivity match {
    case activity: CollectionsDetailsActivity => activity.toolbarJobs
    case _ => throw new IllegalArgumentException(badActivityMessage)
  }

  protected var rootView: Option[View] = None

  def isActiveFragment: Boolean = actions.statuses.activeFragment

  def setActiveFragment(activeFragment: Boolean) =
    actions.statuses = actions.statuses.copy(activeFragment = activeFragment)

  def setActiveFragmentAndScrollType(activeFragment: Boolean, scrollType: ScrollType) =
    actions.statuses = actions.statuses.copy(activeFragment = activeFragment, scrollType = scrollType)

  def setScrollType(scrollType: ScrollType) = singleCollectionJobs.setScrollType(scrollType).resolveAsync()

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = LayoutInflater.from(getActivity).inflate(TR.layout.collection_detail_fragment, container, false)
    rootView = Some(baseView)
    baseView
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    val sType = ScrollType(getArguments.getString(keyScrollType, ScrollDown.toString))
    (for {
      _ <- singleCollectionJobs.initialize(sType)
      _ <- singleCollectionJobs.showData()
    } yield ()).resolveAsync()
    super.onViewCreated(view, savedInstanceState)
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    inflater.inflate(R.menu.collection_edit_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onPrepareOptionsMenu(menu: Menu): Unit = {
    super.onPrepareOptionsMenu(menu)
    (statuses.collectionMode, statuses.positionsEditing.toSeq.length) match {
      case (NormalCollectionMode, _) =>
        statuses.publishStatus match {
          case PublishedByMe =>
            menu.findItem(R.id.action_make_public).setEnabled(false).setTitle(resGetString(R.string.alreadyPublishedCollection))
            menu.findItem(R.id.action_share).setVisible(true)
          case _ =>
            menu.findItem(R.id.action_make_public).setVisible(true)
            menu.findItem(R.id.action_share).setVisible(false)
        }
        menu.findItem(R.id.action_edit).setVisible(false)
        menu.findItem(R.id.action_move_to_collection).setVisible(false)
        menu.findItem(R.id.action_delete).setVisible(false)
      case (EditingCollectionMode, 1) =>
        menu.findItem(R.id.action_make_public).setVisible(false)
        menu.findItem(R.id.action_share).setVisible(false)
        menu.findItem(R.id.action_edit).setVisible(true)
        menu.findItem(R.id.action_move_to_collection).setVisible(true)
        menu.findItem(R.id.action_delete).setVisible(true)
      case (EditingCollectionMode, _) =>
        menu.findItem(R.id.action_make_public).setVisible(false)
        menu.findItem(R.id.action_share).setVisible(false)
        menu.findItem(R.id.action_edit).setVisible(false)
        menu.findItem(R.id.action_move_to_collection).setVisible(true)
        menu.findItem(R.id.action_delete).setVisible(true)
    }
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case R.id.action_edit =>
      groupCollectionsJobs.editCard().resolveAsync()
      true
    case R.id.action_move_to_collection =>
      singleCollectionJobs.moveToCollection().resolveAsyncServiceOr(_ => singleCollectionJobs.showGenericError())
      true
    case R.id.action_delete =>
      (for {
        cards <- groupCollectionsJobs.removeCards()
        _ <- singleCollectionJobs.removeCards(cards)
      } yield ()).resolveAsync()
      true
    case _ => super.onOptionsItemSelected(item)
  }

  override def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit =
    singleCollectionJobs.reorderCard(collectionId, cardId, position).resolveAsync()

  override def scrollY(dy: Int): Unit = toolbarJobs.scrollY(dy).resolveAsync()

  override def scrollStateChanged(idDragging: Boolean, isIdle: Boolean): Unit =
    (for {
      _ <- groupCollectionsJobs.startScroll().resolveIf(idDragging, ())
      _ <- toolbarJobs.scrollIdle().resolveIf(isIdle, ())
    } yield ()).resolveAsync()

  override def close(): Unit = groupCollectionsJobs.close().resolveAsync()

  override def pullToClose(scroll: Int, scrollType: ScrollType, close: Boolean): Unit =
    toolbarJobs.pullToClose(scroll, scrollType, close).resolveAsync()

  override def reloadCards(): Unit = groupCollectionsJobs.reloadCards().resolveAsync()

  override def moveToCollection(toCollectionId: Int, collectionPosition: Int): Unit =
    (for {
      cards <- groupCollectionsJobs.moveToCollection(toCollectionId, collectionPosition)
      _ <- singleCollectionJobs.removeCards(cards)
    } yield ()).resolveAsync()

  override def firstItemInCollection(): Unit = groupCollectionsJobs.firstItemInCollection().resolveAsync()

  override def emptyCollection(): Unit = groupCollectionsJobs.emptyCollection().resolveAsync()

  override def forceScrollType(scrollType: ScrollType): Unit = toolbarJobs.forceScrollType(scrollType).resolveAsync()

  def openReorderMode(scrollType: ScrollType, canScroll: Boolean): Unit =
    groupCollectionsJobs.openReorderMode(scrollType, canScroll).resolveAsync()

  def closeReorderMode(position: Int): Unit =
    groupCollectionsJobs.closeReorderMode(position).resolveAsync()

  def startReorderCards(holder: ViewHolder): Unit =
    singleCollectionJobs.startReorderCards(holder).resolveAsync()

  override def performCard(card: Card, position: Int): Unit =
    groupCollectionsJobs.performCard(card, position).resolveAsyncServiceOr { (e: Throwable) =>
      e match {
        case _: LauncherExecutorProcessPermissionException if card.cardType == PhoneCardType =>
          groupCollectionsJobs.requestCallPhonePermission(card.intent.extractPhone())
        case _ => groupCollectionsJobs.showGenericError()
      }
    }
}

object CollectionFragment {
  val keyPosition = "tab_position"
  val keyCollection = "collection"
  val keyCollectionId = "collection_id"
  val keyScrollType = "scroll_type"
  val keyAnimateCards = "animate_cards"
}

