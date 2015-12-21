package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent._
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{TranslationAnimator, TranslationY}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.WorkSpaceType._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{LauncherWorkSpaceCollectionsHolder, LauncherWorkSpaceWidgetsHolder}
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
    translation = TranslationY,
    update = (value: Float) => {
      workSpacesStatuses = workSpacesStatuses.copy(displacement = value)
      updateCanvas()
    },
    end = () => {
      resetVerticalScroll()
      Ui.nop
    }
  )

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = if (data.widgets) widgets else collections

  lazy val sizeCalculateMovement = getHeight

  def isWidgetScreen = data(statuses.currentItem).widgets

  def isWidgetScreen(page: Int) = data(page).widgets

  def isCollectionScreen = !isWidgetScreen

  def isCollectionScreen(page: Int) = !isWidgetScreen(page)

  def goToWizardScreen(toRight: Boolean): Boolean = data.lift(if (toRight) {
    statuses.currentItem - 1
  } else {
    statuses.currentItem + 1
  }) exists (_.widgets)

  override def createView(viewType: Int): LauncherWorkSpaceHolder = viewType match {
    case `widgets` => new LauncherWorkSpaceWidgetsHolder
    case `collections` => new LauncherWorkSpaceCollectionsHolder(statuses.dimen)
  }

  override def populateView(view: Option[LauncherWorkSpaceHolder], data: LauncherData, viewType: Int, position: Int): Ui[_] =
    view match {
      case Some(v: LauncherWorkSpaceCollectionsHolder) => v.populate(data.collections)
      case _ => Ui.nop
    }


  def performVerticalScroll(delta: Float): Ui[_] = {
    menuAnimator.cancel()
    workSpacesStatuses = workSpacesStatuses.updateDisplacement(sizeCalculateMovement, delta)
    updateCanvas()
  }

  def updateCanvas(): Ui[_] = {
    val percent = 1 - workSpacesStatuses.percent(sizeCalculateMovement)
    val tranform = workSpacesStatuses.displacement < 0 && percent > .5f
    if (tranform) {
      frontParentView <~ vScaleX(percent) <~ vScaleY(percent) <~ vAlpha(percent)
    } else {
      Ui.nop
    }
  }

  def resetVerticalScroll() = workSpacesStatuses = workSpacesStatuses.copy(openingMenu = false, displacement = 0)

  def finishVerticalMovement() = {
    val destiny = workSpacesStatuses.percent(sizeCalculateMovement) match {
      case d if d > .25f => -sizeCalculateMovement / 2
      case _ => 0
    }
    menuAnimator.move(workSpacesStatuses.displacement, destiny)
    menuAnimator.setDuration(durationAnimation)
    menuAnimator.start()
    invalidate()
  }

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    if (workSpacesStatuses.openingMenu) {
      val (action, x, y) = updateTouch(event)
      action match {
        case ACTION_MOVE =>
          requestDisallowInterceptTouchEvent(true)
          val deltaY = statuses.deltaY(y)
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          runUi(performVerticalScroll(deltaY))
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        case ACTION_CANCEL | ACTION_UP =>
          finishVerticalMovement()
        case _ =>
      }
      true
    } else {
      super.onInterceptTouchEvent(event)
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    if (workSpacesStatuses.openingMenu) {
      val (action, x, y) = updateTouch(event)
      action match {
        case ACTION_MOVE =>
          requestDisallowInterceptTouchEvent(true)
          val deltaY = statuses.deltaY(y)
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          runUi(performVerticalScroll(deltaY))
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        case ACTION_CANCEL | ACTION_UP =>
          finishVerticalMovement()
        case _ =>
      }
      true
    } else {
      super.onTouchEvent(event)
    }
  }

  override def setStateIfNeeded(x: Float, y: Float): Unit = {
    val xDiff = math.abs(x - statuses.lastMotionX)
    val yDiff = math.abs(y - statuses.lastMotionY)
    val up = y - statuses.lastMotionY < 0

    val yMoved = yDiff > touchSlop

    // We check that the user is doing up vertical swipe
    if (yMoved && up && (yDiff > xDiff)) {
      resetLongClick()
      workSpacesStatuses = workSpacesStatuses.copy(openingMenu = true)
    } else {
      super.setStateIfNeeded(x, y)
    }
  }

}

case class LauncherWorkSpacesStatuses(
  openingMenu: Boolean = false,
  displacement: Float = 0) {

  def updateDisplacement(size: Int, delta: Float): LauncherWorkSpacesStatuses =
    copy(displacement = math.max(-size, Math.min(size, displacement - delta)))

  def percent(size: Int): Float = math.abs(displacement) / size

}

object WorkSpaceType {
  val widgets = 0
  val collections = 1
}

class LauncherWorkSpaceHolder(implicit activityContext: ActivityContextWrapper)
  extends FrameLayout(activityContext.application)

case class LauncherData(widgets: Boolean, collections: Seq[Collection] = Seq.empty)

