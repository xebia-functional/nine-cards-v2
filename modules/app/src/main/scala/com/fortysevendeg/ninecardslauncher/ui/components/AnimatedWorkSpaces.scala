package com.fortysevendeg.ninecardslauncher.ui.components

import android.animation.{Animator, AnimatorListenerAdapter, ObjectAnimator, ValueAnimator}
import android.content.Context
import android.os.Handler
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view.animation.DecelerateInterpolator
import android.view._
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.ui.components.TouchState._
import macroid.FullDsl._
import macroid.{AppContext, Ui}

import scala.collection.mutable

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data](context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
  extends FrameLayout(context, attr, defStyleAttr) {

  type PageChangedObserver = (Int => Unit)

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  val dimen: Dimen = Dimen()

  var data: Seq[Data] = Seq.empty

  var touchState = stopped

  var enabled = false

  val horizontalGallery = getHorizontalGallery

  var infinite = getInfinite

  var velocityTracker: Option[VelocityTracker] = None

  var lastMotionX: Float = 0

  var lastMotionY: Float = 0

  var onPageChangedObservers: Seq[PageChangedObserver] = Seq.empty

  val (touchSlop, maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (ViewConfigurationCompat.getScaledPagingTouchSlop(configuration),
      configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  val durationAnimation = resGetInteger(android.R.integer.config_shortAnimTime)

  val mainAnimator: ObjectAnimator = new ObjectAnimator

  val hideAfterAnimationListener = new AnimatorListenerAdapter() {
    var swap = false
    override def onAnimationEnd(animation: Animator) {
      new Handler().post(new Runnable() {
        def run() = {
          if (swap) swapViews()
          setLayerType(View.LAYER_TYPE_NONE, null)
        }
      })
      super.onAnimationEnd(animation)
    }
  }

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var previousParentView: Option[FrameLayout] = Some(new FrameLayout(appContext.get))
  var nextParentView: Option[FrameLayout] = Some(new FrameLayout(appContext.get))
  var frontParentView: Option[FrameLayout] = Some(new FrameLayout(appContext.get))

  var previousView = slot[Holder]
  var previewViewType = 0
  var nextView = slot[Holder]
  var nextViewType = 0
  var frontView = slot[Holder]
  var frontViewType = 0

  var displacement: Float = 0

  var currentItem = 0

  def getHorizontalGallery: Boolean = true

  def getInfinite: Boolean = false

  def createView(viewType: Int): Holder

  def populateView(view: Option[Holder], data: Data, viewType: Int, position: Int)

  def getItemViewTypeCount: Int = 0

  def getItemViewType(data: Data, position: Int): Int = 0

  def getWorksSpacesCount() = data.length

  def init(position: Int = 0) = {
    if (data.isEmpty) {
      throw new InstantiationException("data can't be empty")
    }
    currentItem = position

    val (lastItem, nextItem) = if (data.length > 1) (data.length - 1, 1) else (0, 0)
    previewViewType = getItemViewType(data.last, lastItem)
    val previous = createView(previewViewType)
    nextViewType = getItemViewType(data(nextItem), nextItem)
    val next = createView(nextViewType)
    frontViewType = getItemViewType(data(0), 0)
    val front = createView(frontViewType)
    previousView = Some(previous)
    nextView = Some(next)
    frontView = Some(front)

    for {
      p <- previousParentView
      n <- nextParentView
      f <- frontParentView
    } yield {
      p.addView(previous, params)
      n.addView(next, params)
      f.addView(front, params)
      runUi(this <~ vgAddViews(Seq(p, n, f), params))
    }
    reset()
  }

  def notifyPageChangedObservers() = {
    onPageChangedObservers map (observer => observer(currentItem))
  }

  def addPageChangedObservers(f: PageChangedObserver) = {
    onPageChangedObservers = onPageChangedObservers :+ f
  }

  private def getSizeWidget = if (horizontalGallery) getWidth else getHeight

  def isFirst: Boolean = currentItem == 0

  def isLast: Boolean = currentItem == data.length - 1

  def canGoToPrevious = !isFirst || (isFirst && infinite)

  def canGoToNext = !isLast || (isLast && infinite)

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
//    mainAnimator.removeAllListeners()
    mainAnimator.cancel()
    displacement = math.max(-getSizeWidget, Math.min(getSizeWidget, displacement - delta))
    if (displacement > 0) {
      runUi((previousParentView <~ vVisible) ~ (nextParentView <~ vGone))
    } else {
      runUi((previousParentView <~ vGone) ~ (nextParentView <~ vVisible))
    }
    applyTranslation(frontParentView, displacement)
    transformPanelCanvas()
  }

  private def applyTranslation(view: Option[ViewGroup], translate: Float) =
    runUi(view <~ (if (horizontalGallery) vTranslationX(translate) else vTranslationY(translate)))


  private def transformPanelCanvas() = {
    val percent = math.abs(displacement) / getSizeWidget
    val fromLeft = displacement > 0
    applyTransformer(if (fromLeft) previousParentView else nextParentView, percent, fromLeft)
  }

  private def applyTransformer(view: Option[ViewGroup], percent: Float, fromLeft: Boolean) = {
    val translate = {
      val start = if (fromLeft) -getSizeWidget else getSizeWidget
      start - (start * percent)
    }
    applyTranslation(view, translate)
  }

  private def animateViews(dest: Int, duration: Int) = {
    hideAfterAnimationListener.swap = dest != 0
    mainAnimator.setFloatValues(displacement, dest)
    mainAnimator.setDuration(duration)
    mainAnimator.start()
  }

  private def next(): Unit = {
    for {
      frontParent <- frontParentView
      nextParent <- nextParentView
      previousParent <- previousParentView
      front <- frontView
      next <- nextView
      previous <- previousView
    } yield {
      frontParentView = Some(nextParent)
      nextParentView = Some(previousParent)
      previousParentView = Some(frontParent)
      frontView = Some(next)
      nextView = Some(previous)
      previousView = Some(front)
      val auxFront = frontViewType
      frontViewType = nextViewType
      nextViewType = previewViewType
      previewViewType = auxFront
      currentItem = if (currentItem >= data.size - 1) 0 else currentItem + 1
    }
  }

  private def previous(): Unit = {
    for {
      frontParent <- frontParentView
      nextParent <- nextParentView
      previousParent <- previousParentView
      front <- frontView
      next <- nextView
      previous <- previousView
    } yield {
      frontParentView = Some(previousParent)
      nextParentView = Some(frontParent)
      previousParentView = Some(nextParent)
      frontView = Some(previous)
      nextView = Some(front)
      previousView = Some(next)
      val auxFront = frontViewType
      frontViewType = previewViewType
      previewViewType = nextViewType
      nextViewType = auxFront
      currentItem = if (currentItem <= 0) data.length - 1 else currentItem - 1
    }
  }

  private def swapViews(): Unit = {
    if (displacement < 0) next() else previous()
    reset()
  }

  private def reset(): Unit = {
    notifyPageChangedObservers()
    if (data.length > currentItem) {
      // TODO Shouldn't create views directly from here
      val auxFrontViewType = getItemViewType(data(currentItem), currentItem)
      if (frontViewType != auxFrontViewType) {
        frontViewType = auxFrontViewType
        val newView = createView(frontViewType)
        frontView = Some(newView)
        runUi(
          (frontParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
            Ui {
              populateView(frontView, data(currentItem), frontViewType, currentItem)
            }
        )
      } else {
        populateView(frontView, data(currentItem), frontViewType, currentItem)
      }

      if (canGoToPrevious) {
        val positionLeft: Int = if (currentItem - 1 < 0) data.length - 1 else currentItem - 1
        val auxPreviewViewType = getItemViewType(data(positionLeft), positionLeft)
        if (previewViewType != auxPreviewViewType) {
          previewViewType = auxPreviewViewType
          val newView = createView(previewViewType)
          previousView = Some(newView)
          runUi(
            (previousParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
              Ui {
                populateView(previousView, data(positionLeft), previewViewType, positionLeft)
              }
          )
        } else {
          populateView(previousView, data(positionLeft), previewViewType, positionLeft)
        }
      }

      if (canGoToNext) {
        val positionRight: Int = if (currentItem + 1 > data.length - 1) 0 else currentItem + 1
        val auxNextViewType = getItemViewType(data(positionRight), positionRight)
        if (nextViewType != auxNextViewType) {
          nextViewType = auxNextViewType
          val newView = createView(nextViewType)
          nextView = Some(newView)
          runUi(
            (nextParentView <~ vgRemoveAllViews) ~
              (nextParentView <~ vgAddView(newView, params)) ~
              Ui {
                populateView(nextView, data(positionRight), nextViewType, positionRight)
              }
          )
        } else {
          populateView(nextView, data(positionRight), nextViewType, positionRight)
        }
      }
    }
    displacement = 0
    enabled = data.nonEmpty && data.length > 1

    runUi(
      (previousParentView <~ vGone) ~
        (nextParentView <~ vGone <~ vBringToFront) ~
        (frontParentView <~ vClearAnimation <~ vVisible <~ vBringToFront)
    )

    mainAnimator.removeAllListeners()
    mainAnimator.cancel()

    applyTranslation(frontParentView, displacement)
    applyTranslation(nextParentView, getSizeWidget)
    applyTranslation(previousParentView, -getSizeWidget)

    frontParentView map {
      front =>
        mainAnimator.setTarget(front)
        mainAnimator.setPropertyName(if (horizontalGallery) "translationX" else "translationY")
        mainAnimator.setFloatValues(0, 0)
        mainAnimator.setInterpolator(new DecelerateInterpolator())
        mainAnimator.addListener(hideAfterAnimationListener)
        mainAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          override def onAnimationUpdate(arg0: ValueAnimator) {
            displacement = arg0.getAnimatedValue.asInstanceOf[Float]
            transformPanelCanvas()
          }
        })
    }
  }

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    dimen.width = w
    dimen.height = h
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
    touchState != stopped
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
        touchState match {
          case `scrolling` =>
            requestDisallowInterceptTouchEvent(true)
            val deltaX = lastMotionX - x
            val deltaY = lastMotionY - y
            lastMotionX = x
            lastMotionY = y
            performScroll(if (horizontalGallery) deltaX else deltaY)
          case _ => setStateIfNeeded(x, y)
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
      if (isScrolling) {
        touchState = scrolling
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
      }
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

case class Dimen(var width: Int = 0, var height: Int = 0)

object TouchState {
  val stopped = 0
  val scrolling = 1
}