package cards.nine.app.ui.components.layouts

import android.app.WallpaperManager
import android.appwidget.AppWidgetHostView
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent._
import android.widget.FrameLayout
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.components.commons.TranslationAnimator
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, MomentWorkSpace, WorkSpaceType}
import cards.nine.app.ui.launcher.LauncherActivity
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.holders.{LauncherWorkSpaceCollectionsHolder, LauncherWorkSpaceMomentsHolder}
import cards.nine.app.ui.launcher.jobs.{DragJobs, NavigationJobs, WidgetsJobs}
import cards.nine.app.ui.preferences.commons.{AppearBehindWorkspaceAnimation, HorizontalSlideWorkspaceAnimation, WallpaperAnimation}
import cards.nine.commons.javaNull
import cards.nine.models.{Collection, NineCardsTheme, Widget}
import macroid._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class LauncherWorkSpaces(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  implicit def navigationJobs: NavigationJobs = context match {
    case activity: LauncherActivity => activity.navigationJobs
    case _ => throw new RuntimeException("NavigationJobs not found")
  }

  implicit def dragJobs: DragJobs = context match {
    case activity: LauncherActivity => activity.dragJobs
    case _ => throw new RuntimeException("DragJobs not found")
  }

  implicit def widgetJobs: WidgetsJobs = context match {
    case activity: LauncherActivity => activity.widgetJobs
    case _ => throw new RuntimeException("WidgetsJobs not found")
  }

  implicit def theme: NineCardsTheme = statuses.theme

  lazy val canAnimateWallpaper = WallpaperAnimation.readValue

  lazy val wallpaperManager: WallpaperManager = WallpaperManager.getInstance(context)

  lazy val windowToken = getWindowToken

  var workSpacesStatuses = LauncherWorkSpacesStatuses()

  var workSpacesListener = LauncherWorkSpacesListener()

  val menuAnimator = new TranslationAnimator(
    update = (value: Float) => {
      workSpacesStatuses = workSpacesStatuses.copy(displacement = value)
      updateCanvasMenu()
    })

  lazy val sizeCalculateMovement = getHeight

  override def init(newData: Seq[LauncherData], position: Int = 0, forcePopulatePosition: Option[Int] = None): Unit = {
    super.init(newData, position, forcePopulatePosition)
    updateWallpaper().run
  }

  def reloadMoment(moment: LauncherData): Unit = {
    data = moment +: data.drop(1)
    resetItem(0).run
  }

  def getCountCollections: Int = data map {
    case item@LauncherData(CollectionsWorkSpace, _, _, _) => item.collections.length
    case _ => 0
  } sum

  def getCollections: Seq[Collection] = data flatMap (a => a.collections)

  def isEmptyCollections: Boolean = getCountCollections == 0

  def isMomentWorkSpace: Boolean = data(animatedWorkspaceStatuses.currentItem).workSpaceType.isMomentWorkSpace

  def isMomentWorkSpace(page: Int): Boolean = data(page).workSpaceType.isMomentWorkSpace

  def isCollectionWorkSpace: Boolean = !isMomentWorkSpace

  def isCollectionWorkSpace(page: Int): Boolean = !isMomentWorkSpace(page)

  def getWidgets: Seq[Widget] = getView(0) match {
    case (Some(momentWorkSpace: LauncherWorkSpaceMomentsHolder)) => momentWorkSpace.getWidgets
    case _ => Seq.empty
  }

  def changeCollectionInMoment(collection: Option[Collection]): Unit = {
    data.headOption match {
      case Some(momentLauncherData) =>
        val collectionsLauncherData = data.filter(_.workSpaceType == CollectionsWorkSpace)
        val newMoment = momentLauncherData.moment map ( _.copy(collection = collection))
        data = momentLauncherData.copy(moment = newMoment) +: collectionsLauncherData
      case _ =>
    }
  }

  def nextScreen: Option[Int] = {
    val current = animatedWorkspaceStatuses.currentItem
    if (current + 1 < getWorksSpacesCount) Some(current + 1) else None
  }

  def previousScreen: Option[Int] = {
    val current = animatedWorkspaceStatuses.currentItem
    if (current > 0) Some(current - 1) else None
  }

  def prepareItemsScreenInReorder(position: Int): Ui[Any] = getCurrentView match {
    case Some(collectionWorkspace: LauncherWorkSpaceCollectionsHolder) =>
      collectionWorkspace.prepareItemsScreenInReorder(position)
    case _ => Ui.nop
  }

  def addWidget(widgetView: AppWidgetHostView, cell: Cell, widget: Widget): Unit = getView(0) match {
    case (Some(momentWorkSpace: LauncherWorkSpaceMomentsHolder)) =>
      momentWorkSpace.addWidget(widgetView, cell, widget).run
    case None =>
      // The first time it`s possible that the workspace isn't created. In this case we wait 200 millis for launching again
      uiHandlerDelayed(Ui(addWidget(widgetView, cell, widget)), 200).run
    case _ =>
  }

  def addNoConfiguredWidget(wCell: Int, hCell: Int, widget: Widget): Unit = getView(0) match {
    case (Some(momentWorkSpace: LauncherWorkSpaceMomentsHolder)) =>
      momentWorkSpace.addNoConfiguredWidget(wCell, hCell, widget).run
    case None =>
      // The first time it`s possible that the workspace isn't created. In this case we wait 200 millis for launching again
      uiHandlerDelayed(Ui(addNoConfiguredWidget(wCell, hCell, widget)), 200).run
    case _ =>
  }

  def addReplaceWidget(widgetView: AppWidgetHostView, wCell: Int, hCell: Int, widget: Widget): Unit = getView(0) match {
    case (Some(momentWorkSpace: LauncherWorkSpaceMomentsHolder)) =>
      momentWorkSpace.addReplaceWidget(widgetView, wCell, hCell, widget).run
    case _ =>
  }

  def clearWidgets(): Unit = uiWithView(_.clearWidgets)

  def unhostWidget(id: Int): Unit = uiWithView(_.unhostWiget(id))

  def startEditWidget(): Unit = uiWithView(_.startEditWidget())

  def closeEditWidget(): Unit = uiWithView(_.closeEditWidget())

  def reloadSelectedWidget(): Unit = uiWithView(_.reloadSelectedWidget)

  def resizeCurrentWidget(): Unit = uiWithView(_.resizeCurrentWidget)

  def moveCurrentWidget(): Unit = uiWithView(_.moveCurrentWidget)

  def resizeWidgetById(id: Int, increaseX: Int, increaseY: Int): Unit = uiWithView(_.resizeWidgetById(id, increaseX, increaseY))

  def moveWidgetById(id: Int, displaceX: Int, displaceY: Int): Unit = uiWithView(_.moveWidgetById(id, displaceX, displaceY))

  private[this] def uiWithView(f: (LauncherWorkSpaceMomentsHolder) => Ui[Any]) = getView(0) match {
    case (Some(momentWorkSpace: LauncherWorkSpaceMomentsHolder)) => f(momentWorkSpace).run
    case _ =>
  }

  def openMenu(): Unit = {
    workSpacesStatuses = workSpacesStatuses.startLaunchedOpen()
    (uiVibrate() ~
      workSpacesListener.onStartOpenMenu() ~
      (this <~
        vInvalidate <~~
        menuAnimator.move(0, -sizeCalculateMovement / 2)) ~~
      resetMenuMovement()).run
  }

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = data.workSpaceType.value

  override def createEmptyView(): LauncherWorkSpaceHolder = new LauncherWorkSpaceHolder(context)

  override def createView(viewType: Int): LauncherWorkSpaceHolder =
    WorkSpaceType(viewType) match {
      case MomentWorkSpace =>
        new LauncherWorkSpaceMomentsHolder(context, animatedWorkspaceStatuses.dimen)
      case CollectionsWorkSpace =>
        new LauncherWorkSpaceCollectionsHolder(context, animatedWorkspaceStatuses.dimen)
    }

  override def populateView(view: Option[LauncherWorkSpaceHolder], data: LauncherData, viewType: Int, position: Int): Ui[Any] =
    view match {
      case Some(v: LauncherWorkSpaceCollectionsHolder) =>
        v.populate(data.collections, data.positionByType)
      case Some(v: LauncherWorkSpaceMomentsHolder) =>
        data.moment map v.populate getOrElse Ui.nop
      case _ => Ui.nop
    }

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    val (action, x, y) = updateTouch(event)
    if (workSpacesStatuses.openingMenu) {
      action match {
        case ACTION_MOVE =>
          requestDisallowInterceptTouchEvent(true)
          val deltaY = animatedWorkspaceStatuses.deltaY(y)
          animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
          performMenuMovement(deltaY).run
        case ACTION_DOWN =>
          animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
        case ACTION_CANCEL | ACTION_UP =>
          computeFlingMenuMovement()
        case _ =>
      }
      true
    } else {
      checkResetMenuOpened(action, x, y)
      super.onInterceptTouchEvent(event)
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    val (action, x, y) = updateTouch(event)
    if (workSpacesStatuses.openingMenu) {
      action match {
        case ACTION_MOVE =>
          requestDisallowInterceptTouchEvent(true)
          val deltaY = animatedWorkspaceStatuses.deltaY(y)
          animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
          performMenuMovement(deltaY).run
        case ACTION_DOWN =>
          animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
        case ACTION_CANCEL | ACTION_UP =>
          computeFlingMenuMovement()
        case _ =>
      }
      true
    } else {
      checkResetMenuOpened(action, x, y)
      super.onTouchEvent(event)
    }
  }

  override def setStateIfNeeded(x: Float, y: Float): Unit = {
    val touchingWidget = statuses.touchingWidget
    // We check that the user is doing up vertical swipe
    // If the user is touching a widget, we don't do a vertical movement in order to the
    // scrollable widgets works fine
    if (isVerticalMoving(x, y) && !touchingWidget && animatedWorkspaceStatuses.enabled) {
      workSpacesListener.onStartOpenMenu().run
      workSpacesStatuses = workSpacesStatuses.start()
    } else {
      super.setStateIfNeeded(x, y)
    }
  }

  override def applyTransforms(): Ui[Any] = {
    updateWallpaper() ~ transformOutPanel() ~ transformInPanel()
  }

  def closeMenu(): Ui[Future[Any]] = if (workSpacesStatuses.openedMenu) {
    setOpenedMenu(false)
    animateViewsMenuMovement(0, durationAnimation)
  } else Ui(Future.successful(()))

  private[this] def transformOutPanel(): Ui[Any] = {
    val percent = animatedWorkspaceStatuses.percent(getSizeWidget)
    getFrontView <~ ((animationPref, animatedWorkspaceStatuses.isFromLeft) match {
      case (HorizontalSlideWorkspaceAnimation, _) =>
        vTranslationX(animatedWorkspaceStatuses.displacement)
      case (AppearBehindWorkspaceAnimation, true) =>
        val alpha = 1 - percent
        val scale = .5f + (alpha / 2)
        vScaleX(scale) + vScaleY(scale) + vAlpha(alpha)
      case (AppearBehindWorkspaceAnimation, false) =>
        vTranslationX(animatedWorkspaceStatuses.displacement)
    })
  }

  private[this] def transformInPanel(): Ui[Any] = {
    val percent = animatedWorkspaceStatuses.percent(getSizeWidget)
    val fromLeft = animatedWorkspaceStatuses.isFromLeft
    val view = if (fromLeft) getPreviousView else getNextView
    notifyMovementObservers(percent)

    view <~ ((animationPref, fromLeft) match {
      case (HorizontalSlideWorkspaceAnimation, _) =>
        val translate = {
          val start = if (fromLeft) -getSizeWidget else getSizeWidget
          start - (start * percent)
        }
        vTranslationX(translate)
      case (AppearBehindWorkspaceAnimation, true) =>
        val translate = {
          val start = -getSizeWidget
          start - (start * percent)
        }
        vTranslationX(translate)
      case (AppearBehindWorkspaceAnimation, false) =>
        val scale = .5f + (percent / 2)
        vTranslationX(0) + vScaleX(scale) + vScaleY(scale) + vAlpha(percent)
    })

  }

  private[this] def updateWallpaper(): Ui[Any] = if (canAnimateWallpaper) Ui {
    wallpaperManager.setWallpaperOffsets(
      windowToken,
      animatedWorkspaceStatuses.totalXPercent(getSizeWidget, data.length),
      0.5f)
  } else Ui.nop

  private[this] def checkResetMenuOpened(action: Int, x: Float, y: Float) = {
    action match {
      case ACTION_DOWN =>
        statuses = statuses.copy(touchingWidget = false)
        animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
      case _ =>
    }
  }

  private[this] def isVerticalMoving(x: Float, y: Float): Boolean = {
    val xDiff = math.abs(x - animatedWorkspaceStatuses.lastMotionX)
    val yDiff = math.abs(y - animatedWorkspaceStatuses.lastMotionY)

    val rightDirection = (workSpacesStatuses.openedMenu, y - animatedWorkspaceStatuses.lastMotionY < 0) match {
      case (false, true) => true
      case (true, false) => true
      case _ => false
    }

    val yMoved = yDiff > touchSlop
    yMoved && rightDirection && (yDiff > xDiff)
  }

  private[this] def performMenuMovement(delta: Float): Ui[Any] = {
    menuAnimator.cancel()
    workSpacesStatuses = workSpacesStatuses.updateDisplacement(sizeCalculateMovement, delta)
    updateCanvasMenu()
  }

  private[this] def updateCanvasMenu(): Ui[Any] = {
    val percent = workSpacesStatuses.percent(sizeCalculateMovement)
    val updatePercent = 1 - workSpacesStatuses.percent(sizeCalculateMovement)
    val transform = workSpacesStatuses.displacement < 0 && updatePercent > .5f
    if (transform) {
      workSpacesListener.onUpdateOpenMenu(percent * 2) ~
        (getFrontView <~ vScaleX(updatePercent) <~ vScaleY(updatePercent) <~ vAlpha(updatePercent))
    } else {
      Ui.nop
    }
  }

  private[this] def resetMenuMovement(): Ui[Any] = {
    workSpacesStatuses = workSpacesStatuses.reset()
    workSpacesListener.onEndOpenMenu(workSpacesStatuses.openedMenu)
  }

  private[this] def animateViewsMenuMovement(dest: Int, duration: Int): Ui[Future[Any]] =
    (this <~
      vInvalidate <~~
      menuAnimator.move(workSpacesStatuses.displacement, dest, duration)) ~~ resetMenuMovement()

  private[this] def snapMenuMovement(velocity: Float): Ui[Any] = {
    moveItemsAnimator.cancel()
    val destiny = (velocity, workSpacesStatuses.displacement) match {
      case (v, d) if v <= 0 && d < 0 =>
        setOpenedMenu(true)
        -sizeCalculateMovement / 2
      case _ =>
        setOpenedMenu(false)
        0
    }
    animateViewsMenuMovement(destiny, calculateDurationByVelocity(velocity, durationAnimation))
  }

  private[this] def snapDestinationMenuMovement(): Ui[Any] = {
    val destiny = workSpacesStatuses.percent(sizeCalculateMovement) match {
      case d if d > .25f =>
        setOpenedMenu(true)
        -sizeCalculateMovement / 2
      case _ =>
        setOpenedMenu(false)
        0
    }
    animateViewsMenuMovement(destiny, durationAnimation)
  }

  private[this] def computeFlingMenuMovement() = animatedWorkspaceStatuses.velocityTracker foreach {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      (if (math.abs(tracker.getYVelocity) > minimumVelocity)
        snapMenuMovement(tracker.getYVelocity)
      else
        snapDestinationMenuMovement()).run
      tracker.recycle()
      animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(velocityTracker = None)
  }

  private[this] def setOpenedMenu(openedMenu: Boolean): Unit = {
    workSpacesStatuses = workSpacesStatuses.copy(openedMenu = openedMenu)
    animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(enabled = !openedMenu)
  }

}

case class LauncherWorkSpacesStatuses(
  openingMenu: Boolean = false,
  openedMenu: Boolean = false,
  displacement: Float = 0) {

  def updateDisplacement(size: Int, delta: Float): LauncherWorkSpacesStatuses =
    copy(displacement = math.max(-size, Math.min(size, displacement - delta)))

  def percent(size: Int): Float = math.abs(displacement) / size

  def start(): LauncherWorkSpacesStatuses = copy(openingMenu = true)

  def startLaunchedOpen(): LauncherWorkSpacesStatuses = copy(openedMenu = true)

  def reset(): LauncherWorkSpacesStatuses = copy(openingMenu = false)

}

case class LauncherWorkSpacesListener(
  onStartOpenMenu: () => Ui[Any] = () => Ui.nop,
  onUpdateOpenMenu: (Float) => Ui[Any] = (f) => Ui.nop,
  onEndOpenMenu: (Boolean) => Ui[Any] = (b) => Ui.nop)

class LauncherWorkSpaceHolder(context: Context)
  extends FrameLayout(context)


