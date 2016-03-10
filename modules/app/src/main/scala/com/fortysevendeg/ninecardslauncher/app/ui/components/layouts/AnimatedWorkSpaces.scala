package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view._
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.commons._
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data]
  (context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(context, attr, defStyleAttr)
  with LongClickHandler { self =>

  type PageChangedObserver = (Int => Unit)

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var listener = new AnimatedWorkSpacesListener

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

  val moveItemsAnimator = new TranslationAnimator(
    translation = if (statuses.horizontalGallery) TranslationX else TranslationY,
    update = (value: Float) => {
      statuses = statuses.copy(displacement = value)
      transformPanelCanvas()
    }
  )

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

  override def onLongClick: () => Unit = listener.onLongClick

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
    statuses = statuses.copy(currentItem = position)

    removeAllViews()

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
    (ui ~ reset()).run
  }

  def clean(): Unit = {
    data = Seq.empty
    removeAllViews()
  }

  def goToItem(): Int = (statuses.displacement, statuses.currentItem) match {
    case (disp, item) if disp < 0 && item >= data.size - 1 => 0
    case (disp, item) if disp < 0 => item + 1
    case (_, item) if item <= 0 => data.length - 1
    case (_, item) => item - 1
  }

  def notifyPageChangedObservers() = onPageChangedObservers foreach (observer => observer(goToItem()))

  def addPageChangedObservers(f: PageChangedObserver) = onPageChangedObservers = onPageChangedObservers :+ f

  private[this] def getSizeWidget = if (statuses.horizontalGallery) getWidth else getHeight

  def isPosition(position: Int): Boolean = statuses.currentItem == position

  def isFirst: Boolean = isPosition(0)

  def isLast: Boolean = isPosition(data.length - 1)

  def canGoToPrevious = !isFirst || (isFirst && statuses.infinite)

  def canGoToNext = !isLast || (isLast && statuses.infinite)

  private[this] def snap(velocity: Float): Ui[_] = {
    moveItemsAnimator.cancel()
    val destiny = (velocity, statuses.displacement) match {
      case (v, d) if v > 0 && d > 0 => getSizeWidget
      case (v, d) if v <= 0 && d < 0 => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, calculateDurationByVelocity(velocity, durationAnimation))
  }

  private[this] def snapDestination(): Ui[_] = {
    val destiny = statuses.displacement match {
      case d if d > getSizeWidget * .6f => getSizeWidget
      case d if d < -getSizeWidget * .6f => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
  }

  private[this] def performScroll(delta: Float): Ui[_] = {
    moveItemsAnimator.cancel()
    statuses = statuses.updateDisplacement(getSizeWidget, delta)

    val uiVisibility = statuses.displacement match {
      case d if d > 0 => (previousParentView <~ vVisible) ~ (nextParentView <~ vGone)
      case _ => (previousParentView <~ vGone) ~ (nextParentView <~ vVisible)
    }

    uiVisibility ~ applyTranslation(frontParentView, statuses.displacement) ~ transformPanelCanvas()
  }

  def selectPosition(position: Int): Unit = {
    statuses = statuses.copy(currentItem = position)
    reset().run
  }

  private[this] def applyTranslation(view: Option[ViewGroup], translate: Float): Ui[_] =
    view <~ (if (statuses.horizontalGallery) vTranslationX(translate) else vTranslationY(translate))

  private[this] def transformPanelCanvas(): Ui[_] = {
    val percent = statuses.percent(getSizeWidget)
    val fromLeft = statuses.isFromLeft
    applyTransformer(if (fromLeft) previousParentView else nextParentView, percent, fromLeft)
  }

  private[this] def applyTransformer(view: Option[ViewGroup], percent: Float, fromLeft: Boolean): Ui[_] = {
    val translate = {
      val start = if (fromLeft) -getSizeWidget else getSizeWidget
      start - (start * percent)
    }
    applyTranslation(view, translate)
  }

  private[this] def animateViews(dest: Int, duration: Int): Ui[_] = {
    statuses = statuses.copy(swap = dest != 0)
    if (statuses.swap) notifyPageChangedObservers()
    (self <~
      vInvalidate <~~
      moveItemsAnimator.move(statuses.displacement, dest, duration)) ~~
      resetAnimationEnd
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
    statuses = statuses.copy(currentItem = goToItem())
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
    statuses = statuses.copy(currentItem = goToItem())
  }

  private[this] def swapViews(): Ui[_] = {
    if (statuses.isFromLeft) previous() else next()
    reset()
  }

  private[this] def resetAnimationEnd(): Ui[_] =
    (if (statuses.swap) swapViews() else Ui.nop) ~
      (self <~ vLayerHardware(activate = false))

  def reset(): Ui[_] = {
    // TODO Shouldn't create views directly from here
    val frontUi = generateFrontUi
    val leftUi = generateLeftUi
    val rightUi = generateRightUi
    statuses = statuses.copy(displacement = 0, enabled = data.nonEmpty && data.length > 1)

    moveItemsAnimator.cancel()

    (frontParentView <~ moveItemsAnimator.move(0, 0, attachTarget = true)) ~
      frontUi ~ leftUi ~ rightUi ~
      applyTranslation(frontParentView, statuses.displacement) ~
      applyTranslation(nextParentView, getSizeWidget) ~
      applyTranslation(previousParentView, -getSizeWidget) ~
      (previousParentView <~ vGone) ~
      (nextParentView <~ vGone <~ vBringToFront) ~
      (frontParentView <~ vClearAnimation <~ vVisible <~ vBringToFront)

  }

  private[this] def generateFrontUi: Ui[_] = {
    val currentItem = statuses.currentItem
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
    val currentItem = statuses.currentItem
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
    val currentItem = statuses.currentItem
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

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    super.onInterceptTouchEvent(event)
    val (action, x, y) = updateTouch(event)
    (action, statuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        true
      case (ACTION_MOVE, _) =>
        setStateIfNeeded(x, y)
        !statuses.enabled || statuses.isScrolling
      case (ACTION_DOWN, _) =>
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        !statuses.enabled || statuses.isScrolling
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
        statuses = statuses.copy(touchState = Stopped)
        !statuses.enabled || statuses.isScrolling
      case _ => !statuses.enabled || statuses.isScrolling
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    super.onTouchEvent(event)
    val (action, x, y) = updateTouch(event)
    (action, statuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        val deltaX = statuses.deltaX(x)
        val deltaY = statuses.deltaY(y)
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        if (overScroll(deltaX, deltaY)) {
          applyTranslation(frontParentView, 0).run
        } else {
          performScroll(if (statuses.horizontalGallery) deltaX else deltaY).run
        }
      case (ACTION_MOVE, Stopped) => setStateIfNeeded(x, y)
      case (ACTION_DOWN, _) =>
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        startLongClick()
      case (ACTION_CANCEL | ACTION_UP, _) =>
        resetLongClick()
        computeFling()
        statuses = statuses.copy(touchState = Stopped)
      case _ =>
    }
    true
  }

  protected def updateTouch(event: MotionEvent) = {
    if (statuses.velocityTracker.isEmpty) statuses = statuses.copy(velocityTracker = Some(VelocityTracker.obtain()))
    statuses.velocityTracker foreach (_.addMovement(event))
    val action = MotionEventCompat.getActionMasked(event)
    val x = MotionEventCompat.getX(event, 0)
    val y = MotionEventCompat.getY(event, 0)
    (action, x, y)
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

  def setStateIfNeeded(x: Float, y: Float) = {
    if (statuses.enabled) {
      val xDiff = math.abs(x - statuses.lastMotionX)
      val yDiff = math.abs(y - statuses.lastMotionY)

      val xMoved = xDiff > touchSlop
      val yMoved = yDiff > touchSlop

      if (xMoved || yMoved) {
        resetLongClick()
        val penultimate = data.length - 2
        val isScrolling = (statuses.infinite, statuses.horizontalGallery, xDiff > yDiff, moveItemsAnimator.isRunning) match {
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
          statuses = statuses.copy(touchState = Scrolling)
          (self <~ vLayerHardware(activate = true)).run
        }
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
      }
    }
  }

  private[this] def computeFling() = statuses.velocityTracker foreach {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      if (statuses.isScrolling && !overScroll(-tracker.getXVelocity, -tracker.getYVelocity)) {
        val velocity = if (statuses.horizontalGallery) tracker.getXVelocity else tracker.getYVelocity
        (if (math.abs(velocity) > minimumVelocity) snap(velocity) else snapDestination()).run
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
  lastMotionY: Float = 0,
  swap: Boolean = false,
  displacement: Float = 0,
  currentItem: Int = 0) {

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

  def isStopped = touchState == Stopped

  def isScrolling = touchState == Scrolling

  def updateDisplacement(size: Int, delta: Float): AnimatedWorkSpacesStatuses =
    copy(displacement = math.max(-size, Math.min(size, displacement - delta)))

  def percent(size: Int): Float = math.abs(displacement) / size

  def isFromLeft = displacement > 0

}

case class AnimatedWorkSpacesListener(
  onLongClick: () => Unit = () => ())

case class Dimen(width: Int = 0, height: Int = 0)


