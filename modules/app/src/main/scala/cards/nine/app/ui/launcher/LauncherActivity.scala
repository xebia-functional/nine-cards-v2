package cards.nine.app.ui.launcher

import android.app.Activity
import android.appwidget.{AppWidgetHost, AppWidgetManager}
import android.bluetooth.{BluetoothAdapter, BluetoothDevice, BluetoothHeadset, BluetoothProfile}
import android.content.{BroadcastReceiver, Context, Intent}
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}

import scala.concurrent.duration._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.{
  ChangeMomentException,
  LoadDataException,
  SpaceException
}
import cards.nine.app.ui.launcher.jobs._
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.app.ui.launcher.types.AppsAlphabetical
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{CardData, Collection, NineCardsTheme, Widget}
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import macroid._

class LauncherActivity
    extends AppCompatActivity
    with Contexts[AppCompatActivity]
    with ContextSupportProvider
    with TypedFindView
    with BroadcastDispatcher { self =>

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(self)

  lazy val launcherJobs = createLauncherJobs

  lazy val appDrawerJobs = createAppDrawerJobs

  lazy val navigationJobs = createNavigationJobs

  lazy val dragJobs = createDragJobs

  lazy val widgetJobs = createWidgetsJobs

  override val actionsFilters: Seq[String] =
    (MomentsActionFilter.cases map (_.action)) ++ (AppsActionFilter.cases map (_.action)) ++ (CollectionsActionFilter.cases map (_.action))

  override def manageCommand(action: String, data: Option[String]): Unit = {
    (MomentsActionFilter(action), AppsActionFilter(action), CollectionsActionFilter(action), data) match {
      case (Some(MomentReloadedActionFilter), _, _, _) =>
        launcherJobs.reloadAppsMomentBar().resolveAsync()
      case (Some(MomentConstrainsChangedActionFilter), _, _, _) =>
        launcherJobs.changeMomentIfIsAvailable(force = false, data).resolveAsync()
      case (Some(MomentAddedOrRemovedActionFilter), _, _, _) =>
        launcherJobs.reloadFence().resolveAsync()
      case (Some(MomentBestAvailableActionFilter), _, _, _) =>
        launcherJobs.changeMomentIfIsAvailable(force = false, data).resolveAsync()
      case (Some(MomentForceBestAvailableActionFilter), _, _, _) =>
        launcherJobs.changeMomentIfIsAvailable(force = true).resolveAsync()
      case (_, Some(AppInstalledActionFilter), _, _) =>
        appDrawerJobs.loadApps(AppsAlphabetical).resolveAsync()
      case (_, Some(AppUninstalledActionFilter), _, _) =>
        appDrawerJobs.loadApps(AppsAlphabetical).resolveAsync()
      case (_, Some(AppUpdatedActionFilter), _, _) =>
        appDrawerJobs.loadApps(AppsAlphabetical).resolveAsync()
      case (_, _, Some(CollectionAddedActionFilter), Some(id)) =>
        launcherJobs
          .reloadCollection(id.toInt)
          .resolveAsyncServiceOr(_ => launcherJobs.navigationUiActions.showContactUsError())
      case _ =>
    }
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    statuses = statuses.copy(
      appWidgetManager = Option(AppWidgetManager.getInstance(this)),
      appWidgetHost = Option(new AppWidgetHost(this, R.id.app_widget_host_id)))

    setContentView(R.layout.launcher_activity)
    registerDispatchers()
    launcherJobs.initialize().resolveAsync()

    import scala.collection.JavaConversions._

    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter

    val pairedDevices = bluetoothAdapter.getBondedDevices

    pairedDevices.map { d =>
      android.util.Log
        .d("9cards", s"name: ${d.getName} - address: ${d.getAddress} - type: ${d.getType}")
    }

    android.util.Log.d("9cards", s"headset: $isBluetoothHeadsetConnected")

    def isBluetoothHeadsetConnected = {
      val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter
      mBluetoothAdapter != null && mBluetoothAdapter.isEnabled && mBluetoothAdapter
        .getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothProfile.STATE_CONNECTED
    }

  }

  override def onStart(): Unit = {
    super.onStart()
    launcherJobs.registerFence().resolveAsync()
  }

  override def onResume(): Unit = {
    super.onResume()
    launcherJobs
      .resume()
      .resolveAsync(
        onResult = (_) =>
          launcherJobs.workspaceUiActions
            .openLauncherWizardInline()
            .resolveAsyncDelayed(3.seconds),
        onException = (ex) =>
          ex match {
            case _: LoadDataException =>
              navigationJobs.navigationUiActions.goToWizard().resolveAsync()
            case _: ChangeMomentException =>
              launcherJobs.reloadAppsMomentBar().resolveAsync()
            case _: UiException =>
              launcherJobs.loadLauncherInfo().resolveAsync()
            case _ =>
        }
      )
  }

  override def onPause(): Unit = {
    super.onPause()
    launcherJobs.pause().resolveAsync()
  }

  override def onStop(): Unit = {
    super.onStop()
    launcherJobs.unregisterFence().resolveAsync()
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher()
    launcherJobs.destroy().resolveAsync()
  }

  override def onBackPressed(): Unit = back().resolveAsync()

  override def onWindowFocusChanged(hasFocus: Boolean): Unit = {
    super.onWindowFocusChanged(hasFocus)
    statuses = statuses.copy(hasFocus = hasFocus)
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val alreadyOnHome = statuses.hasFocus && ((intent.getFlags &
        Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    if (alreadyOnHome) back().resolveAsync()
  }

  override def dispatchKeyEvent(event: KeyEvent): Boolean =
    (event.getAction, event.getKeyCode) match {
      case (KeyEvent.ACTION_DOWN | KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME) =>
        true
      case _ => super.dispatchKeyEvent(event)
    }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {

    def getExtraAppWidgetId =
      Option(data) flatMap (d => Option(d.getExtras)) flatMap { extras =>
        val id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        if (id == 0) None else Some(id)
      }

    (requestCode, resultCode) match {
      case (RequestCodes.goToCollectionDetails, _) =>
        launcherJobs.mainLauncherUiActions.resetFromCollection().resolveAsync()
      case (RequestCodes.goToProfile, ResultCodes.logoutSuccessful) =>
        (for {
          _ <- launcherJobs.workspaceUiActions.cleanWorkspaces()
          _ <- launcherJobs.navigationUiActions.goToWizard()
        } yield ()).resolveAsync()
      case (RequestCodes.goToWidgets, Activity.RESULT_OK) =>
        widgetJobs.configureOrAddWidget(getExtraAppWidgetId).resolveAsync()
      case (RequestCodes.goToConfigureWidgets, Activity.RESULT_OK) =>
        widgetJobs.addWidget(getExtraAppWidgetId).resolveAsyncServiceOr[Throwable] {
          case ex: SpaceException =>
            widgetJobs.navigationUiActions.showWidgetNoHaveSpaceMessage()
          case _ => widgetJobs.navigationUiActions.showContactUsError()
        }
      case (
          RequestCodes.goToConfigureWidgets | RequestCodes.goToWidgets,
          Activity.RESULT_CANCELED) =>
        widgetJobs.cancelWidget(getExtraAppWidgetId).resolveAsync()
      case (RequestCodes.goToPreferences, ResultCodes.preferencesChanged) =>
        launcherJobs
          .preferencesChanged(data.getStringArrayExtra(ResultData.preferencesResultData))
          .resolveAsync()
      case _ =>
    }
  }

  override def onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array[String],
      grantResults: Array[Int]): Unit =
    launcherJobs
      .requestPermissionsResult(requestCode, permissions, grantResults)
      .resolveAsyncServiceOr { _ =>
        launcherJobs.navigationUiActions.showContactUsError()
      }

  private[this] def back() =
    if (statuses.mode == EditWidgetsMode) {
      statuses.transformation match {
        case Some(_) => widgetJobs.backToActionEditWidgets()
        case _       => widgetJobs.closeModeEditWidgets()
      }
    } else if (launcherJobs.mainLauncherUiActions.dom.isDrawerTabsOpened) {
      appDrawerJobs.mainAppDrawerUiActions.closeTabs()
    } else if (launcherJobs.mainLauncherUiActions.dom.isMenuVisible) {
      launcherJobs.menuDrawersUiActions.close()
    } else if (launcherJobs.mainLauncherUiActions.dom.isAppsByMomentMenuVisible) {
      launcherJobs.menuDrawersUiActions.closeAppsMoment()
    } else if (launcherJobs.mainLauncherUiActions.dom.isDrawerVisible) {
      appDrawerJobs.mainAppDrawerUiActions.close()
    } else if (launcherJobs.mainLauncherUiActions.dom.isActionShowed) {
      launcherJobs.navigationUiActions.unrevealActionFragment
    } else if (launcherJobs.mainLauncherUiActions.dom.isBackgroundMenuVisible) {
      launcherJobs.workspaceUiActions.closeBackgroundMenu()
    } else {
      TaskService.empty
    }
}

object LauncherActivity {

  var statuses = LauncherStatuses()

  def createLauncherJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new LauncherJobs(
      mainLauncherUiActions = new LauncherUiActions(dom),
      workspaceUiActions = new WorkspaceUiActions(dom),
      menuDrawersUiActions = new MenuDrawersUiActions(dom),
      appDrawerUiActions = new AppDrawerUiActions(dom),
      navigationUiActions = new NavigationUiActions(dom),
      dockAppsUiActions = new DockAppsUiActions(dom),
      topBarUiActions = new TopBarUiActions(dom),
      widgetUiActions = new WidgetUiActions(dom),
      dragUiActions = new DragUiActions(dom))
  }

  def createAppDrawerJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new AppDrawerJobs(new AppDrawerUiActions(dom))
  }

  def createNavigationJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new NavigationJobs(
      navigationUiActions = new NavigationUiActions(dom),
      menuDrawersUiActions = new MenuDrawersUiActions(dom),
      widgetUiActions = new WidgetUiActions(dom),
      appDrawerUiActions = new AppDrawerUiActions(dom))
  }

  def createWidgetsJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new WidgetsJobs(new WidgetUiActions(dom), new NavigationUiActions(dom))
  }

  def createDragJobs(
      implicit activityContextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      uiContext: UiContext[_]) = {
    val dom = new LauncherDOM(activityContextWrapper.getOriginal)
    new DragJobs(
      mainAppDrawerUiActions = new AppDrawerUiActions(dom),
      dragUiActions = new DragUiActions(dom),
      navigationUiActions = new NavigationUiActions(dom),
      dockAppsUiActions = new DockAppsUiActions(dom),
      workspaceUiActions = new WorkspaceUiActions(dom))
  }

}

