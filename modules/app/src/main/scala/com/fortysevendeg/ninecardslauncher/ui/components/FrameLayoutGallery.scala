package com.fortysevendeg.ninecardslauncher.ui.components

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.{MotionEvent, VelocityTracker, ViewConfiguration}
import android.widget.FrameLayout
import com.fortysevendeg.ninecardslauncher.ui.components.TouchState._
import macroid.{AppContext, Tweak}

abstract class FrameLayoutGallery(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
  extends FrameLayout(context, attr, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  var touchState = stopped

  var enabled = false

  var horizontalGallery = true

  var infinite = true

  var velocityTracker: Option[VelocityTracker] = None

  var lastMotionX: Float = 0

  var lastMotionY: Float = 0

  val (touchSlop, maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (ViewConfigurationCompat.getScaledPagingTouchSlop(configuration),
      configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  def isFirst: Boolean

  def isLast: Boolean

  def performScroll(delta: Float)

  def snap(velocity: Float)

  def snapDestination()

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    super.onInterceptTouchEvent(event)
    if (!enabled) return false
    import android.view.MotionEvent._
    val action = MotionEventCompat.getActionMasked(event)
    if (action == ACTION_MOVE && touchState != stopped) {
      requestDisallowInterceptTouchEvent(true)
      return true
    }
    if (velocityTracker.isEmpty) velocityTracker = Some(VelocityTracker.obtain())
    velocityTracker map (_.addMovement(event))
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    action match {
      case ACTION_MOVE => setStateIfNeeded(x, y)
      case ACTION_DOWN => lastMotionX = x; lastMotionY = y
      case ACTION_CANCEL | ACTION_UP => computeFling(); touchState = stopped
    }
    true
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    super.onTouchEvent(event)
    if (!enabled) return false
    import android.view.MotionEvent._
    val action = MotionEventCompat.getActionMasked(event)
    if (velocityTracker.isEmpty) velocityTracker = Some(VelocityTracker.obtain())
    velocityTracker map (_.addMovement(event))
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    action match {
      case ACTION_MOVE =>
        if (touchState == scrolling) {
          requestDisallowInterceptTouchEvent(true)
          val deltaX = lastMotionX - x
          val deltaY = lastMotionY - y
          lastMotionX = x
          lastMotionY = y
          performScroll(if (horizontalGallery) deltaX else deltaY)
        } else {
          setStateIfNeeded(x, y)
        }
      case ACTION_DOWN => lastMotionX = x; lastMotionY = y
      case ACTION_CANCEL | ACTION_UP => computeFling(); touchState = stopped
    }
    true
  }

  private def setStateIfNeeded(x: Float, y: Float) {
    val xDiff = math.abs(x - lastMotionX)
    val yDiff = math.abs(y - lastMotionY)

    val xMoved = xDiff > touchSlop
    val yMoved = yDiff > touchSlop

    if (xMoved || yMoved) {
      if (infinite) {
        if (horizontalGallery && xDiff > yDiff) {
          touchState = scrolling
        } else if (!horizontalGallery && xDiff < yDiff) {
          touchState = scrolling
        }
      } else {
        if (horizontalGallery && xDiff > yDiff && x - lastMotionX > 0 && !isFirst) {
          // swipe to right
          touchState = scrolling
        } else if (horizontalGallery && xDiff > yDiff && x - lastMotionX < 0 && !isLast) {
          // swipe to left
          touchState = scrolling
        } else if (!horizontalGallery && xDiff < yDiff && y - lastMotionY > 0 && !isFirst) {
          // swipe to down
          touchState = scrolling
        } else if (!horizontalGallery && xDiff < yDiff && y - lastMotionY < 0 && !isLast) {
          // swipe to up
          touchState = scrolling
        }
      }
      lastMotionX = x
      lastMotionY = y
    }
  }

  private def computeFling() {
    velocityTracker map {
      tracker =>
        tracker.computeCurrentVelocity(1000, maximumVelocity)
        val velocity = if (horizontalGallery) tracker.getXVelocity else tracker.getYVelocity
        if (touchState == scrolling) {
          if (math.abs(velocity) > minimumVelocity) {
            snap(velocity)
          } else {
            snapDestination()
          }
        }
        tracker.recycle()
        velocityTracker = None
    }
  }

}

object FrameLayoutGallery {
  type W = FrameLayoutGallery

  def flgEnabled(e: Boolean): Tweak[W] = Tweak[W](_.enabled = e)
  def flgHorizontalGallery(h: Boolean): Tweak[W] = Tweak[W](_.horizontalGallery = h)
  def flgInfinite(i: Boolean): Tweak[W] = Tweak[W](_.infinite = i)

}

object TouchState {
  val stopped = 0
  val scrolling = 1
}