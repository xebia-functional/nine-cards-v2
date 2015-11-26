package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.content.Context
import android.support.v4.view.ViewConfigurationCompat
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.{LayoutParams, MarginLayoutParams}
import android.view.{MotionEvent, ViewConfiguration, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class PullToCloseView(context: Context, attrs: AttributeSet, defStyle: Int)(implicit contextWrapper: ContextWrapper)
  extends ViewGroup(context, attrs, defStyle) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attrs, 0)

  lazy val content = getChildAt(0)

  var states = PullToCloseStates()

  val listeners = PullToCloseListener()

  val touchSlop = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
  }

  def isPulling: Boolean = states.isPulling

  def currentPosY: Int = states.currentPosY

  override def checkLayoutParams(p: ViewGroup.LayoutParams): Boolean = p.isInstanceOf[MarginLayoutParams]

  override def generateDefaultLayoutParams(): ViewGroup.LayoutParams =
    new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

  override def generateLayoutParams(p: LayoutParams): LayoutParams = new MarginLayoutParams(p)

  override def generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams =
    new MarginLayoutParams(getContext, attrs)

  override def onFinishInflate(): Unit = {
    if (getChildCount != 1) {
      throw new IllegalStateException("PullToCloseView only can host 1 element")
    }
    super.onFinishInflate()
  }

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
        val top: Int = getPaddingTop + lp.topMargin + states.currentPosY
        val right: Int = left + content.getMeasuredWidth
        val bottom: Int = top + content.getMeasuredHeight
        content.layout(left, top, right, bottom)
      case _ =>
    }

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = {
    val x = ev.getX
    val y = ev.getY
    (states.isPulling, childInTop, ev.getAction) match {
      case (true, _, ACTION_UP | ACTION_CANCEL) => release(ev)
      case (true, _, ACTION_DOWN) => actionDown(ev, x, y)
      case (true, _, ACTION_MOVE) => actionMove(ev, x, y)
      case (false, true, ACTION_DOWN) => actionDown(ev, x, y)
      case (false, true, ACTION_MOVE) => actionMoveIdle(ev, x, y)
      case _ => super.dispatchTouchEvent(ev)
    }
  }

  private[this] def release(ev: MotionEvent): Boolean = {
    if (states.currentPosY > 0) {
      if (states.shouldClose()) listeners.close()
      val anim: ValueAnimator = ValueAnimator.ofInt(0, 100)
      anim.addUpdateListener(new AnimatorUpdateListener {
        override def onAnimationUpdate(animation: ValueAnimator): Unit =
          movePos(-states.currentPosY * animation.getAnimatedFraction)
      })
      anim.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator): Unit = {
          listeners.endPulling()
          states = states.copy(isPulling = false)
          restart()
        }
      })
      anim.start()
    } else {
      listeners.endPulling()
      states = states.copy(isPulling = false)
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def actionDown(ev: MotionEvent, x: Float, y: Float): Boolean = {
    states = states.start(x, y)
    super.dispatchTouchEvent(ev)
    true
  }

  private[this] def actionMove(ev: MotionEvent, x: Float, y: Float): Boolean = {
    val firstTime = !states.isPulling && childInTop && states.dontStarted
    if (firstTime) {
      // User began movement when the child can scroll up and we need start information
      states = states.start(x, y)
    }
    states = states.move(x, y)
    val moveDown = states.offsetY > 0

    (moveDown, !moveDown, states.hasLeftStartPosition, childInTop) match {
      case (down, _, _, inTop) if down && !inTop => // disable move when user not reach top
      case (down, up, canUp, _) if (up && canUp) || down => movePos(states.offsetY)
      case _ =>
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def actionMoveIdle(ev: MotionEvent, x: Float, y: Float): Boolean = {
    if (y - states.startY > touchSlop) {
      states = states.copy(isPulling = true)
      states = states.start(x, y)
      listeners.startPulling()
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def restart() = {
    content.offsetTopAndBottom(-states.currentPosY)
    invalidate()
    states = states.restart()
  }

  private[this] def movePos(deltaY: Float) = {
    if (deltaY >= 0 || !states.isInStartPosition) {
      val to: Int = {
        val to = states.currentPosY + deltaY.toInt
        states.willOverTop(to) match {
          case true => states.posStart
          case false => to
        }
      }
      states = states.updateCurrentPostY(to)
      listeners.scroll(to, states.shouldClose())
      val change: Int = to - states.lastPosY
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

case class PullToCloseListener(
  var startPulling: () => Unit = () => (),
  var endPulling: () => Unit = () => (),
  var scroll: (Int, Boolean) => Unit = (i: Int, b: Boolean) => (),
  var close: () => Unit = () => ())

case class PullToCloseStates(
  resistance: Float = 3f,
  lastPosY: Int = 0,
  currentPosY: Int = 0,
  startX: Float = 0,
  startY: Float = 0,
  lastMoveX: Float = 0,
  lastMoveY: Float = 0,
  offsetX: Float = 0,
  offsetY: Float = 0,
  isPulling: Boolean = false)(implicit contextWrapper: ContextWrapper) {

  val distanceToValidClose = resGetDimensionPixelSize(R.dimen.distance_to_valid_close)

  val posStart = 0

  def restart(): PullToCloseStates = copy(
    isPulling = false,
    lastPosY = 0,
    currentPosY = 0,
    startX = 0,
    startY = 0,
    lastMoveX = 0,
    lastMoveY = 0,
    offsetX = 0,
    offsetY = 0)

  def dontStarted: Boolean = startX == 0 && startY == 0

  def start(x: Float, y: Float): PullToCloseStates = copy(
    currentPosY = 0,
    startX = x,
    startY = y,
    lastMoveX = x,
    lastMoveY = y)

  def move(x: Float, y: Float): PullToCloseStates = copy(
    offsetX = x - lastMoveX,
    offsetY = (y - lastMoveY) / resistance,
    lastMoveX = x,
    lastMoveY = y,
    isPulling = true)

  def updateCurrentPostY(current: Int): PullToCloseStates = copy(
    lastPosY = currentPosY,
    currentPosY = current)

  def hasLeftStartPosition: Boolean = currentPosY > posStart

  def isInStartPosition: Boolean = currentPosY == posStart

  def willOverTop(to: Int): Boolean = to < posStart

  def shouldClose(): Boolean = currentPosY > distanceToValidClose

}