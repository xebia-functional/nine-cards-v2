package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent._
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.TranslationAnimator
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{LauncherPresenter, LauncherWorkSpaceCollectionsHolder, LauncherWorkSpaceMomentsHolder}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class LauncherWorkSpaces(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  var presenter: Option[LauncherPresenter] = None

  var workSpacesStatuses = LauncherWorkSpacesStatuses()

  var workSpacesListener = LauncherWorkSpacesListener()

  val menuAnimator = new TranslationAnimator(
    update = (value: Float) => {
      workSpacesStatuses = workSpacesStatuses.copy(displacement = value)
      updateCanvasMenu()
    })

  lazy val sizeCalculateMovement = getHeight

  def getCountCollections: Int = data map {
    case item@LauncherData(CollectionsWorkSpace, _) => item.collections.length
    case _ => 0
  } sum

  def isEmptyCollections: Boolean = getCountCollections == 0

  def isMomentWorkSpace: Boolean = data(statuses.currentItem).workSpaceType.isMomentWorkSpace

  def isMomentWorkSpace(page: Int): Boolean = data(page).workSpaceType.isMomentWorkSpace

  def isCollectionWorkSpace: Boolean = !isMomentWorkSpace

  def isCollectionWorkSpace(page: Int): Boolean = !isMomentWorkSpace(page)

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = data.workSpaceType.value

  override def createView(viewType: Int): LauncherWorkSpaceHolder = WorkSpaceType(viewType) match {
    case MomentWorkSpace => new LauncherWorkSpaceMomentsHolder
    case CollectionsWorkSpace =>
      presenter map (p => new LauncherWorkSpaceCollectionsHolder(p, statuses.dimen)) getOrElse(throw new RuntimeException("Missing LauncherPresenter"))
  }

  override def populateView(view: Option[LauncherWorkSpaceHolder], data: LauncherData, viewType: Int, position: Int): Ui[_] =
    view match {
      case Some(v: LauncherWorkSpaceCollectionsHolder) => v.populate(data.collections)
      case _ => Ui.nop
    }

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    val (action, x, y) = updateTouch(event)
    if (workSpacesStatuses.openingMenu) {
      action match {
        case ACTION_MOVE =>
          requestDisallowInterceptTouchEvent(true)
          val deltaY = statuses.deltaY(y)
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          performMenuMovement(deltaY).run
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
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
          val deltaY = statuses.deltaY(y)
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          performMenuMovement(deltaY).run
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
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
    // We check that the user is doing up vertical swipe
    if (isVerticalMoving(x, y)) {
      workSpacesListener.onStartOpenMenu().run
      resetLongClick()
      workSpacesStatuses = workSpacesStatuses.copy(openingMenu = true)
    } else {
      super.setStateIfNeeded(x, y)
    }
  }

  def closeMenu(): Ui[_] = if (workSpacesStatuses.openedMenu) {
    setOpenedMenu(false)
    animateViewsMenuMovement(0, durationAnimation)
  } else Ui.nop

  private[this] def checkResetMenuOpened(action: Int, x: Float, y: Float) = {
    action match {
      case ACTION_DOWN =>
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
      case ACTION_MOVE =>
        if (!statuses.enabled) {
          if (isVerticalMoving(x, y)) {
            resetLongClick()
            statuses = statuses.copy(enabled = true)
            workSpacesStatuses = workSpacesStatuses.copy(openingMenu = true)
          }
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        }
      case _ =>
    }
  }

  private[this] def isVerticalMoving(x: Float, y: Float): Boolean = {
    val xDiff = math.abs(x - statuses.lastMotionX)
    val yDiff = math.abs(y - statuses.lastMotionY)

    val rightDirection = (workSpacesStatuses.openedMenu, y - statuses.lastMotionY < 0) match {
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
        (frontParentView <~ vScaleX(updatePercent) <~ vScaleY(updatePercent) <~ vAlpha(updatePercent))
    } else {
      Ui.nop
    }
  }

  private[this] def resetMenuMovement(): Ui[_] = {
    workSpacesStatuses = workSpacesStatuses.copy(openingMenu = false)
    workSpacesListener.onEndOpenMenu(workSpacesStatuses.openedMenu)
  }

  private[this] def animateViewsMenuMovement(dest: Int, duration: Int): Ui[_] =
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

  private[this] def computeFlingMenuMovement() = statuses.velocityTracker foreach {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      (if (math.abs(tracker.getYVelocity) > minimumVelocity)
        snapMenuMovement(tracker.getYVelocity)
      else
        snapDestinationMenuMovement()).run
      tracker.recycle()
      statuses = statuses.copy(velocityTracker = None)
  }

  private[this] def setOpenedMenu(openedMenu: Boolean): Unit = {
    workSpacesStatuses = workSpacesStatuses.copy(openedMenu = openedMenu)
    statuses = statuses.copy(enabled = !openedMenu)
  }

}

case class LauncherWorkSpacesStatuses(
  openingMenu: Boolean = false,
  openedMenu: Boolean = false,
  displacement: Float = 0) {

  def updateDisplacement(size: Int, delta: Float): LauncherWorkSpacesStatuses =
    copy(displacement = math.max(-size, Math.min(size, displacement - delta)))

  def percent(size: Int): Float = math.abs(displacement) / size

}

case class LauncherWorkSpacesListener(
  onStartOpenMenu: () => Ui[_] = () => Ui.nop,
  onUpdateOpenMenu: (Float) => Ui[_] = (f) => Ui.nop,
  onEndOpenMenu: (Boolean) => Ui[_] = (b) => Ui.nop)

class LauncherWorkSpaceHolder(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.application)

case class LauncherData(workSpaceType: WorkSpaceType, collections: Seq[Collection] = Seq.empty)

sealed trait WorkSpaceType {
  val value: Int

  def isMomentWorkSpace: Boolean = this == MomentWorkSpace
}

case object MomentWorkSpace extends WorkSpaceType {
  override val value: Int = 0
}

case object CollectionsWorkSpace extends WorkSpaceType {
  override val value: Int = 1
}

object WorkSpaceType {
  def apply(value: Int): WorkSpaceType = value match {
    case MomentWorkSpace.value => MomentWorkSpace
    case _ => CollectionsWorkSpace
  }
}

