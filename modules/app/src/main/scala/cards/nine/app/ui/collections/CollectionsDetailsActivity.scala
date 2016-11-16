package cards.nine.app.ui.collections

import android.app.Activity
import android.content.Intent
import android.content.Intent._
import android.graphics.{Bitmap, BitmapFactory}
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view._
import cards.nine.app.commons._
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.jobs._
import cards.nine.app.ui.collections.jobs.uiactions._
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.action_filters.{AppInstalledActionFilter, AppsActionFilter}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{ActivityUiContext, AppUtils, UiContext, UiExtensions}
import cards.nine.app.ui.components.drawables.PathMorphDrawable
import cards.nine.app.ui.preferences.commons.{CircleOpeningCollectionAnimation, CollectionOpeningAnimations}
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{NotPublished, PublicCollectionStatus}
import cards.nine.models.{Card, CardData, Collection, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import macroid._

import scala.util.Try

class CollectionsDetailsActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with GroupCollectionsUiListener
  with TypedFindView
  with UiExtensions
  with BroadcastDispatcher { self =>

  val defaultPosition = 0

  val defaultIndexColor = 0

  val defaultIcon = ""

  val defaultStateChanged = false

  var firstTime = true

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  implicit lazy val groupCollectionsJobs = createGroupCollectionsJobs

  implicit lazy val toolbarJobs = createToolbarJobs

  implicit lazy val sharedCollectionJobs = createSharedCollectionJobs

  implicit def getSingleCollectionJobs: Option[SingleCollectionJobs] =
    createSingleCollectionJobs(groupCollectionsJobs.groupCollectionsUiActions.dom)

  def getSingleCollectionJobsByPosition(position: Int): Option[SingleCollectionJobs] =
    createSingleCollectionJobsByPosition(groupCollectionsJobs.groupCollectionsUiActions.dom, position)

  override val actionsFilters: Seq[String] = AppsActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (AppsActionFilter(action), data) match {
    case (Some(AppInstalledActionFilter), _) =>
      (for {
        cards <- groupCollectionsJobs.reloadCards()
        _ <- getSingleCollectionJobs match {
          case Some(singleCollectionJobs) => singleCollectionJobs.reloadCards(cards)
          case _ => TaskService.empty
        }
      } yield ()).resolveAsync()
    case _ =>
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)

    statuses = statuses.reset()

    val position = getInt(
      Seq(bundle, getIntent.getExtras),
      startPositionKey,
      defaultPosition)

    val initialToolbarColor = getInt(
      Seq(bundle, getIntent.getExtras),
      toolbarColorKey,
      defaultIndexColor)

    val backgroundColor = getInt(
      Seq(bundle, getIntent.getExtras),
      backgroundColorKey,
      defaultIndexColor)

    val icon = getString(
      Seq(bundle, getIntent.getExtras),
      toolbarIconKey,
      defaultIcon)

    val isStateChanged = getBoolean(
      Seq(bundle, getIntent.getExtras),
      stateChangedKey,
      defaultStateChanged)

    setContentView(R.layout.collections_detail_activity)

    groupCollectionsJobs.initialize(backgroundColor, initialToolbarColor, icon, position, isStateChanged).
      resolveAsync(
        onResult = (_) => groupCollectionsJobs.groupCollectionsUiActions.openCollectionsWizardInline().resolveAsync(),
        onException = (_) => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError().resolveAsync())

    registerDispatchers()

  }

  override def onResume(): Unit = {
    val anim = CollectionOpeningAnimations.readValue
    if (firstTime && anim == CircleOpeningCollectionAnimation && anim.isSupported) {
      overridePendingTransition(0, 0)
      firstTime = false
    } else {
      overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
    }
    super.onResume()
    groupCollectionsJobs.resume().resolveAsync()
  }

  override def onPause(): Unit = {
    overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)
    super.onPause()
    groupCollectionsJobs.pause().resolveAsync()
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher()
  }

  override def onSaveInstanceState(outState: Bundle): Unit = {
    outState.putInt(startPositionKey, groupCollectionsJobs.groupCollectionsUiActions.dom.getCurrentPosition getOrElse defaultPosition)
    outState.putBoolean(stateChangedKey, true)
    groupCollectionsJobs.groupCollectionsUiActions.dom.getCurrentCollection foreach { collection =>
      outState.putInt(toolbarColorKey, collection.themedColorIndex)
      outState.putString(toolbarIconKey, collection.icon)
    }
    super.onSaveInstanceState(outState)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    requestCode match {
      case `shortcutAdded` => Option(data) flatMap (i => Option(i.getExtras)) match {
        case Some(b: Bundle) if b.containsKey(EXTRA_SHORTCUT_NAME) && b.containsKey(EXTRA_SHORTCUT_INTENT) =>
          val shortcutName = b.getString(EXTRA_SHORTCUT_NAME)
          val shortcutIntent = b.getParcelable[Intent](EXTRA_SHORTCUT_INTENT)
          val maybeBitmap = getBitmapFromShortcutIntent(b)
          (for {
            cards <- groupCollectionsJobs.addShortcut(shortcutName, shortcutIntent, maybeBitmap)
            _ <- getSingleCollectionJobs match {
              case Some(singleCollectionJobs) => singleCollectionJobs.addCards(cards)
              case _ => TaskService.empty
            }
          } yield ()).resolveAsyncServiceOr(_ => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError())
        case _ =>
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.collection_detail_menu, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onPrepareOptionsMenu (menu: Menu): Boolean = {
    groupCollectionsJobs.savePublishStatus().resolveAsync()
    super.onPrepareOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      groupCollectionsJobs.close().resolveAsync()
      false
    case R.id.action_add_card =>
      groupCollectionsJobs.showMenu(openMenu = true).resolveAsyncServiceOr(_ => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError())
      true
    case R.id.action_make_public =>
      sharedCollectionJobs.showPublishCollectionWizard().
        resolveAsyncServiceOr(_ => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError())
      true
    case R.id.action_share =>
      sharedCollectionJobs.shareCollection().
        resolveAsyncServiceOr(_ => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError())
      true
    case _ => super.onOptionsItemSelected(item)
  }

  override def onRequestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): Unit = {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    groupCollectionsJobs.requestPermissionsResult(requestCode, permissions, grantResults).
      resolveAsyncServiceOr(_ => groupCollectionsJobs.groupCollectionsUiActions.showContactUsError())
  }

  override def onBackPressed(): Unit = groupCollectionsJobs.back().resolveAsync()

  private[this] def getBitmapFromShortcutIntent(bundle: Bundle): Option[Bitmap] = bundle match {
    case b if b.containsKey(EXTRA_SHORTCUT_ICON) =>
      Try(b.getParcelable[Bitmap](EXTRA_SHORTCUT_ICON)).toOption
    case b if b.containsKey(EXTRA_SHORTCUT_ICON_RESOURCE) =>
      val extra = Try(b.getParcelable[ShortcutIconResource](EXTRA_SHORTCUT_ICON_RESOURCE)).toOption
      extra flatMap { e =>
        val resources = getPackageManager.getResourcesForApplication(e.packageName)
        val id = resources.getIdentifier(e.resourceName, javaNull, javaNull)
        Option(BitmapFactory.decodeResource(resources, id))
      }
    case _ => None
  }

  override def closeEditingMode(): Unit =
    statuses.collectionMode match {
      case EditingCollectionMode => groupCollectionsJobs.closeEditingMode().resolveAsync()
      case _ =>
    }

  def updateScroll(dy: Int): Unit = getSingleCollectionJobs foreach(_.updateScroll(dy).resolveAsync())

  override def isNormalMode: Boolean = statuses.collectionMode == NormalCollectionMode

  override def isEditingMode: Boolean = statuses.collectionMode == EditingCollectionMode

  override def showPublicCollectionDialog(collection: Collection): Unit =
    groupCollectionsJobs.navigationUiActions.openPublishCollection(collection).resolveAsync()

  def showEditCollectionDialog(cardName: String, onChangeName: (Option[String]) => Unit): Unit =
    groupCollectionsJobs.navigationUiActions.openEditCard(cardName, onChangeName).resolveAsync()

  override def addCards(cardsRequest: Seq[CardData]): Unit =
    (for {
      cards <- groupCollectionsJobs.addCards(cardsRequest)
      _ <- getSingleCollectionJobs match {
        case Some(singleCollectionJobs) => singleCollectionJobs.addCards(cards)
        case _ => TaskService.empty
      }
    } yield ()).resolveAsync()

  override def bindAnimatedAdapter(): Unit = getSingleCollectionJobs foreach(_.bindAnimatedAdapter().resolveAsync())

  override def reloadCards(cards: Seq[Card]): Unit = getSingleCollectionJobs foreach(_.reloadCards(cards).resolveAsync())

  override def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): Unit =
    getSingleCollectionJobs foreach { job =>
      job.saveEditedCard(collectionId, cardId, cardName).resolveAsyncServiceOr(_ => job.showGenericError())
    }

  override def showDataInPosition(position: Int): Unit =
    getSingleCollectionJobsByPosition(position) foreach(_.showData().resolveAsync())

  override def showAppsDialog(args: Bundle): Unit =
    (for {
      _ <- groupCollectionsJobs.groupCollectionsUiActions.hideMenu()
      _ <- groupCollectionsJobs.navigationUiActions.openApps(args)
    } yield ()).resolveAsync()

  override def showContactsDialog(args: Bundle): Unit =
    (for {
      _ <- groupCollectionsJobs.groupCollectionsUiActions.hideMenu()
      _ <- groupCollectionsJobs.navigationUiActions.openContacts(args)
    } yield ()).resolveAsync()

  override def showShortcutsDialog(args: Bundle): Unit =
    (for {
      _ <- groupCollectionsJobs.groupCollectionsUiActions.hideMenu()
      _ <- groupCollectionsJobs.navigationUiActions.openShortcuts(args)
    } yield ()).resolveAsync()

  override def showRecommendationsDialog(args: Bundle): Unit =
    (for {
      _ <- groupCollectionsJobs.groupCollectionsUiActions.hideMenu()
      _ <- groupCollectionsJobs.navigationUiActions.openRecommendations(args)
    } yield ()).resolveAsync()

}