case class LauncherStatuses(
    appWidgetManager: Option[AppWidgetManager] = None,
    appWidgetHost: Option[AppWidgetHost] = None,
    theme: NineCardsTheme = AppUtils.getDefaultTheme,
    touchingWidget: Boolean = false, // This parameter is for controlling scrollable widgets
    hasFocus: Boolean = false,
    hostingNoConfiguredWidget: Option[Widget] = None,
    mode: LauncherMode = NormalMode,
    transformation: Option[EditWidgetTransformation] = None,
    idWidget: Option[Int] = None,
    cardAddItemMode: Option[CardData] = None,
    collectionReorderMode: Option[Collection] = None,
    startPositionReorderMode: Int = 0,
    currentDraggingPosition: Int = 0,
    lastPhone: Option[String] = None) {

  def startAddItem(card: CardData): LauncherStatuses =
    copy(mode = AddItemMode, cardAddItemMode = Some(card))

  def startReorder(collection: Collection, position: Int): LauncherStatuses =
    copy(
      startPositionReorderMode = position,
      collectionReorderMode = Some(collection),
      currentDraggingPosition = position,
      mode = ReorderMode)

  def updateCurrentPosition(position: Int): LauncherStatuses =
    copy(currentDraggingPosition = position)

  def reset(): LauncherStatuses =
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

sealed trait DragArea

case object ActionsDragArea extends DragArea

case object WorkspacesDragArea extends DragArea

case object DockAppsDragArea extends DragArea

case object NoDragArea extends DragArea
