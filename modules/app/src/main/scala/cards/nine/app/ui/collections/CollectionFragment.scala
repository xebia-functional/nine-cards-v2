package cards.nine.app.ui.collections

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view._
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.collections.CollectionFragment._
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.jobs._
import cards.nine.app.ui.collections.jobs.uiactions._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{PhoneCardType, PublishedByMe}
import cards.nine.models.{Card, Collection}
import cards.nine.process.intents.LauncherExecutorProcessPermissionException
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{TR, _}
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

  lazy val actions = new SingleCollectionUiActions(self, self)

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

  def isActiveFragment: Boolean = actions.singleCollectionStatuses.activeFragment

  def setActiveFragment(activeFragment: Boolean) =
    actions.singleCollectionStatuses = actions.singleCollectionStatuses.copy(activeFragment = activeFragment)

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override def onStart(): Unit = {
    super.onStart()
    singleCollectionJobs.storeCurrentCollectionId.resolveAsync()
  }

  override def onStop(): Unit = {
    super.onStop()
    singleCollectionJobs.removeCurrentCollectionId.resolveAsync()
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = LayoutInflater.from(getActivity).inflate(TR.layout.collection_detail_fragment, container, false)
    rootView = Some(baseView)
    baseView
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    (for {
      _ <- singleCollectionJobs.initialize()
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
            menu.findItem(R.id.action_make_public).setEnabled(true).setTitle(resGetString(R.string.make_public))
            menu.findItem(R.id.action_share).setVisible(false)
        }
        menu.findItem(R.id.action_add_apps).setVisible(true)
        menu.findItem(R.id.action_add_contact).setVisible(true)
        menu.findItem(R.id.action_add_recommendation).setVisible(true)
        menu.findItem(R.id.action_add_shortcut).setVisible(true)
        menu.findItem(R.id.action_edit).setVisible(false)
        menu.findItem(R.id.action_move_to_collection).setVisible(false)
        menu.findItem(R.id.action_delete).setVisible(false)
      case (EditingCollectionMode, 1) =>
        menu.findItem(R.id.action_add_apps).setVisible(false)
        menu.findItem(R.id.action_add_contact).setVisible(false)
        menu.findItem(R.id.action_add_recommendation).setVisible(false)
        menu.findItem(R.id.action_add_shortcut).setVisible(false)
        menu.findItem(R.id.action_make_public).setVisible(false)
        menu.findItem(R.id.action_share).setVisible(false)
        menu.findItem(R.id.action_edit).setVisible(true)
        menu.findItem(R.id.action_move_to_collection).setVisible(true)
        menu.findItem(R.id.action_delete).setVisible(true)
      case (EditingCollectionMode, _) =>
        menu.findItem(R.id.action_add_apps).setVisible(false)
        menu.findItem(R.id.action_add_contact).setVisible(false)
        menu.findItem(R.id.action_add_recommendation).setVisible(false)
        menu.findItem(R.id.action_add_shortcut).setVisible(false)
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
        cards <- groupCollectionsJobs.removeCardsInEditMode()
        _ <- singleCollectionJobs.removeCards(cards)
      } yield ()).resolveAsync()
      true
    case _ => super.onOptionsItemSelected(item)
  }

  override def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit =
    singleCollectionJobs.reorderCard(collectionId, cardId, position).resolveAsync()

  override def scrollStateChanged(idDragging: Boolean): Unit =
    groupCollectionsJobs.showMenu().resolveIf(idDragging, ()).resolveAsync()

  override def close(): Unit = groupCollectionsJobs.close().resolveAsync()

  override def pullToClose(scroll: Int, close: Boolean): Unit =
    toolbarJobs.pullToClose(scroll, close).resolveAsync()

  override def reloadCards(): Unit = groupCollectionsJobs.reloadCards().resolveAsync()

  override def moveToCollection(toCollectionId: Int, collectionPosition: Int): Unit =
    (for {
      cards <- groupCollectionsJobs.moveToCollection(toCollectionId, collectionPosition)
      _ <- singleCollectionJobs.removeCards(cards)
    } yield ()).resolveAsync()

  override def firstItemInCollection(): Unit = groupCollectionsJobs.firstItemInCollection().resolveAsync()

  override def emptyCollection(): Unit = groupCollectionsJobs.emptyCollection().resolveAsync()

  def openReorderMode(): Unit =
    groupCollectionsJobs.openReorderMode().resolveAsync()

  def closeReorderMode(position: Int): Unit = groupCollectionsJobs.closeReorderMode(position).resolveAsync()

  def startReorderCards(holder: ViewHolder): Unit =
    singleCollectionJobs.startReorderCards(holder).resolveAsync()

  override def performCard(card: Card, position: Int): Unit =
    groupCollectionsJobs.performCard(card, position).resolveAsyncServiceOr[Throwable] {
      case _: LauncherExecutorProcessPermissionException if card.cardType == PhoneCardType =>
        groupCollectionsJobs.requestCallPhonePermission(card.intent.extractPhone())
      case _ => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError()
    }
}

object CollectionFragment {
  val keyPosition = "tab_position"
  val keyCollection = "collection"
  val keyCollectionId = "collection_id"
  val keyAnimateCards = "animate_cards"
}