trait ActionsScreenListener {
  def onStartFinishAction()

  def onEndFinishAction()
}

object CollectionsDetailsActivity {

  var statuses = CollectionsDetailsStatuses()

  def createGroupCollectionsJobs
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) = {
    val dom = new GroupCollectionsDOM(activityContextWrapper.getOriginal)
    val listener = activityContextWrapper.getOriginal.asInstanceOf[GroupCollectionsUiListener]
    new GroupCollectionsJobs(
      groupCollectionsUiActions = new GroupCollectionsUiActions(dom, listener),
      toolbarUiActions = new ToolbarUiActions(dom, listener),
      navigationUiActions = new NavigationUiActions())
  }

  def createToolbarJobs
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) = {
    val dom = new GroupCollectionsDOM(activityContextWrapper.getOriginal)
    val listener = activityContextWrapper.getOriginal.asInstanceOf[GroupCollectionsUiListener]
    new ToolbarJobs(new ToolbarUiActions(dom, listener))
  }

  def createSharedCollectionJobs
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) = {
    val dom = new GroupCollectionsDOM(activityContextWrapper.getOriginal)
    val listener = activityContextWrapper.getOriginal.asInstanceOf[GroupCollectionsUiListener]
    new SharedCollectionJobs(new SharedCollectionUiActions(dom, listener))
  }

  def createSingleCollectionJobs(dom: GroupCollectionsDOM): Option[SingleCollectionJobs] =
    dom.getAdapter flatMap (_.getActiveFragment) map (_.singleCollectionJobs)

  def createSingleCollectionJobsByPosition(dom: GroupCollectionsDOM, position: Int): Option[SingleCollectionJobs] =
    dom.getAdapter flatMap (_.getFragmentByPosition(position)) map (_.singleCollectionJobs)

  val startPositionKey = "start_position"
  val backgroundColorKey = "color_background"
  val toolbarColorKey = "color_toolbar"
  val toolbarIconKey = "icon_toolbar"
  val stateChangedKey = "state_changed"

  val cardAdded = "cardAdded"

  def getContentTransitionName(position: Int) = s"icon_$position"
}

case class CollectionsDetailsStatuses(
  theme: NineCardsTheme = AppUtils.getDefaultTheme,
  iconHome: Option[PathMorphDrawable] = None,
  collectionMode: CollectionMode = NormalCollectionMode,
  positionsEditing: Set[Int] = Set.empty,
  lastPhone: Option[String] = None,
  publishStatus: PublicCollectionStatus = NotPublished) {

  def getPositionsSelected: Int = positionsEditing.toSeq.length

  def reset(): CollectionsDetailsStatuses = copy(
    collectionMode = NormalCollectionMode,
    positionsEditing = Set.empty,
    lastPhone = None,
    publishStatus = NotPublished)

}