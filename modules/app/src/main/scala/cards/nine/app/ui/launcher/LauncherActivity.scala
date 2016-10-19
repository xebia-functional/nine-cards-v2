package cards.nine.app.ui.launcher

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import cards.nine.app.ui.collections.ActionsScreenListener
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.drawer.AppsAlphabetical
import cards.nine.app.ui.launcher.exceptions.{ChangeMomentException, LoadDataException, SpaceException}
import cards.nine.app.ui.launcher.jobs._
import cards.nine.commons.services.TaskService
import cards.nine.models.{CardData, Collection, Widget}
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import macroid._

class LauncherActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ActionsScreenListener
  with LauncherUiActionsImpl
  with BroadcastDispatcher { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  implicit lazy val presenter: LauncherPresenter = new LauncherPresenter(self)

  lazy val managerContext: FragmentManagerContext[Fragment, FragmentManager] = activityManagerContext

  lazy val launcherJobs = createLauncherJobs

  lazy val appDrawerJobs = createAppDrawerJobs

  lazy val navigationJobs = createNavigationJobs

  lazy val widgetJobs = createWidgetsJobs

  private[this] var hasFocus = false

  override val actionsFilters: Seq[String] =
    (MomentsActionFilter.cases map (_.action)) ++ (AppsActionFilter.cases map (_.action)) ++ (CollectionsActionFilter.cases map (_.action))

  override def manageCommand(action: String, data: Option[String]): Unit = {
    (MomentsActionFilter(action), AppsActionFilter(action), CollectionsActionFilter(action), data) match {
      case (Some(MomentReloadedActionFilter), _, _, _) => presenter.reloadAppsMomentBar()
      case (Some(MomentConstrainsChangedActionFilter), _, _, _) => presenter.reloadAppsMomentBar()
      case (Some(MomentForceBestAvailableActionFilter), _, _, _) => presenter.changeMomentIfIsAvailable()
      case (_, Some(AppInstalledActionFilter), _, _) => presenter.loadApps(AppsAlphabetical)
      case (_, Some(AppUninstalledActionFilter), _, _) => presenter.loadApps(AppsAlphabetical)
      case (_, Some(AppUpdatedActionFilter), _, _) => presenter.loadApps(AppsAlphabetical)
      case (_, _, Some(CollectionAddedActionFilter), Some(id)) => presenter.reloadCollection(id.toInt)
      case _ =>
    }
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.launcher_activity)
    registerDispatchers()
    launcherJobs.initialize().resolveAsync()
  }

  override def onResume(): Unit = {
    super.onResume()
    launcherJobs.resume().resolveAsyncServiceOr[Throwable] {
      case _: LoadDataException => navigationJobs.goToWizard()
      case _: ChangeMomentException => launcherJobs.reloadAppsMomentBar()
      case _ => TaskService.empty
    }
  }

  override def onPause(): Unit = {
    super.onPause()
    launcherJobs.pause().resolveAsync()
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher()
    launcherJobs.destroy().resolveAsync()
  }

  override def onStartFinishAction(): Unit = launcherJobs.mainLauncherUiActions.resetAction().resolveAsync()

  override def onEndFinishAction(): Unit = launcherJobs.mainLauncherUiActions.destroyAction().resolveAsync()

  override def onBackPressed(): Unit = back().resolveAsync()

  override def onWindowFocusChanged(hasFocus: Boolean): Unit = {
    super.onWindowFocusChanged(hasFocus)
    this.hasFocus = hasFocus
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val alreadyOnHome = hasFocus && ((intent.getFlags &
      Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
      != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    if (alreadyOnHome) back().resolveAsync()
  }

  override def dispatchKeyEvent(event: KeyEvent): Boolean = (event.getAction, event.getKeyCode) match {
    case (KeyEvent.ACTION_DOWN | KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME) => true
    case _ => super.dispatchKeyEvent(event)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {

    def getExtraAppWidgetId = Option(data) flatMap(d => Option(d.getExtras)) flatMap { extras =>
      val id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
      if (id == 0) None else Some(id)
    }

    (requestCode, resultCode) match {
      case (RequestCodes.goToCollectionDetails, _) =>
        presenter.resetFromCollectionDetail()
      case (RequestCodes.goToProfile, ResultCodes.logoutSuccessful) =>
        presenter.logout()
      case (RequestCodes.goToWidgets, Activity.RESULT_OK) =>
        widgetJobs.configureOrAddWidget(getExtraAppWidgetId).resolveAsync()
      case (RequestCodes.goToConfigureWidgets, Activity.RESULT_OK) =>
        widgetJobs.addWidget(getExtraAppWidgetId).resolveAsyncServiceOr[Throwable]{
          case ex: SpaceException => widgetJobs.navigationUiActions.showWidgetNoHaveSpaceMessage()
          case _ => widgetJobs.navigationUiActions.showContactUsError()
        }
      case (RequestCodes.goToConfigureWidgets | RequestCodes.goToWidgets, Activity.RESULT_CANCELED) =>
        widgetJobs.cancelWidget(getExtraAppWidgetId).resolveAsync()
      case (RequestCodes.goToPreferences, ResultCodes.preferencesChanged) =>
        presenter.preferencesChanged(data.getStringArrayExtra(ResultData.preferencesResultData))
      case _ =>
    }
  }

  override def onRequestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): Unit =
    presenter.requestPermissionsResult(requestCode, permissions, grantResults)

  private[this] def back() =
    if (statuses.mode == EditWidgetsMode) {
      statuses.transformation match {
        case Some(_) => widgetJobs.backToActionEditWidgets()
        case _ => widgetJobs.closeModeEditWidgets()
      }
    } else if (launcherJobs.mainLauncherUiActions.dom.isDrawerTabsOpened) {
      appDrawerJobs.mainAppDrawerUiActions.closeTabs()
    } else if (launcherJobs.mainLauncherUiActions.dom.isMenuVisible) {
      launcherJobs.menuDrawersUiActions.close()
    } else if (launcherJobs.mainLauncherUiActions.dom.isDrawerVisible) {
      appDrawerJobs.mainAppDrawerUiActions.close()
    } else if (launcherJobs.mainLauncherUiActions.dom.isActionShowed) {
      launcherJobs.navigationUiActions.unrevealActionFragment
    } else if (launcherJobs.mainLauncherUiActions.dom.isCollectionMenuVisible) {
      launcherJobs.workspaceUiActions.closeMenu()
    } else {
      TaskService.empty
    }
}

object LauncherActivity {

  var statuses = LauncherPresenterStatuses()

  def createLauncherJobs(implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_],
    presenter: LauncherPresenter) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new LauncherJobs(
      mainLauncherUiActions = MainLauncherUiActions(dom),
      workspaceUiActions = WorkspaceUiActions(dom),
      menuDrawersUiActions = MenuDrawersUiActions(dom),
      appDrawerUiActions = MainAppDrawerUiActions(dom),
      navigationUiActions = NavigationUiActions(dom),
      dockAppsUiActions = DockAppsUiActions(dom),
      topBarUiActions = TopBarUiActions(dom),
      widgetUiActions = WidgetUiActions(dom))
  }

  def createAppDrawerJobs(implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_],
    presenter: LauncherPresenter) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new AppDrawerJobs(MainAppDrawerUiActions(dom))
  }

  def createNavigationJobs(implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_],
    presenter: LauncherPresenter) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new NavigationJobs(
      navigationUiActions = NavigationUiActions(dom),
      menuDrawersUiActions = MenuDrawersUiActions(dom),
      widgetUiActions = WidgetUiActions(dom))
  }

  def createWidgetsJobs(implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    WidgetsJobs(WidgetUiActions(dom), NavigationUiActions(dom))
  }

}

