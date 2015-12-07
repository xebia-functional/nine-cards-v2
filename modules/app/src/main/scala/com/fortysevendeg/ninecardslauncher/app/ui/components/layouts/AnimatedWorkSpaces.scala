package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.animation.{Animator, AnimatorListenerAdapter, ObjectAnimator, ValueAnimator}
import android.content.Context
import android.os
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view._
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{Scrolling, Stopped, ViewState}
import com.fortysevendeg.ninecardslauncher.commons._
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data]
  (context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(context, attr, defStyleAttr) { self =>

  type PageChangedObserver = (Int => Unit)

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  val listener = new AnimatedWorkSpacesListener

  var data: Seq[Data] = Seq.empty

  var statuses = AnimatedWorkSpacesStatuses(
    horizontalGallery = getHorizontalGallery,
    infinite = getInfinite)

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
      runUi(self <~ vLayerHardware(activate = false))
      super.onAnimationEnd(animation)
    }
  }

  val mainAnimator: ObjectAnimator = new ObjectAnimator
  mainAnimator.setInterpolator(new DecelerateInterpolator())
  mainAnimator.setPropertyName(if (statuses.horizontalGallery) "translationX" else "translationY")
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

  val longClickMillis = 1000

  val handler = new os.Handler()

  val runnable = new Runnable {
    override def run(): Unit = listener.onLongClick()
  }

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

  def goToItem(): Int = (displacement, currentItem) match {
    case (disp, item) if disp < 0 && item >= data.size - 1 => 0
    case (disp, item) if disp < 0 => currentItem + 1
    case _ if currentItem <= 0 => data.length - 1
    case _ => currentItem - 1
  }

  def notifyPageChangedObservers() = onPageChangedObservers foreach (observer => observer(goToItem()))

  def addPageChangedObservers(f: PageChangedObserver) = onPageChangedObservers = onPageChangedObservers :+ f

  private[this] def getSizeWidget = if (statuses.horizontalGallery) getWidth else getHeight

  def isPosition(position: Int): Boolean = currentItem == position

  def isFirst: Boolean = isPosition(0)

  def isLast: Boolean = isPosition(data.length - 1)

  def canGoToPrevious = !isFirst || (isFirst && statuses.infinite)

  def canGoToNext = !isLast || (isLast && statuses.infinite)

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

  def selectPosition(position: Int): Unit = {
    currentItem = position
    runUi(reset())
  }

  private[this] def applyTranslation(view: Option[ViewGroup], translate: Float): Ui[_] =
    view <~ (if (statuses.horizontalGallery) vTranslationX(translate) else vTranslationY(translate))

  private[this] def transformPanelCanvas(): Ui[_] = {
    val percent = math.abs(displacement) / getSizeWidget
    val fromLeft = displacement > 0
    applyTransformer(if (fromLeft) previousParentView else nextParentView, percent, fromLeft)
  }

  private[this] def applyTransformer(view: Option[ViewGroup], percent: Float, fromLeft: Boolean): Ui[_] = {
    val translate = {
      val start = if (fromLeft) -getSizeWidget else getSizeWidget
      start - (start * percent)
    }
    applyTranslation(view, translate)
  }

  private[this] def animateViews(dest: Int, duration: Int) = {
    val swap = dest != 0
    if (swap) notifyPageChangedObservers()
    resetAfterAnimationListener.swap = swap
    mainAnimator.setFloatValues(displacement, dest)
    mainAnimator.setDuration(duration)
    mainAnimator.start()
  }

  private[this] def next(): Unit = for {
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

  private[this] def previous(): Unit = for {
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

  private[this] def swapViews(): Unit = {
    if (displacement < 0) next() else previous()
    runUi(reset())
  }

  def reset(): Ui[_] = {
    // TODO Shouldn't create views directly from here
    val frontUi = generateFrontUi
    val leftUi = generateLeftUi
    val rightUi = generateRightUi
    displacement = 0
    statuses = statuses.copy(enabled = data.nonEmpty && data.length > 1)

    mainAnimator.cancel()

    frontParentView foreach {
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
      (frontParentView <~ vClearAnimation <~ vVisible <~ vBringToFront) ~
      Ui(listener.endScroll())

  }

  private[this] def generateFrontUi: Ui[_] = {
    val auxFrontViewType = getItemViewType(data(currentItem), currentItem)

    auxFrontViewType match {
      case aux if aux != frontViewType =>
        frontViewType = auxFrontViewType
        val newView = createView(frontViewType)
        frontView = Some(newView)
        (frontParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
          populateView(frontView, data(currentItem), frontViewType, currentItem)
      case _ => populateView(frontView, data(currentItem), frontViewType, currentItem)
    }
  }

  private[this] def generateLeftUi: Ui[_] = {
    val positionLeft: Int = if (currentItem - 1 < 0) data.length - 1 else currentItem - 1
    val auxPreviewViewType = getItemViewType(data(positionLeft), positionLeft)
    (auxPreviewViewType, canGoToPrevious) match {
      case (aux, can) if aux != previewViewType && can =>
        previewViewType = auxPreviewViewType
        val newView = createView(previewViewType)
        previousView = Some(newView)
        (previousParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
          populateView(previousView, data(positionLeft), previewViewType, positionLeft)
      case (aux, can) if can => populateView(previousView, data(positionLeft), previewViewType, positionLeft)
      case _ => Ui.nop
    }
  }

  private[this] def generateRightUi: Ui[_] = {
    val positionRight: Int = if (currentItem + 1 > data.length - 1) 0 else currentItem + 1
    val auxNextViewType = getItemViewType(data(positionRight), positionRight)
    (auxNextViewType, canGoToNext) match {
      case (aux, can) if aux != nextViewType && can =>
        nextViewType = auxNextViewType
        val newView = createView(nextViewType)
        nextView = Some(newView)
        (nextParentView <~ vgRemoveAllViews <~ vgAddView(newView, params)) ~
          populateView(nextView, data(positionRight), nextViewType, positionRight)
      case (aux, can) if can => populateView(nextView, data(positionRight), nextViewType, positionRight)
      case _ => Ui.nop
    }
  }

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    statuses = statuses.copy(dimen = Dimen(w, h))
  }

  private[this] def onStatusEnabled(enabled: Boolean)(f: () => Boolean): Boolean = enabled && f()

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    super.onInterceptTouchEvent(event)
    onStatusEnabled(statuses.enabled) { () =>
      val action = MotionEventCompat.getActionMasked(event)
      if (action == ACTION_MOVE && statuses.touchState != Stopped) {
        requestDisallowInterceptTouchEvent(true)
        true
      } else {
        initVelocityTracker(event)
        val x = MotionEventCompat.getX(event, 0)
        val y = MotionEventCompat.getY(event, 0)
        action match {
          case ACTION_MOVE => setStateIfNeeded(x, y)
          case ACTION_DOWN =>
            statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          case ACTION_CANCEL | ACTION_UP =>
            computeFling()
            statuses = statuses.copy(touchState = Stopped)
          case _ =>
        }
        statuses.touchState != Stopped
      }
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    super.onTouchEvent(event)
    onStatusEnabled(statuses.enabled) { () =>
      val action = MotionEventCompat.getActionMasked(event)
      initVelocityTracker(event)
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      action match {
        case ACTION_MOVE =>
          statuses.touchState match {
            case Scrolling =>
              handler.removeCallbacks(runnable)
              requestDisallowInterceptTouchEvent(true)
              val deltaX = statuses.deltaX(x)
              val deltaY = statuses.deltaY(y)
              statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
              if (overScroll(deltaX, deltaY)) {
                runUi(applyTranslation(frontParentView, 0))
              } else {
                runUi(performScroll(if (statuses.horizontalGallery) deltaX else deltaY))
              }
            case Stopped => setStateIfNeeded(x, y)
          }
        case ACTION_DOWN =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          handler.postDelayed(runnable, longClickMillis)
        case ACTION_CANCEL | ACTION_UP =>
          handler.removeCallbacks(runnable)
          computeFling()
          statuses = statuses.copy(touchState = Stopped)
        case _ =>
      }
      true
    }
  }

  private[this] def initVelocityTracker(event: MotionEvent) = {
    if (statuses.velocityTracker.isEmpty) statuses = statuses.copy(velocityTracker = Some(VelocityTracker.obtain()))
    statuses.velocityTracker foreach (_.addMovement(event))
  }

  private[this] def overScroll(deltaX: Float, deltaY: Float): Boolean = frontParentView exists { view =>
    val xView = view.getX
    val yView = view.getY
    (statuses.infinite, statuses.horizontalGallery, xView, yView, deltaX, deltaY) match {
      case (false, true, x, _, dx, _) if x >= 0 && dx < 0 && isFirst => true
      case (false, true, x, _, dx, _) if x <= 0 && dx > 0 && isLast => true
      case (false, false, _, y, _, dy) if y >= 0 && dy < 0 && isFirst => true
      case (false, false, _, y, _, dy) if y <= 0 && dy > 0 && isLast => true
      case _ => false
    }
  }

  private[this] def setStateIfNeeded(x: Float, y: Float) = {
    val xDiff = math.abs(x - statuses.lastMotionX)
    val yDiff = math.abs(y - statuses.lastMotionY)

    val xMoved = xDiff > touchSlop
    val yMoved = yDiff > touchSlop

    if (xMoved || yMoved) {
      val penultimate = data.length - 2
      val isScrolling = (statuses.infinite, statuses.horizontalGallery, xDiff > yDiff, mainAnimator.isRunning) match {
        case (true, true, true, _) => true
        case (true, false, false, _) => true
        case (false, true, true, true) if x - statuses.lastMotionX > 0 && isPosition(1) => false
        case (false, true, true, true) if x - statuses.lastMotionX < 0 && isPosition(penultimate) => false
        case (false, false, false, true) if y - statuses.lastMotionY > 0 && isPosition(1) => false
        case (false, false, false, true) if y - statuses.lastMotionY < 0 && isPosition(penultimate) => false
        case (false, true, true, _) if x - statuses.lastMotionX > 0 && !isFirst => true
        case (false, true, true, _) if x - statuses.lastMotionX < 0 && !isLast => true
        case (false, false, false, _) if y - statuses.lastMotionY > 0 && !isFirst => true
        case (false, false, false, _) if y - statuses.lastMotionY < 0 && !isLast => true
        case _ => false
      }
      if (isScrolling) {
        listener.startScroll(x - statuses.lastMotionX > 0)
        statuses = statuses.copy(touchState = Scrolling)
        runUi(self <~ vLayerHardware(activate = true))
      }
      statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
    }
  }

  private[this] def computeFling() = statuses.velocityTracker foreach {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      if (statuses.touchState == Scrolling && !overScroll(-tracker.getXVelocity, -tracker.getYVelocity)) {
        val velocity = if (statuses.horizontalGallery) tracker.getXVelocity else tracker.getYVelocity
        if (math.abs(velocity) > minimumVelocity) snap(velocity) else snapDestination()
      }
      tracker.recycle()
      statuses = statuses.copy(velocityTracker = None)
  }

}

case class AnimatedWorkSpacesStatuses(
  horizontalGallery: Boolean,
  infinite: Boolean,
  dimen: Dimen = Dimen(),
  touchState: ViewState = Stopped,
  enabled: Boolean = false,
  velocityTracker: Option[VelocityTracker] = None,
  lastMotionX: Float = 0,
  lastMotionY: Float = 0) {

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

}

case class AnimatedWorkSpacesListener(
  var startScroll: (Boolean) => Unit = (b: Boolean) => (),
  var endScroll: () => Unit = () => (),
  var onLongClick: () => Unit = () => ())

case class Dimen(var width: Int = 0, var height: Int = 0)


