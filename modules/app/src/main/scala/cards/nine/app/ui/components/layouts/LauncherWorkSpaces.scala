package cards.nine.app.ui.components.layouts

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent._
import android.widget.FrameLayout
import cards.nine.app.ui.commons.AnimationsUtils._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.components.commons.TranslationAnimator
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, MomentWorkSpace, WorkSpaceType}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.holders.{LauncherWorkSpaceCollectionsHolder, LauncherWorkSpaceMomentsHolder}
import cards.nine.app.ui.launcher.jobs.WidgetsJobs
import cards.nine.app.ui.launcher.{LauncherActivity, LauncherPresenter}
import cards.nine.commons.javaNull
import cards.nine.models.{Collection, Widget}
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class LauncherWorkSpaces(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  // TODO First implementation in order to remove LauncherPresenter
  implicit def presenter: LauncherPresenter = context match {
    case activity: LauncherActivity => activity.presenter
    case _ => throw new RuntimeException("LauncherPresenter not found")
  }

  implicit def widgetJobs: WidgetsJobs = context match {
    case activity: LauncherActivity => activity.widgetJobs
    case _ => throw new RuntimeException("WidgetsJobs not found")
  }

  implicit def theme: NineCardsTheme = context match {
    case activity: LauncherActivity => activity.theme
    case _ => throw new RuntimeException("NineCardsTheme not found")
  }

  var workSpacesStatuses = LauncherWorkSpacesStatuses()

  var workSpacesListener = LauncherWorkSpacesListener()

  val menuAnimator = new TranslationAnimator(
    update = (value: Float) => {
      workSpacesStatuses = workSpacesStatuses.copy(displacement = value)
      updateCanvasMenu()
    })

  lazy val sizeCalculateMovement = getHeight

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

  def showRulesInMoment(): Unit = uiWithView(_.createRules)

  def hideRulesInMoment(): Unit = uiWithView(_.removeRules())

  def reloadSelectedWidget(): Unit = uiWithView(_.reloadSelectedWidget)

  def resizeCurrentWidget(): Unit = uiWithView(_.resizeCurrentWidget)

  def moveCurrentWidget(): Unit = uiWithView(_.moveCurrentWidget)

  def resizeWidgetById(id: Int, increaseX: Int, increaseY: Int): Unit = uiWithView(_.resizeWidgetById(id, increaseX, increaseY))

  def moveWidgetById(id: Int, displaceX: Int, displaceY: Int): Unit = uiWithView(_.moveWidgetById(id, displaceX, displaceY))

  private[this] def uiWithView(f: (LauncherWorkSpaceMomentsHolder) => Ui[_]) = getView(0) match {
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

  override def populateView(view: Option[LauncherWorkSpaceHolder], data: LauncherData, viewType: Int, position: Int): Ui[_] =
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

  def closeMenu(): Ui[Future[Any]] = if (workSpacesStatuses.openedMenu) {
    setOpenedMenu(false)
    animateViewsMenuMovement(0, durationAnimation)
  } else Ui(Future.successful(()))

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

  private[this] def performMenuMovement(delta: Float): Ui[_] = {
    menuAnimator.cancel()
    workSpacesStatuses = workSpacesStatuses.updateDisplacement(sizeCalculateMovement, delta)
    updateCanvasMenu()
  }

  private[this] def updateCanvasMenu(): Ui[_] = {
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

  private[this] def resetMenuMovement(): Ui[_] = {
    workSpacesStatuses = workSpacesStatuses.reset()
    workSpacesListener.onEndOpenMenu(workSpacesStatuses.openedMenu)
  }

  private[this] def animateViewsMenuMovement(dest: Int, duration: Int): Ui[Future[Any]] =
    (this <~
      vInvalidate <~~
      menuAnimator.move(workSpacesStatuses.displacement, dest, duration)) ~~ resetMenuMovement()

  private[this] def snapMenuMovement(velocity: Float): Ui[_] = {
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

  private[this] def snapDestinationMenuMovement(): Ui[_] = {
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
  onStartOpenMenu: () => Ui[_] = () => Ui.nop,
  onUpdateOpenMenu: (Float) => Ui[_] = (f) => Ui.nop,
  onEndOpenMenu: (Boolean) => Ui[_] = (b) => Ui.nop)

class LauncherWorkSpaceHolder(context: Context)
  extends FrameLayout(context)


