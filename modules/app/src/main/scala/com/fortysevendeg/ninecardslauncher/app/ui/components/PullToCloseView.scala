package com.fortysevendeg.ninecardslauncher.app.ui.components

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
import macroid.{ContextWrapper, Tweak}

class PullToCloseView(context: Context, attrs: AttributeSet, defStyle: Int)(implicit contextWrapper: ContextWrapper)
  extends ViewGroup(context, attrs, defStyle) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attrs: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attrs, 0)

  lazy val content = getChildAt(0)

  val indicator = PullToCloseIndicator()

  val listeners = PullToCloseListener()

  val touchSlop = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
  }

  def isPulling: Boolean = indicator.isPulling

  def currentPosY: Int = indicator.currentPosY

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
        val top: Int = getPaddingTop + lp.topMargin + indicator.currentPosY
        val right: Int = left + content.getMeasuredWidth
        val bottom: Int = top + content.getMeasuredHeight
        content.layout(left, top, right, bottom)
      case _ =>
    }

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = {
    val x = ev.getX
    val y = ev.getY
    (indicator.isPulling, childInTop, ev.getAction) match {
      case (true, _, ACTION_UP | ACTION_CANCEL) => release(ev)
      case (true, _, ACTION_DOWN) => actionDown(ev, x, y)
      case (true, _, ACTION_MOVE) => actionMove(ev, x, y)
      case (false, true, ACTION_DOWN) => actionDown(ev, x, y)
      case (false, true, ACTION_MOVE) => actionMoveIdle(ev, x, y)
      case _ => super.dispatchTouchEvent(ev)
    }
  }

  private[this] def release(ev: MotionEvent): Boolean = {
    if (indicator.currentPosY > 0) {
      if (indicator.shouldClose()) listeners.close()
      val anim: ValueAnimator = ValueAnimator.ofInt(0, 100)
      anim.addUpdateListener(new AnimatorUpdateListener {
        override def onAnimationUpdate(animation: ValueAnimator): Unit =
          movePos(-indicator.currentPosY * animation.getAnimatedFraction)
      })
      anim.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator): Unit = {
          listeners.endPulling()
          indicator.isPulling = false
          restart()
        }
      })
      anim.start()
    } else {
      listeners.endPulling()
      indicator.isPulling = false
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def actionDown(ev: MotionEvent, x: Float, y: Float): Boolean = {
    indicator.start(x, y)
    super.dispatchTouchEvent(ev)
    true
  }

  private[this] def actionMove(ev: MotionEvent, x: Float, y: Float): Boolean = {
    val firstTime = !indicator.isPulling && childInTop && indicator.dontStarted
    if (firstTime) {
      // User began movement when the child can scroll up and we need start information
      indicator.start(x, y)
    }
    indicator.move(x, y)
    val moveDown = indicator.offsetY > 0

    (moveDown, !moveDown, indicator.hasLeftStartPosition, childInTop) match {
      case (down, _, _, inTop) if down && !inTop => // disable move when user not reach top
      case (down, up, canUp, _) if (up && canUp) || down => movePos(indicator.offsetY)
      case _ =>
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def actionMoveIdle(ev: MotionEvent, x: Float, y: Float): Boolean = {
    if (y - indicator.startY > touchSlop) {
      indicator.isPulling = true
      indicator.start(x, y)
      listeners.startPulling()
    }
    super.dispatchTouchEvent(ev)
  }

  private[this] def restart() = {
    content.offsetTopAndBottom(-indicator.currentPosY)
    invalidate()
    indicator.restart()
  }

  private[this] def movePos(deltaY: Float) = {
    if (deltaY >= 0 || !indicator.isInStartPosition) {
      val to: Int = {
        val to = indicator.currentPosY + deltaY.toInt
        indicator.willOverTop(to) match {
          case true => indicator.posStart
          case false => to
        }
      }
      indicator.updateCurrentPostY(to)
      listeners.scroll(to, indicator.shouldClose())
      val change: Int = to - indicator.lastPosY
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

case class PullToCloseIndicator(
  resistance: Float = 3f,
  var lastPosY: Int = 0,
  var currentPosY: Int = 0,
  var startX: Float = 0,
  var startY: Float = 0,
  var lastMoveX: Float = 0,
  var lastMoveY: Float = 0,
  var offsetX: Float = 0,
  var offsetY: Float = 0,
  var isPulling: Boolean = false)(implicit contextWrapper: ContextWrapper) {

  val distanceToValidClose = resGetDimensionPixelSize(R.dimen.distance_to_valid_close)

  val posStart = 0

  def restart() = {
    isPulling = false
    lastPosY = 0
    currentPosY = 0
    startX = 0
    startY = 0
    lastMoveX = 0
    lastMoveY = 0
    offsetX = 0
    offsetY = 0
  }

  def dontStarted: Boolean = startX == 0 && startY == 0

  def start(x: Float, y: Float) = {
    currentPosY = 0
    startX = x
    startY = y
    lastMoveX = x
    lastMoveY = y
  }

  def move(x: Float, y: Float) = {
    processMove(x - lastMoveX, y - lastMoveY)
    lastMoveX = x
    lastMoveY = y
    isPulling = true
  }

  def offsets(x: Float, y: Float) = {
    offsetX = x
    offsetY = y
  }

  def updateCurrentPostY(current: Int) = {
    lastPosY = currentPosY
    currentPosY = current
  }

  private def processMove(ox: Float, oy: Float) = offsets(ox, oy / resistance)

  def hasLeftStartPosition: Boolean = currentPosY > posStart

  def isInStartPosition: Boolean = currentPosY == posStart

  def willOverTop(to: Int): Boolean = to < posStart

  def shouldClose(): Boolean = currentPosY > distanceToValidClose

}

object PullToCloseViewTweaks {

  def pcvListener(pullToCloseListener: PullToCloseListener) = Tweak[PullToCloseView] {
    view =>
      view.listeners.startPulling = pullToCloseListener.startPulling
      view.listeners.endPulling = pullToCloseListener.endPulling
      view.listeners.scroll = pullToCloseListener.scroll
      view.listeners.close = pullToCloseListener.close
  }

}