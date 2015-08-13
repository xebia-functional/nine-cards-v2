package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.animation.{Animator, AnimatorListenerAdapter, ObjectAnimator, ValueAnimator}
import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view._
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.TouchState._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Transformer, Ui}

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data](context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(context, attr, defStyleAttr, defStyleRes) { self =>

  type PageChangedObserver = (Int => Unit)

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0, 0)

  def this(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper) = this(context, attr, defStyleAttr, 0)

  var startScroll: () => Unit = () => {}

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

  val resetAfterAnimationListener = new AnimatorListenerAdapter() {
    var swap = false

    override def onAnimationEnd(animation: Animator) {
      if (swap) swapViews()
      runUi(self <~ layerHardware(false))
      super.onAnimationEnd(animation)
    }
  }

  val mainAnimator: ObjectAnimator = new ObjectAnimator
  mainAnimator.setInterpolator(new DecelerateInterpolator())
  mainAnimator.setPropertyName(if (horizontalGallery) "translationX" else "translationY")
  mainAnimator.addListener(resetAfterAnimationListener)
  mainAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    override def onAnimationUpdate(value: ValueAnimator) {
      displacement = value.getAnimatedValue.asInstanceOf[Float]
      runUi(transformPanelCanvas())
    }
  })

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var previousParentView: Option[FrameLayout] = Some(new FrameLayout(contextWrapper.application))
  var nextParentView: Option[FrameLayout] = Some(new FrameLayout(contextWrapper.application))
  var frontParentView: Option[FrameLayout] = Some(new FrameLayout(contextWrapper.application))

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

  def populateView(view: Option[Holder], data: Data, viewType: Int, position: Int): Ui[_]

  def getItemViewTypeCount: Int = 0

  def getItemViewType(data: Data, position: Int): Int = 0

  def getWorksSpacesCount = data.length

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

    val ui = (for {
      p <- previousParentView
      n <- nextParentView
      f <- frontParentView
    } yield {
        p.addView(previous, params)
        n.addView(next, params)
        f.addView(front, params)
        self <~ vgAddViews(Seq(p, n, f), params)
      }) getOrElse (throw new InstantiationException("parent views can't be added"))
    runUi(ui ~ reset())
  }

  private def layerHardware(activate: Boolean) = Transformer {
    case v: View if Option(v.getTag(R.id.use_layer_hardware)).isDefined => v <~ (if (activate) vLayerTypeHardware() else vLayerTypeNone())
  }

  def goToItem(): Int = {
    (displacement, currentItem) match {
      case (disp, item) if disp < 0 && item >= data.size - 1 => 0
      case (disp, item) if disp < 0 => currentItem + 1
      case (disp, item) if currentItem <= 0 => data.length - 1
      case _ => currentItem - 1
    }
  }

  def notifyPageChangedObservers() = {
    onPageChangedObservers map (observer => observer(goToItem()))
  }

  def addPageChangedObservers(f: PageChangedObserver) = {
    onPageChangedObservers = onPageChangedObservers :+ f
  }

  private def getSizeWidget = if (horizontalGallery) getWidth else getHeight

  def isPosition(position: Int): Boolean = currentItem == position

  def isFirst: Boolean = isPosition(0)

  def isLast: Boolean = isPosition(data.length - 1)

  def canGoToPrevious = !isFirst || (isFirst && infinite)

  def canGoToNext = !isLast || (isLast && infinite)

  def snap(velocity: Float): Unit = {
    mainAnimator.cancel()
    val destiny = velocity match {
      case v if v > 0 && displacement > 0 => getSizeWidget
      case v if v <= 0 && displacement < 0 => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, calculateDurationByVelocity(velocity, durationAnimation))
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

  def performScroll(delta: Float): Ui[_] = {
    mainAnimator.cancel()
    displacement = math.max(-getSizeWidget, Math.min(getSizeWidget, displacement - delta))

    val uiVisibility = displacement match {
      case d if d > 0 => (previousParentView <~ vVisible) ~ (nextParentView <~ vGone)
      case _ => (previousParentView <~ vGone) ~ (nextParentView <~ vVisible)
    }

    uiVisibility ~ applyTranslation(frontParentView, displacement) ~ transformPanelCanvas()
  }

  private def applyTranslation(view: Option[ViewGroup], translate: Float): Ui[_] =
    view <~ (if (horizontalGallery) vTranslationX(translate) else vTranslationY(translate))


  private def transformPanelCanvas(): Ui[_] = {
    val percent = math.abs(displacement) / getSizeWidget
    val fromLeft = displacement > 0
    applyTransformer(if (fromLeft) previousParentView else nextParentView, percent, fromLeft)
  }

  private def applyTransformer(view: Option[ViewGroup], percent: Float, fromLeft: Boolean): Ui[_] = {
    val translate = {
      val start = if (fromLeft) -getSizeWidget else getSizeWidget
      start - (start * percent)
    }
    applyTranslation(view, translate)
  }

  private def animateViews(dest: Int, duration: Int) = {
    val swap = dest != 0
    if (swap) notifyPageChangedObservers()
    resetAfterAnimationListener.swap = swap
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
      currentItem = goToItem()
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
      currentItem = goToItem()
    }
  }

  private def swapViews(): Unit = {
    if (displacement < 0) next() else previous()
    runUi(reset())
  }

  private def reset(): Ui[_] = {
    // TODO Shouldn't create views directly from here

    val auxFrontViewType = getItemViewType(data(currentItem), currentItem)

    val frontUi = auxFrontViewType match {
      case aux if aux != frontViewType =>
        frontViewType = auxFrontViewType
        val newView = createView(frontViewType)
        frontView = Some(newView)
        (frontParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
          populateView(frontView, data(currentItem), frontViewType, currentItem)
      case _ => populateView(frontView, data(currentItem), frontViewType, currentItem)
    }

    val positionLeft: Int = if (currentItem - 1 < 0) data.length - 1 else currentItem - 1
    val auxPreviewViewType = getItemViewType(data(positionLeft), positionLeft)
    val leftUi = (auxPreviewViewType, canGoToPrevious) match {
      case (aux, can) if aux != previewViewType && can =>
        previewViewType = auxPreviewViewType
        val newView = createView(previewViewType)
        previousView = Some(newView)
        (previousParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
          populateView(previousView, data(positionLeft), previewViewType, positionLeft)
      case (aux, can) if can => populateView(previousView, data(positionLeft), previewViewType, positionLeft)
      case _ => Ui.nop
    }

    val positionRight: Int = if (currentItem + 1 > data.length - 1) 0 else currentItem + 1
    val auxNextViewType = getItemViewType(data(positionRight), positionRight)
    val rightUi = (auxNextViewType, canGoToNext) match {
      case (aux, can) if aux != nextViewType && can =>
        nextViewType = auxNextViewType
        val newView = createView(nextViewType)
        nextView = Some(newView)
        (nextParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
          populateView(nextView, data(positionRight), nextViewType, positionRight)
      case (aux, can) if can => populateView(nextView, data(positionRight), nextViewType, positionRight)
      case _ => Ui.nop
    }

    displacement = 0
    enabled = data.nonEmpty && data.length > 1

    mainAnimator.cancel()

    frontParentView map {
      front =>
        mainAnimator.setTarget(front)
        mainAnimator.setFloatValues(0, 0)
    }

    frontUi ~ leftUi ~ rightUi ~
      applyTranslation(frontParentView, displacement) ~
      applyTranslation(nextParentView, getSizeWidget) ~
      applyTranslation(previousParentView, -getSizeWidget) ~
      (previousParentView <~ vGone) ~
      (nextParentView <~ vGone <~ vBringToFront) ~
      (frontParentView <~ vClearAnimation <~ vVisible <~ vBringToFront)

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
      case _ =>
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
            runUi(performScroll(if (horizontalGallery) deltaX else deltaY))
          case _ => setStateIfNeeded(x, y)
        }
      case ACTION_DOWN => lastMotionX = x; lastMotionY = y
      case ACTION_CANCEL | ACTION_UP => computeFling(); touchState = stopped
      case _ =>
    }
    true
  }

  private def setStateIfNeeded(x: Float, y: Float) {
    val xDiff = math.abs(x - lastMotionX)
    val yDiff = math.abs(y - lastMotionY)

    val xMoved = xDiff > touchSlop
    val yMoved = yDiff > touchSlop

    if (xMoved || yMoved) {
      val penultimate = data.length - 2
      val isScrolling = (infinite, horizontalGallery, xDiff > yDiff, mainAnimator.isRunning) match {
        case (i, h, xMove, running) if i && h && xMove => true
        case (i, h, xMove, running) if i && !h && !xMove => true
        case (i, h, xMove, running) if running && !i && h && xMove && x - lastMotionX > 0 && isPosition(1) => false
        case (i, h, xMove, running) if running && !i && h && xMove && x - lastMotionX < 0 && isPosition(penultimate) => false
        case (i, h, xMove, running) if running && !i && !h && !xMove && y - lastMotionY > 0 && isPosition(1) => false
        case (i, h, xMove, running) if running && !i && !h && !xMove && y - lastMotionY < 0 && isPosition(penultimate) => false
        case (i, h, xMove, running) if !i && h && xMove && x - lastMotionX > 0 && !isFirst => true
        case (i, h, xMove, running) if !i && h && xMove && x - lastMotionX < 0 && !isLast => true
        case (i, h, xMove, running) if !i && !h && !xMove && y - lastMotionY > 0 && !isFirst => true
        case (i, h, xMove, running) if !i && !h && !xMove && y - lastMotionY < 0 && !isLast => true
        case _ => false
      }
      if (isScrolling) {
        startScroll()
        touchState = scrolling
        runUi(self <~ layerHardware(true))
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