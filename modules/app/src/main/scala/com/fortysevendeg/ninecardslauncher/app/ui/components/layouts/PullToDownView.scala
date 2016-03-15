package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.{LayoutParams, MarginLayoutParams}
import android.view._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{SwipeController, Swiping}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts

class PullToDownView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends ViewGroup(context, attr, defStyleAttr)
  with Contexts[View]
  with SwipeController {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  lazy val content = getChildAt(0)

  var pullToDownStatuses = PullToDownStatuses(
    distanceToValidAction = resGetDimensionPixelSize(R.dimen.distance_to_valid_action))

  var pullingListeners = PullingListener()

  var horizontalListener = HorizontalMovementListener()

  val touchSlop = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
  }

  override def checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p.isInstanceOf[MarginLayoutParams]

  override def generateDefaultLayoutParams(): ViewGroup.LayoutParams =
    new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

  override def generateLayoutParams(p: LayoutParams): LayoutParams = new MarginLayoutParams(p)

  override def generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams =
    new MarginLayoutParams(getContext, attrs)

  override def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): Unit = {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    content.getLayoutParams match {
      case lp: ViewGroup.MarginLayoutParams =>
        val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
          widthMeasureSpec,
          getPaddingLeft + getPaddingRight + lp.leftMargin + lp.rightMargin, lp.width)
        val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
          heightMeasureSpec,
          getPaddingTop + getPaddingBottom + lp.topMargin, lp.height)
        content.measure(childWidthMeasureSpec, childHeightMeasureSpec)
      case _ =>
    }
  }

  override def onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int): Unit =
    content.getLayoutParams match {
      case lp: ViewGroup.MarginLayoutParams =>
        val left: Int = getPaddingLeft + lp.leftMargin
        val top: Int = getPaddingTop + lp.topMargin + pullToDownStatuses.currentPosY
        val right: Int = left + content.getMeasuredWidth
        val bottom: Int = top + content.getMeasuredHeight
        content.layout(left, top, right, bottom)
      case _ =>
    }

  override def dispatchTouchEvent(event: MotionEvent): Boolean = {
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    val action = MotionEventCompat.getActionMasked(event)
    updateSwipe(event)
    (pullToDownStatuses.action, action) match {
      case (_, ACTION_DOWN) => actionDown(event, x, y)
      case (NoMovement, ACTION_MOVE) => actionMoveIdle(event, x, y)
      case (Pulling, ACTION_MOVE) => actionMovePulling(event, x, y)
      case (Pulling, ACTION_UP | ACTION_CANCEL) => releasePulling(event)
      case (HorizontalMovement, ACTION_MOVE) => actionMoveHorizontal(event, x, y)
      case (HorizontalMovement, ACTION_UP | ACTION_CANCEL) => releaseHorizontal(event)
      case _ => super.dispatchTouchEvent(event)
    }
  }

  def drop(): Unit = {
    val anim: ValueAnimator = ValueAnimator.ofInt(0, 100)
    anim.addUpdateListener(new AnimatorUpdateListener {
      override def onAnimationUpdate(animation: ValueAnimator): Unit = {
        movePos(-pullToDownStatuses.currentPosY * animation.getAnimatedFraction)
        requestDisallowInterceptTouchEvent(true)
      }
    })
    anim.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        pullingListeners.end()
        pullToDownStatuses = pullToDownStatuses.copy(action = NoMovement)
        restart()
      }
    })
    anim.start()
  }

  private[this] def actionDown(ev: MotionEvent, x: Float, y: Float): Boolean = {
    pullToDownStatuses = pullToDownStatuses.start(x, y, NoMovement)
    super.dispatchTouchEvent(ev)
    true
  }

  private[this] def actionMoveIdle(ev: MotionEvent, x: Float, y: Float): Boolean = {
    if (pullToDownStatuses.enabled) {
      val deltaX = x - pullToDownStatuses.startX
      val deltaY = y - pullToDownStatuses.startY
      val pulling = childInTop && (deltaY > touchSlop)
      val moveX = pullToDownStatuses.scrollHorizontalEnabled &&
        (math.abs(deltaX) > touchSlop) &&
        (math.abs(deltaX) > math.abs(deltaY))
      (pulling, moveX) match {
        case (true, _) =>
          pullToDownStatuses = pullToDownStatuses.start(x, y, Pulling)
          pullingListeners.start()
        case (_, true) =>
          pullToDownStatuses = pullToDownStatuses.start(x, y, HorizontalMovement)
          horizontalListener.start()
        case _ =>
      }
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def actionMovePulling(ev: MotionEvent, x: Float, y: Float): Boolean = {
    pullToDownStatuses = pullToDownStatuses.move(x, y)
    val moveDown = pullToDownStatuses.offsetY > 0

    (moveDown, !moveDown, pullToDownStatuses.hasLeftStartPosition, childInTop) match {
      case (down, _, _, inTop) if down && !inTop => // disable move when user not reach top
      case (down, up, canUp, _) if (up && canUp) || down => movePos(pullToDownStatuses.offsetY)
      case _ =>
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def releasePulling(ev: MotionEvent): Boolean = {
    recycleSwipe()
    if (pullToDownStatuses.currentPosY > 0) {
      drop()
    } else {
      pullingListeners.end()
      pullToDownStatuses = pullToDownStatuses.copy(action = NoMovement)
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def actionMoveHorizontal(ev: MotionEvent, x: Float, y: Float): Boolean = {
    requestDisallowInterceptTouchEvent(true)
    val to = pullToDownStatuses.currentPosY + pullToDownStatuses.offsetX.toInt
    pullToDownStatuses = pullToDownStatuses.updateCurrentPostX(to)
    horizontalListener.scroll(pullToDownStatuses.offsetX.toInt)
    pullToDownStatuses = pullToDownStatuses.move(x, y)
    super.dispatchTouchEvent(ev)
  }

  private[this] def releaseHorizontal(ev: MotionEvent): Boolean = {
    val swipe = currentSwiping
    recycleSwipe()
    horizontalListener.end(swipe, -pullToDownStatuses.currentPosX)
    pullToDownStatuses = pullToDownStatuses.restart()
    super.dispatchTouchEvent(ev)
  }

  private[this] def restart() = {
    content.offsetTopAndBottom(-pullToDownStatuses.currentPosY)
    invalidate()
    pullToDownStatuses = pullToDownStatuses.restart()
  }

  private[this] def movePos(deltaY: Float) = {
    if (deltaY >= 0 || !pullToDownStatuses.isInStartPosition) {
      val to: Int = {
        val to = pullToDownStatuses.currentPosY + deltaY.toInt
        pullToDownStatuses.willOverTop(to) match {
          case true => pullToDownStatuses.posStart
          case false => to
        }
      }
      pullToDownStatuses = pullToDownStatuses.updateCurrentPostY(to)
      pullingListeners.scroll(to, pullToDownStatuses.isValidAction)
      val change: Int = to - pullToDownStatuses.lastPosY
      updatePos(change)
    }
  }

  private[this] def updatePos(change: Int) = {
    if (change != 0) {
      content.offsetTopAndBottom(change)
      invalidate()
    }
  }

  private[this] def childInTop: Boolean = !content.canScrollVertically(-1)

}

case class PullingListener(
  start: () => Unit = () => (),
  end: () => Unit = () => (),
  scroll: (Int, Boolean) => Unit = (_, _) => ())

case class HorizontalMovementListener(
  start: () => Unit = () => (),
  end: (Swiping, Int) => Unit = (_, _) => (),
  scroll: (Int) => Unit = (_) => ())

sealed trait PullType

case object Pulling extends PullType

case object HorizontalMovement extends PullType

case object NoMovement extends PullType

case class PullToDownStatuses(
  distanceToValidAction: Int,
  resistance: Float = 3f,
  lastPosX: Int = 0,
  currentPosX: Int = 0,
  lastPosY: Int = 0,
  currentPosY: Int = 0,
  startX: Float = 0,
  startY: Float = 0,
  lastMoveX: Float = 0,
  lastMoveY: Float = 0,
  offsetX: Float = 0,
  offsetY: Float = 0,
  enabled: Boolean = true,
  scrollHorizontalEnabled: Boolean = false,
  action: PullType = NoMovement) {

  val posStart = 0

  def restart(): PullToDownStatuses = copy(
    action = NoMovement,
    lastPosX = 0,
    currentPosX = 0,
    lastPosY = 0,
    currentPosY = 0,
    startX = 0,
    startY = 0,
    lastMoveX = 0,
    lastMoveY = 0,
    offsetX = 0,
    offsetY = 0)

  def dontStarted: Boolean = startX == 0 && startY == 0

  def start(x: Float, y: Float, action: PullType): PullToDownStatuses = copy(
    currentPosX = 0,
    currentPosY = 0,
    startX = x,
    startY = y,
    lastMoveX = x,
    lastMoveY = y,
    action = action)

  def move(x: Float, y: Float): PullToDownStatuses = copy(
    offsetX = x - lastMoveX,
    offsetY = (y - lastMoveY) / resistance,
    lastMoveX = x,
    lastMoveY = y)

  def updateCurrentPostX(current: Int): PullToDownStatuses = copy(
    lastPosX = currentPosX,
    currentPosX = current)

  def updateCurrentPostY(current: Int): PullToDownStatuses = copy(
    lastPosY = currentPosY,
    currentPosY = current)

  def hasLeftStartPosition: Boolean = currentPosY > posStart

  def isInStartPosition: Boolean = currentPosY == posStart

  def willOverTop(to: Int): Boolean = to < posStart

  def isValidAction: Boolean = currentPosY > distanceToValidAction

}