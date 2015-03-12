package com.fortysevendeg.ninecardslauncher.ui.components

import android.animation.{Animator, AnimatorListenerAdapter, ObjectAnimator, ValueAnimator}
import android.content.Context
import android.os.Handler
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view.animation.DecelerateInterpolator
import android.view.{MotionEvent, VelocityTracker, ViewConfiguration, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.ui.components.TouchState._
import macroid.AppContext
import macroid.FullDsl._

abstract class FrameLayoutGallery[Holder <: ViewGroup, Data](context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
  extends FrameLayout(context, attr, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  val data: List[Data] = getData()

  var touchState = stopped

  var enabled = false

  var horizontalGallery = true

  var infinite = false

  var velocityTracker: Option[VelocityTracker] = None

  var lastMotionX: Float = 0

  var lastMotionY: Float = 0

  val (touchSlop, maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (ViewConfigurationCompat.getScaledPagingTouchSlop(configuration),
      configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  val durationAnimation = appContext.get.getResources.getInteger(android.R.integer.config_shortAnimTime)

  val mainAnimator: ObjectAnimator = new ObjectAnimator

  val hideAfterAnimationListener = new AnimatorListenerAdapter() {
    override def onAnimationEnd(animation: Animator) {
      new Handler().post(new Runnable() {
        def run() = swapViews()
      })
      super.onAnimationEnd(animation)
    }
  }

  var previousView = slot[Holder]
  var nextView = slot[Holder]
  var frontView = slot[Holder]
  var displacement: Float = 0

  var currentItem = 0

  def getData(): List[Data]

  def createView(): Holder

  def populateView(view: Option[Holder], data: Data, position: Int)

  createViews()

  private def createViews() = {
    val previous = createView()
    val next = createView()
    val front = createView()
    previousView = Some(previous)
    nextView = Some(next)
    frontView = Some(front)
    val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)
    runUi(
      (this <~ vgAddView(previous, params)) ~
        (this <~ vgAddView(next, params)) ~
        (this <~ vgAddView(front, params))
    )
    reset()
  }

  private def getSizeWidget = if (horizontalGallery) getWidth else getHeight

  def isFirst: Boolean = currentItem == 0

  def isLast: Boolean = currentItem == data.length - 1

  def snap(velocity: Float): Unit = {
    mainAnimator.cancel()
    val destiny = velocity match {
      case v if v > 0 && displacement > 0 => getSizeWidget
      case v if v <= 0 && displacement < 0 => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
  }

  def snapDestination(): Unit = {
    val destiny = displacement match {
      case d if d > getSizeWidget * .6f => getSizeWidget
      case d if d < -getSizeWidget * .6f => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
    invalidate()
  }

  def performScroll(delta: Float): Unit = {
    mainAnimator.removeAllListeners()
    mainAnimator.cancel()
    displacement = math.max(-getSizeWidget, Math.min(getSizeWidget, displacement - delta))
    if (displacement > 0) {
      runUi((previousView <~ vVisible) ~ (nextView <~ vGone))
    } else {
      runUi((previousView <~ vGone) ~ (nextView <~ vVisible))
    }
    applyTranslation(frontView, displacement)
    transformPanelCanvas()
  }

  private def applyTranslation(view: Option[ViewGroup], translate: Float) =
    runUi(view <~ (if (horizontalGallery) vTranslationX(translate) else vTranslationY(translate)))


  private def transformPanelCanvas() = {
    val percent = math.abs(displacement) / getSizeWidget
    val fromLeft = displacement > 0
    applyTransformer(if (fromLeft) previousView else nextView, percent, fromLeft)
  }

  private def applyTransformer(view: Option[ViewGroup], percent: Float, fromLeft: Boolean) = {
    val ratio = (percent * .6f) + .4f
    runUi(view <~ vAlpha(ratio) <~ vScaleX(ratio) <~ vScaleY(ratio))
    val translate = {
      val start = if (fromLeft) -(getSizeWidget * .4f) else getSizeWidget - (getSizeWidget * .4f)
      start - (start * percent)
    }
    applyTranslation(view, translate)
  }

  private def animateViews(dest: Int, duration: Int) = {
    mainAnimator.setFloatValues(displacement, dest)
    mainAnimator.setDuration(duration)
    if (dest != 0) {
      mainAnimator.addListener(hideAfterAnimationListener)
    } else {
      mainAnimator.removeAllListeners()
    }
    mainAnimator.start()
  }

  private def next(): Unit = {
    for {
      front <- frontView
      next <- nextView
      previous <- previousView
    } yield {
      frontView = Some(next)
      nextView = Some(previous)
      previousView = Some(front)
      currentItem = currentItem + 1
      if (currentItem > data.size - 1) currentItem = 0
    }
  }

  private def previous(): Unit = {
    for {
      front <- frontView
      next <- nextView
      previous <- previousView
    } yield {
      frontView = Some(previous)
      nextView = Some(front)
      previousView = Some(next)
      currentItem = currentItem - 1
      if (currentItem < 0) currentItem = data.length - 1
    }
  }

  private def swapViews(): Unit = {
    if (displacement < 0) next() else previous()
    reset()
  }

  private def reset(): Unit = {
    if (data.length > currentItem) {
      populateView(frontView, data(currentItem), currentItem)

      val positionLeft: Int = if (currentItem - 1 < 0) data.length - 1 else currentItem - 1
      populateView(previousView, data(positionLeft), positionLeft)

      val positionRight: Int = if (currentItem + 1 > data.length - 1) 0 else currentItem + 1
      populateView(nextView, data(positionRight), positionRight)
    }
    displacement = 0
    enabled = data.nonEmpty && data.length > 1

    runUi(
      (previousView <~ vGone) ~
        (nextView <~ vGone <~ vBringToFront) ~
        (frontView <~ vClearAnimation <~ vVisible <~ vBringToFront)
    )

    mainAnimator.removeAllListeners()
    mainAnimator.cancel()

    applyTranslation(frontView, displacement)
    applyTranslation(nextView, getSizeWidget)
    applyTranslation(previousView, -getSizeWidget)

    frontView map {
      front =>
        mainAnimator.setTarget(front)
        mainAnimator.setPropertyName(if (horizontalGallery) "translationX" else "translationY")
        mainAnimator.setFloatValues(0, 0)
        mainAnimator.setInterpolator(new DecelerateInterpolator())
        mainAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          override def onAnimationUpdate(arg0: ValueAnimator) {
            displacement = arg0.getAnimatedValue.asInstanceOf[Float]
            transformPanelCanvas()
          }
        })
    }
  }

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
      val isScrolling = (infinite, horizontalGallery, xDiff > yDiff) match {
        case (i, h, xMove) if i && h && xMove => true
        case (i, h, xMove) if i && !h && !xMove => true
        case (i, h, xMove) if !i && h && xMove && x - lastMotionX > 0 && !isFirst => true
        case (i, h, xMove) if !i && h && xMove && x - lastMotionX < 0 && !isLast => true
        case (i, h, xMove) if !i && !h && !xMove && y - lastMotionY > 0 && !isFirst => true
        case (i, h, xMove) if !i && !h && !xMove && y - lastMotionY < 0 && !isLast => true
        case _ => false
      }
      if (isScrolling) touchState = scrolling
      lastMotionX = x
      lastMotionY = y
    }
  }

  private def computeFling() = velocityTracker map {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      val velocity = if (horizontalGallery) tracker.getXVelocity else tracker.getYVelocity
      if (touchState == scrolling) {
        if (math.abs(velocity) > minimumVelocity) snap(velocity) else snapDestination()
      }
      tracker.recycle()
      velocityTracker = None
  }

}

object TouchState {
  val stopped = 0
  val scrolling = 1
}