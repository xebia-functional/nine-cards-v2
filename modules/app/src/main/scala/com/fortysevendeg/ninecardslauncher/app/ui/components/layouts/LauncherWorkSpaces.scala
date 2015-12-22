package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent._
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.TranslationAnimator
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{LauncherWorkSpaceCollectionsHolder, LauncherWorkSpaceMomentsHolder}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

class LauncherWorkSpaces(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit activityContext: ActivityContextWrapper)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, attr, defStyleAttr) {

  def this(context: Context)(implicit activityContext: ActivityContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit activityContext: ActivityContextWrapper) = this(context, attr, 0)

  var workSpacesStatuses = LauncherWorkSpacesStatuses()

  val menuAnimator = new TranslationAnimator(
    update = (value: Float) => {
      workSpacesStatuses = workSpacesStatuses.copy(displacement = value)
      updateCanvasMenu()
    },
    end = () => {
      resetMenuMovement()
      Ui.nop
    }
  )

  lazy val sizeCalculateMovement = getHeight

  def isMomentWorkSpace = data(statuses.currentItem).workSpaceType.isMomentWorkSpace

  def isMomentWorkSpace(page: Int) = data(page).workSpaceType.isMomentWorkSpace

  def isCollectionWorkSpace = !isMomentWorkSpace

  def isCollectionWorkSpace(page: Int) = !isMomentWorkSpace(page)

  def goToMomentWorkSpace(toRight: Boolean): Boolean = data.lift(if (toRight) {
    statuses.currentItem - 1
  } else {
    statuses.currentItem + 1
  }) exists (_.workSpaceType.isMomentWorkSpace)

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = data.workSpaceType.value

  override def createView(viewType: Int): LauncherWorkSpaceHolder = WorkSpaceType(viewType) match {
    case MomentWorkSpace => new LauncherWorkSpaceMomentsHolder
    case CollectionsWorkSpace => new LauncherWorkSpaceCollectionsHolder(statuses.dimen)
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
          runUi(performMenuMovement(deltaY))
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        case ACTION_CANCEL | ACTION_UP =>
          computeFlingMenuMovement()
        case _ =>
      }
      true
    } else {
      checkResetMenuOpened(x, y)
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
          runUi(performMenuMovement(deltaY))
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        case ACTION_CANCEL | ACTION_UP =>
          computeFlingMenuMovement()
        case _ =>
      }
      true
    } else {
      checkResetMenuOpened(x, y)
      super.onTouchEvent(event)
    }
  }

  override def setStateIfNeeded(x: Float, y: Float): Unit = {
    // We check that the user is doing up vertical swipe
    if (isVerticalMoving(x, y)) {
      resetLongClick()
      workSpacesStatuses = workSpacesStatuses.copy(openingMenu = true)
    } else {
      super.setStateIfNeeded(x, y)
    }
  }

  private[this] def checkResetMenuOpened(x: Float, y: Float) = if (!statuses.enabled) {
    if (isVerticalMoving(x, y)) {
      statuses = statuses.copy(enabled = true)
      workSpacesStatuses = workSpacesStatuses.copy(openingMenu = true)
    }
    statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
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
    val percent = 1 - workSpacesStatuses.percent(sizeCalculateMovement)
    val transform = workSpacesStatuses.displacement < 0 && percent > .5f
    if (transform) {
      frontParentView <~ vScaleX(percent) <~ vScaleY(percent) <~ vAlpha(percent)
    } else {
      Ui.nop
    }
  }

  private[this] def resetMenuMovement() = workSpacesStatuses = workSpacesStatuses.copy(openingMenu = false)

  private[this] def animateViewsMenuMovement(dest: Int, duration: Int) = {
    menuAnimator.move(workSpacesStatuses.displacement, dest)
    menuAnimator.setDuration(duration)
    menuAnimator.start()
    invalidate()
  }

  private[this] def snapMenuMovement(velocity: Float): Unit = {
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

  private[this] def snapDestinationMenuMovement(): Unit = {
    val destiny = workSpacesStatuses.percent(sizeCalculateMovement) match {
      case d if d > .25f =>
        setOpenedMenu(true)
        -sizeCalculateMovement / 2
      case _ =>
        setOpenedMenu(false)
        0
    }
    animateViewsMenuMovement(destiny, durationAnimation)
    invalidate()
  }

  private[this] def computeFlingMenuMovement() = statuses.velocityTracker foreach {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      if (math.abs(tracker.getYVelocity) > minimumVelocity) snapMenuMovement(tracker.getYVelocity) else snapDestinationMenuMovement()
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

class LauncherWorkSpaceHolder(implicit activityContext: ActivityContextWrapper)
  extends FrameLayout(activityContext.application)

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

