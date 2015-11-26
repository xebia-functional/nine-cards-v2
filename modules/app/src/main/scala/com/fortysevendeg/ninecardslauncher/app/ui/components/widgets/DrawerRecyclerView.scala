package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.{MotionEvent, ViewConfiguration}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.ScrollableManager
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{Scrolling, Stopped, ViewState}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.SearchBoxAnimatedController
import macroid.ContextWrapper
import macroid.FullDsl._

class DrawerRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var animatedController: Option[SearchBoxAnimatedController] = None

  var states = DrawerRecyclerStates()

  val touchSlop = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
  }

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = states.disableScroll || super.dispatchTouchEvent(ev)

  addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
    override def onTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Unit = {
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      animatedController foreach (_.initVelocityTracker(event))
      (MotionEventCompat.getActionMasked(event), states.touchState) match {
        case (ACTION_MOVE, Scrolling) =>
          requestDisallowInterceptTouchEvent(true)
          val delta = states.deltaX(x)
          states = states.copy(lastMotionX = x, lastMotionY = y)
          animatedController foreach { controller =>
            runUi(controller.movementByOverScroll(delta))
          }
        case (ACTION_MOVE, Stopped) =>
          setStateIfNeeded(x, y)
        case (ACTION_DOWN, _) =>
          states = states.copy(lastMotionX = x, lastMotionY = y)
        case (ACTION_CANCEL | ACTION_UP, _) =>
          animatedController foreach (_.computeFling())
          states = states.copy(touchState = Stopped)
          blockScroll(false)
        case _ =>
      }
    }

    override def onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean = {
      animatedController foreach (_.initVelocityTracker(event))
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      (MotionEventCompat.getActionMasked(event), states.touchState) match {
        case (ACTION_MOVE, Scrolling) =>
          requestDisallowInterceptTouchEvent(true)
          true
        case (ACTION_MOVE, _) =>
          setStateIfNeeded(x, y)
          states.touchState != Stopped
        case (ACTION_DOWN, _) =>
          states = states.copy(lastMotionX = x, lastMotionY = y)
          false
        case (ACTION_CANCEL | ACTION_UP, _) =>
          animatedController foreach (_.computeFling())
          states = states.copy(touchState = Stopped)
          blockScroll(false)
          states.touchState != Stopped
        case _ => states.touchState != Stopped
      }
    }

    override def onRequestDisallowInterceptTouchEvent(b: Boolean): Unit = {

    }
  })

  private[this] def setStateIfNeeded(x: Float, y: Float) = {
    val xDiff = math.abs(x - states.lastMotionX)
    val yDiff = math.abs(y - states.lastMotionY)

    val xMoved = xDiff > touchSlop

    if (xMoved) {
      val isScrolling = xDiff > yDiff
      if (isScrolling) {
        animatedController foreach (controller => runUi(controller.startMovement))
        states = states.copy(touchState = Scrolling)
        blockScroll(true)
      }
      states = states.copy(lastMotionX = x, lastMotionY = y)
    }
  }

  private[this] def blockScroll(bs: Boolean) = getLayoutManager match {
    case lm: ScrollableManager => lm.blockScroll = bs
    case _ =>
  }

}



case class DrawerRecyclerStates(
  disableScroll: Boolean = false,
  lastMotionX: Float = 0,
  lastMotionY: Float = 0,
  touchState: ViewState = Stopped) {

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

}