case class LauncherPresenterStatuses(
  touchingWidget: Boolean = false, // This parameter is for controlling scrollable widgets
  hostingNoConfiguredWidget: Option[Widget] = None,
  mode: LauncherMode = NormalMode,
  transformation: Option[EditWidgetTransformation] = None,
  idWidget: Option[Int] = None,
  cardAddItemMode: Option[CardData] = None,
  collectionReorderMode: Option[Collection] = None,
  startPositionReorderMode: Int = 0,
  currentDraggingPosition: Int = 0,
  lastPhone: Option[String] = None) {

  def startAddItem(card: CardData): LauncherPresenterStatuses =
    copy(mode = AddItemMode, cardAddItemMode = Some(card))

  def startReorder(collection: Collection, position: Int): LauncherPresenterStatuses =
    copy(
      startPositionReorderMode = position,
      collectionReorderMode = Some(collection),
      currentDraggingPosition = position,
      mode = ReorderMode)

  def updateCurrentPosition(position: Int): LauncherPresenterStatuses =
    copy(currentDraggingPosition = position)

  def reset(): LauncherPresenterStatuses =
    copy(
      startPositionReorderMode = 0,
      cardAddItemMode = None,
      collectionReorderMode = None,
      currentDraggingPosition = 0,
      mode = NormalMode)

  def isReordering: Boolean = mode == ReorderMode

}

sealed trait LauncherMode

case object NormalMode extends LauncherMode

case object AddItemMode extends LauncherMode

case object ReorderMode extends LauncherMode

case object EditWidgetsMode extends LauncherMode

sealed trait EditWidgetTransformation

case object ResizeTransformation extends EditWidgetTransformation

case object MoveTransformation extends EditWidgetTransformation