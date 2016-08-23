package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.appwidget.{AppWidgetHost, AppWidgetManager}
import android.content.{ClipData, ComponentName, Intent}
import android.graphics.Point
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent._
import android.view.View.OnDragListener
import android.view.{DragEvent, View, WindowManager}
import com.fortysevendeg.macroid.extras.DeviceVersion.{KitKat, Lollipop}
import com.fortysevendeg.macroid.extras.DrawerLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardsPreferencesValue
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsExcerpt._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.{AlertDialogFragment, MomentDialog}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.RippleCollectionDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AppsMomentLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.CollectionActionsPanelLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DockAppsPanelLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.EditWidgetsBottomPanelLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.EditWidgetsTopPanelLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.{LauncherData, LauncherMoment}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.EditWidgetsMode
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets.WidgetsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.collection.CollectionsUiActions
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drag.AppDrawerIconShadowBuilder
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.DrawerUiActions
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders.Arrow
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.snails.LauncherSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.types.{AddItemToCollection, ReorderCollection}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, CardType, NineCardsMoment}
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, LastCallsContact, _}
import com.fortysevendeg.ninecardslauncher.process.device.{GetAppOrder, GetByName}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
import com.fortysevendeg.ninecardslauncher.process.widget.{MoveWidgetRequest, ResizeWidgetRequest}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait LauncherUiActionsImpl
  extends LauncherUiActions
    with CollectionsUiActions
    with DrawerUiActions {

  self: TypedFindView with SystemBarsTint with Contexts[AppCompatActivity] =>

  lazy val preferenceValues = new NineCardsPreferencesValue

  implicit val uiContext: UiContext[Activity]

  implicit val presenter: LauncherPresenter

  implicit lazy val theme: NineCardsTheme = presenter.getTheme

  implicit val managerContext: FragmentManagerContext[Fragment, FragmentManager]

  lazy val appWidgetManager = AppWidgetManager.getInstance(activityContextWrapper.application)

  lazy val appWidgetHost = new AppWidgetHost(activityContextWrapper.application, R.id.app_widget_host_id)

  lazy val foreground = Option(findView(TR.launcher_foreground))

  lazy val appsMoment = Option(findView(TR.launcher_apps_moment))

  lazy val actionForCollections = Seq(
    CollectionActionItem(resGetString(R.string.edit), R.drawable.icon_launcher_action_edit, CollectionActionEdit),
    CollectionActionItem(resGetString(R.string.remove), R.drawable.icon_launcher_action_remove, CollectionActionRemove))

  lazy val actionForApps = Seq(
    CollectionActionItem(resGetString(R.string.appInfo), R.drawable.icon_launcher_action_info_app, CollectionActionAppInfo),
    CollectionActionItem(resGetString(R.string.uninstall), R.drawable.icon_launcher_action_uninstall, CollectionActionUninstall))

  val tagDialog = "dialog"

  override def initialize: Ui[Any] =
    Ui {
      appWidgetHost.startListening()
      initAllSystemBarsTint
    } ~
      prepareBars ~
      initCollectionsUi ~
      initDrawerUi ~
      (root <~ dragListener())

  override def destroy: Ui[Any] = Ui(appWidgetHost.stopListening())

  override def reloadWorkspaces(data: Seq[LauncherData], page: Option[Int]): Ui[Any] =
    (workspaces <~ lwsDataCollections(data, page)) ~ reloadWorkspacePager

  override def reloadDockApps(dockApp: DockApp): Ui[Any] = dockAppsPanel <~ daplReload(dockApp)

  override def openModeEditWidgets(): Ui[Any] =
    uiVibrate() ~
      (dockAppsPanel <~ applyFadeOut()) ~
      (paginationPanel <~ applyFadeOut()) ~
      (topBarPanel <~ applyFadeOut()) ~
      (editWidgetsTopPanel <~ ewtInit <~ applyFadeIn()) ~
      (editWidgetsBottomPanel <~ ewbShowActions <~ applyFadeIn()) ~
      (workspaces <~ awsDisabled() <~ lwsShowRules <~ lwsReloadSelectedWidget) ~
      (drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd)

  override def reloadViewEditWidgets(): Ui[Any] =
    (editWidgetsTopPanel <~ ewtInit) ~
      (editWidgetsBottomPanel <~ ewbShowActions) ~
      (workspaces <~ lwsReloadSelectedWidget)

  override def closeModeEditWidgets(): Ui[Any] = {
    val collectionMoment = getData.headOption flatMap (_.moment) flatMap (_.collection)
    (dockAppsPanel <~ applyFadeIn()) ~
      (paginationPanel <~ applyFadeIn()) ~
      (topBarPanel <~ applyFadeIn()) ~
      (editWidgetsTopPanel <~ applyFadeOut()) ~
      (editWidgetsBottomPanel <~ applyFadeOut()) ~
      (workspaces <~ awsEnabled() <~ lwsHideRules() <~ lwsReloadSelectedWidget) ~
      (drawerLayout <~ dlUnlockedStart <~ (if (collectionMoment.isDefined) dlUnlockedEnd else Tweak.blank))
  }

  override def resizeWidget(): Ui[Any] =
    (workspaces <~ lwsResizeCurrentWidget()) ~
      (editWidgetsBottomPanel <~ ewbAnimateCursors) ~
      (editWidgetsTopPanel <~ ewtResizing)

  override def moveWidget(): Ui[Any] =
    (workspaces <~ lwsMoveCurrentWidget()) ~
      (editWidgetsBottomPanel <~ ewbAnimateCursors) ~
      (editWidgetsTopPanel <~ ewtMoving)

  override def arrowWidget(arrow: Arrow): Ui[Any] = workspaces <~ lwsArrowWidget(arrow)

  override def resizeWidgetById(id: Int, resize: ResizeWidgetRequest): Ui[Any] = workspaces <~ lwsResizeWidgetById(id, resize)

  override def moveWidgetById(id: Int, move: MoveWidgetRequest): Ui[Any] = workspaces <~ lwsMoveWidgetById(id, move)

  override def cancelWidget(appWidgetId: Int): Ui[Any] = Ui(appWidgetHost.deleteAppWidgetId(appWidgetId))

  override def editWidgetsShowActions(): Ui[Any] =
    (workspaces <~ lwsReloadSelectedWidget) ~
      (editWidgetsTopPanel <~ ewtInit) ~
      (editWidgetsBottomPanel <~ ewbAnimateActions)

  override def showAddItemMessage(nameCollection: String): Ui[Any] = showMessage(R.string.itemAddedToCollectionSuccessful, Seq(nameCollection))

  override def showWidgetCantResizeMessage(): Ui[Any] = showMessage(R.string.noResizeForWidget)

  override def showWidgetCantMoveMessage(): Ui[Any] = showMessage(R.string.noMoveForWidget)

  override def showWidgetNoHaveSpaceMessage(): Ui[Any] = showMessage(R.string.noSpaceForWidget)

  override def showContactUsError(): Ui[Any] = showMessage(R.string.contactUsError)

  override def showMinimumOneCollectionMessage(): Ui[Any] = showMessage(R.string.minimumOneCollectionMessage)

  override def showNoImplementedYetMessage(): Ui[Any] = showMessage(R.string.todo)

  override def showLoading(): Ui[Any] = showCollectionsLoading

  override def goToPreviousScreen(): Ui[Any] = {
    val canMoveToPreviousScreen = (workspaces ~> lwsCanMoveToPreviousScreen()).get getOrElse false
    goToPreviousWorkspace().ifUi(canMoveToPreviousScreen)
  }

  override def goToNextScreen(): Ui[Any] = {
    val canMoveToNextScreen = (workspaces ~> lwsCanMoveToNextScreen()).get getOrElse false
    goToNextWorkspace().ifUi(canMoveToNextScreen)
  }

  override def loadLauncherInfo(data: Seq[LauncherData], apps: Seq[DockApp]): Ui[Any] = {
    val momentType = data.headOption.flatMap(_.moment).flatMap(_.momentType)
    val launcherMoment = data.headOption.flatMap(_.moment)
    (loading <~ vGone) ~
      (appsMoment <~ (launcherMoment map amlPopulate getOrElse Tweak.blank)) ~
      (topBarPanel <~ (momentType map tblReloadMoment getOrElse Tweak.blank)) ~
      (dockAppsPanel <~ daplInit(apps)) ~
      (workspaces <~
        vGlobalLayoutListener(_ =>
          (workspaces <~
            lwsData(data, selectedPageDefault) <~
            (topBarPanel map (tbp => lwsAddPageChangedObserver(tbp.movement)) getOrElse Tweak.blank) <~
            awsAddPageChangedObserver(currentPage => {
              (paginationPanel <~ reloadPager(currentPage)).run
            })) ~
            createPager(selectedPageDefault)
        ))
  }

  override def reloadCurrentMoment(): Ui[Any] = workspaces <~ lwsDataForceReloadMoment()

  override def reloadMomentTopBar(): Ui[Any] = {
    val momentType = getData.headOption.flatMap(_.moment).flatMap(_.momentType)
    topBarPanel <~ (momentType map tblReloadMoment getOrElse Tweak.blank)
  }

  override def reloadMoment(data: LauncherData): Ui[Any] = {
    val momentType = data.moment.flatMap(_.momentType)
    val launcherMoment = data.moment
    (workspaces <~ lwsDataMoment(data)) ~
      (appsMoment <~ (launcherMoment map amlPopulate getOrElse Tweak.blank)) ~
      (topBarPanel <~ (momentType map tblReloadMoment getOrElse Tweak.blank))
  }

  override def reloadBarMoment(data: LauncherMoment): Ui[Any] =
    (appsMoment <~ amlPopulate(data)) ~ (drawerLayout <~ (data.collection match {
      case Some(_) => dlUnlockedEnd
      case None => dlLockedClosedEnd
    }))

  override def showUserProfile(email: Option[String], name: Option[String], avatarUrl: Option[String], coverPhotoUrl: Option[String]): Ui[Any] =
    userProfileMenu(email, name, avatarUrl, coverPhotoUrl)

  override def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any] =
    addApps(
      apps = apps,
      clickListener = (app: App) => presenter.openApp(app),
      longClickListener = (view: View, app: App) => {
        presenter.startAddItemToCollection(app)
        (view <~ startDrag()).run
      },
      getAppOrder = getAppOrder,
      counters = counters)

  override def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any] =
    addContacts(
      contacts = contacts,
      clickListener = (contact: Contact) => presenter.openContact(contact),
      longClickListener = (view: View, contact: Contact) => {
        presenter.startAddItemToCollection(contact)
        (view <~ startDrag()).run
      },
      counters = counters)

  override def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any] =
    addLastCallContacts(contacts, (contact: LastCallsContact) => presenter.openLastCall(contact))

  override def rippleToCollection(color: Int, point: Point): Ui[Future[Any]] = {
    val y = KitKat.ifSupportedThen(point.y - getStatusBarHeight) getOrElse point.y
    val background = new RippleCollectionDrawable(point.x, y, color)
    (foreground <~
      vVisible <~
      vBackground(background)) ~
      background.start()
  }

  override def resetFromCollection(): Ui[Any] = foreground <~ vBlankBackground <~ vGone

  override def editCollection(collection: Collection): Ui[Any] = showEditCollection(collection)

  override def editMoment(momentType: String): Ui[Any] = showEditMoment(momentType)

  override def addWidgets(widgets: Seq[AppWidget]): Ui[Any] = {
    val uiWidgets = widgets map { widget =>
      val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widget.appWidgetId)

      val widthContent = workspaces map (_.getWidth) getOrElse 0
      val heightContent = workspaces map (_.getHeight) getOrElse 0

      val cell = appWidgetInfo.getCell(widthContent, heightContent)

      Ui {
        val hostView = appWidgetHost.createView(activityContextWrapper.application, widget.appWidgetId, appWidgetInfo)
        hostView.setAppWidget(widget.appWidgetId, appWidgetInfo)
        (workspaces <~ lwsAddWidget(hostView, cell, widget)).run
      }
    }
    Ui.sequence(uiWidgets: _*)
  }

  override def clearWidgets(): Ui[Any] = workspaces <~ lwsClearWidgets()

  override def deleteSelectedWidget(): Ui[Any] = Ui {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        val dialog = new AlertDialogFragment(
          message = R.string.removeWidgetMessage,
          positiveAction = () => presenter.deleteDBWidget())
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  def unhostWidget(id: Int): Ui[Any] = workspaces <~ lwsUnhostWidget(id)

  override def hostWidget(widget: Widget): Ui[Any] = {
    val appWidgetId = appWidgetHost.allocateAppWidgetId()
    val provider = new ComponentName(widget.packageName, widget.className)
    val success = appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider)
    if (success) {
      Ui(presenter.configureOrAddWidget(Some(appWidgetId)))
    } else {
      val intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
      uiStartIntentForResult(intent, RequestCodes.goToWidgets)
    }
  }

  override def configureWidget(appWidgetId: Int): Ui[Any] = {
    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
    Option(appWidgetInfo.configure) match {
      case Some(configure) =>
        val intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
        intent.setComponent(configure)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        uiStartIntentForResult(intent, RequestCodes.goToConfigureWidgets)
      case _ => Ui(presenter.addWidget(Some(appWidgetId)))
    }
  }

  override def getWidgetInfoById(appWidgetId: Int): Option[(ComponentName, Cell)] =
    for {
      ws <- workspaces
      info <- Option(appWidgetManager.getAppWidgetInfo(appWidgetId))
    } yield (info.provider, info.getCell(ws.getWidth, ws.getHeight))

  override def showWidgetsDialog(): Ui[Any] = {
    val widthContent = workspaces map (_.getWidth) getOrElse 0
    val heightContent = workspaces map (_.getHeight) getOrElse 0
    val map = Map(
      WidgetsFragment.widgetContentWidth -> widthContent.toString,
      WidgetsFragment.widgetContentHeight -> heightContent.toString
    )
    showAction(f[WidgetsFragment], None, resGetColor(R.color.primary), map)
  }

  override def showSelectMomentDialog(): Ui[Any] = activityContextWrapper.original.get match {
    case Some(activity: Activity) => Ui {
      val momentDialog = new MomentDialog()
      momentDialog.show()
    }
    case _ => Ui.nop
  }

  override def openMenu(): Ui[Any] = drawerLayout <~ dlOpenDrawer

  override def openAppsMoment(): Ui[Any] =
    (drawerLayout ~> dlIsLockedClosedDrawerEnd) flatMap {
      case Some(false) => drawerLayout <~ dlOpenDrawerEnd
      case _ => Ui.nop
    }

  override def closeAppsMoment(): Ui[Any] = drawerLayout <~ dlCloseDrawerEnd

  override def back: Ui[Any] =
    if (presenter.statuses.mode == EditWidgetsMode) {
      Ui(presenter.statuses.transformation match {
        case Some(_) => presenter.backToActionEditWidgets()
        case _ => presenter.closeModeEditWidgets()
      })
    } else if (isDrawerTabsOpened) {
      closeDrawerTabs
    } else if (isMenuVisible) {
      closeMenu()
    } else if (isDrawerVisible) {
      revealOutDrawer
    } else if (isActionShowed) {
      unrevealActionFragment
    } else if (isCollectionMenuVisible) {
      closeCollectionMenu()
    } else {
      Ui.nop
    }

  override def resetAction: Ui[Any] = turnOffFragmentContent

  override def destroyAction: Ui[Any] = (actionFragmentContent <~ vBlankBackground) ~ Ui(removeActionFragment)

  override def logout: Ui[Any] = cleanWorkspaces() ~ Ui(presenter.goToWizard())

  override def closeTabs: Ui[Any] = closeDrawerTabs

  override def startReorder: Ui[Any] =
    (dockAppsPanel <~ applyFadeOut()) ~
      (topBarPanel <~ applyFadeOut()) ~
      (collectionActionsPanel <~ caplLoad(actionForCollections) <~ applyFadeIn()) ~
      reloadEdges()

  override def endReorder: Ui[Any] =
    (dockAppsPanel <~ applyFadeIn()) ~
      (topBarPanel <~ applyFadeIn()) ~
      (collectionActionsPanel <~~ applyFadeOut()) ~
      hideEdges()

  override def goToMomentWorkspace(): Ui[Any] = goToWorkspace(pageMoments)

  override def goToNextScreenReordering(): Ui[Any] = {
    val canMoveToNextScreen = (workspaces ~> lwsCanMoveToNextScreenOnlyCollections()).get getOrElse false
    (goToNextWorkspace() ~ (workspaces <~ lwsPrepareItemsScreenInReorder(0)) ~ reloadEdges()).ifUi(canMoveToNextScreen)
  }

  override def goToPreviousScreenReordering(): Ui[Any] = {
    val canMoveToPreviousScreen = (workspaces ~> lwsCanMoveToPreviousScreenOnlyCollections()).get getOrElse false
    (goToPreviousWorkspace() ~ (workspaces <~ lwsPrepareItemsScreenInReorder(numSpaces - 1)) ~ reloadEdges()).ifUi(canMoveToPreviousScreen)
  }

  override def startAddItem(cardType: CardType): Ui[Any] = {
    val isCollectionWorkspace = (workspaces ~> lwsIsCollectionWorkspace).get getOrElse false
    revealOutDrawer ~
      (topBarPanel <~ applyFadeOut()) ~
      (cardType match {
        case AppCardType => collectionActionsPanel <~ caplLoad(actionForApps) <~ applyFadeIn()
        case _ => Ui.nop
      }) ~
      reloadEdges() ~
      (if (isCollectionWorkspace) Ui.nop else goToWorkspace(pageCollections))
  }

  override def endAddItem: Ui[Any] =
    (topBarPanel <~ applyFadeIn()) ~
      (collectionActionsPanel <~~ applyFadeOut()) ~
      hideEdges()

  override def goToPreviousScreenAddingItem(): Ui[Any] = {
    val canMoveToPreviousScreen = (workspaces ~> lwsCanMoveToPreviousScreen()).get getOrElse false
    (goToPreviousWorkspace() ~ reloadEdges()).ifUi(canMoveToPreviousScreen)
  }

  override def goToNextScreenAddingItem(): Ui[Any] = {
    val canMoveToNextScreen = (workspaces ~> lwsCanMoveToNextScreen()).get getOrElse false
    (goToNextWorkspace() ~ reloadEdges()).ifUi(canMoveToNextScreen)
  }

  override def isTabsOpened: Boolean = isDrawerTabsOpened

  override def getData: Seq[LauncherData] = workspaces.map(_.data) getOrElse Seq.empty

  override def getCurrentPage: Option[Int] = workspaces.map(_.currentPage())

  override def canRemoveCollections: Boolean = getCountCollections > 1

  override def isWorkspaceScrolling: Boolean = workspaces exists(_.statuses.isScrolling)

  override def getCollectionsWithMoment(moments: Seq[Moment]): Seq[(NineCardsMoment, Option[Collection])] =
    moments map {
      case Moment(_, Some(collectionId: Int), _, _, _, Some(m: NineCardsMoment)) =>
        (m, getCollections.find(_.id == collectionId))
    }

  override def getCollection(position: Int): Option[Collection] = getCollections.lift(position)

  override def isEmptyCollectionsInWorkspace: Boolean = isEmptyCollections

  def turnOffFragmentContent: Ui[Any] = {
    val collectionMoment = getData.headOption flatMap (_.moment) flatMap (_.collection)
    (fragmentContent <~ vClickable(false)) ~
      (drawerLayout <~ dlUnlockedStart <~ (if (collectionMoment.isDefined) dlUnlockedEnd else Tweak.blank))
  }

  def reloadPager(currentPage: Int) = Transformer {
    case imageView: TintableImageView if imageView.isPosition(currentPage) =>
      imageView <~ vActivated(true) <~~ pagerAppear
    case imageView: TintableImageView =>
      imageView <~ vActivated(false)
  }

  private[this] def reloadEdges(): Ui[Any] = {
    val canMoveToNextScreen = (workspaces ~> lwsCanMoveToNextScreenOnlyCollections()).get getOrElse false
    val canMoveToPreviousScreen = (workspaces ~> lwsCanMoveToPreviousScreenOnlyCollections()).get getOrElse false
    (workspacesEdgeLeft <~ (if (canMoveToPreviousScreen) vVisible else vGone)) ~
      (workspacesEdgeRight <~ (if (canMoveToNextScreen) vVisible else vGone))
  }

  private[this] def hideEdges(): Ui[Any] =
    (workspacesEdgeLeft <~ vGone) ~
      (workspacesEdgeRight <~ vGone)

  private[this] def prepareBars =
    KitKat.ifSupportedThen {
      val activity = activityContextWrapper.getOriginal
      val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
      val sbHeight = getStatusBarHeight
      val nbHeight = getNavigationBarHeight
      val elevation = resGetDimensionPixelSize(R.dimen.elevation_fab_button)
      Ui(activity.getWindow.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) ~
        (content <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (menuCollectionRoot <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (editWidgetsBottomPanel <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (drawerContent <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (appsMoment <~ amlPaddingTopAndBottom(sbHeight, nbHeight)) ~
        (actionFragmentContent <~
          vPadding(paddingDefault, paddingDefault + sbHeight, paddingDefault, paddingDefault + nbHeight)) ~
        (drawerLayout <~ vBackground(R.drawable.background_workspace)) ~
        (Lollipop.ifSupportedThen {
          actionFragmentContent <~ vElevation(elevation)
        } getOrElse Ui.nop)
    } getOrElse Ui.nop

  private[this] def dragListener(): Tweak[View] = Tweak[View] { view =>
    view.setOnDragListener(new OnDragListener {
      val dragAreaKey = "drag-area"

      override def onDrag(v: View, event: DragEvent): Boolean = {
        val dragArea = v.getField[DragArea](dragAreaKey) getOrElse NoDragArea
        (event.getAction, (topBarPanel ~> height).get, (dockAppsPanel ~> height).get) match {
          case (_, Some(topBar), Some(bottomBar)) =>
            val height = KitKat.ifSupportedThen(view.getHeight - getStatusBarHeight) getOrElse view.getHeight
            val top = KitKat.ifSupportedThen(topBar + getStatusBarHeight) getOrElse topBar
            // Project location to views
            val x = event.getX
            val y = event.getY
            val currentDragArea = if (y < top) ActionsDragArea else if (y > height - bottomBar) DockAppsDragArea else WorkspacesDragArea

            val (action, area) = if (dragArea != currentDragArea) {
              (v <~ vAddField(dragAreaKey, currentDragArea)).run
              (ACTION_DRAG_EXITED, dragArea)
            } else {
              (event.getAction, currentDragArea)
            }

            (area, event.getLocalState, action) match {
              case (WorkspacesDragArea, DragObject(_, AddItemToCollection), _) =>
                // Project to workspace
                (workspaces <~ lwsDragAddItemDispatcher(action, x, y - top)).run
              case (DockAppsDragArea, DragObject(_, AddItemToCollection), _) =>
                // Project to dock apps
                (dockAppsPanel <~ daplDragDispatcher(action, x, y - (height - bottomBar))).run
              case (WorkspacesDragArea, DragObject(_, ReorderCollection), _) =>
                // Project to workspace
                (workspaces <~ lwsDragReorderCollectionDispatcher(action, x, y - top)).run
              case (DockAppsDragArea, DragObject(_, ReorderCollection), ACTION_DROP) =>
                // Project to workspace
                (workspaces <~ lwsDragReorderCollectionDispatcher(action, x, y - top)).run
              case (ActionsDragArea, DragObject(_, ReorderCollection), ACTION_DROP) =>
                // Project to Collection actions
                ((collectionActionsPanel <~ caplDragDispatcher(action, x, y)) ~
                  (workspaces <~ lwsDragReorderCollectionDispatcher(action, x, y - top))).run
              case (ActionsDragArea, _, _) =>
                // Project to Collection actions
                (collectionActionsPanel <~ caplDragDispatcher(action, x, y)).run
              case _ =>
            }
          case _ =>
        }
        true
      }
    })
  }

  private[this] def startDrag(): Tweak[View] = Tweak[View] { view =>
    val dragData = ClipData.newPlainText("", "")
    val shadow = new AppDrawerIconShadowBuilder(view)
    view.startDrag(dragData, shadow, DragObject(shadow, AddItemToCollection), 0)
  }

}

sealed trait DragArea

case object ActionsDragArea extends DragArea

case object WorkspacesDragArea extends DragArea

case object DockAppsDragArea extends DragArea

case object NoDragArea extends DragArea