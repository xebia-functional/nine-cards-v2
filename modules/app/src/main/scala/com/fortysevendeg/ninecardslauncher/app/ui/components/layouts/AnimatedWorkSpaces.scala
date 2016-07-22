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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.commons._
import AnimatedWorkSpaces._
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data]
  (context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr)
  with Contexts[View]
  with LongClickHandler { self =>

  // First parameter  [Data]    : Current data of the screen
  // Second parameter [Data]    : The data where you go
  // Third parameter  [Boolean] : movement to left?
  // Fourth parameter [Float]   : Fraction of the movement
  type MovementObserver = ((Data, Data, Boolean, Float) => Unit)

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val minimumViews = 3

  val positionViewKey = "position-view"

  var listener = new AnimatedWorkSpacesListener

  var data: Seq[Data] = Seq.empty

  private[this] var views: Seq[Holder] = Seq.empty

  var statuses = AnimatedWorkSpacesStatuses(
    horizontalGallery = true,
    infinite = false)

  var onPageChangedObservers: Seq[PageChangedObserver] = Seq.empty

  var onMovementObservers: Seq[MovementObserver] = Seq.empty

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

  var parentViewOne = slot[FrameLayout]

  var parentViewTwo = slot[FrameLayout]

  var parentViewThree = slot[FrameLayout]

  (self <~ vgAddViews(Seq(
    (w[FrameLayout] <~
      wire(parentViewOne) <~
      vAddField(positionViewKey, PreviousView)).get,
    (w[FrameLayout] <~
      wire(parentViewTwo) <~
      vAddField(positionViewKey, NextView)).get,
    (w[FrameLayout] <~
      wire(parentViewThree) <~
      vAddField(positionViewKey, FrontView)).get), params)).run

  override def onLongClick(): Unit = listener.onLongClick()

  def createEmptyView(): Holder

  def createView(viewType: Int): Holder

  def populateView(view: Option[Holder], data: Data, viewType: Int, position: Int): Ui[_]

  def getItemViewTypeCount: Int = 0

  def getItemViewType(data: Data, position: Int): Int = 0

  def getWorksSpacesCount = data.length

  def getCurrentView: Option[Holder] = views.lift(statuses.currentItem)

  def getView(position: Int): Option[Holder] = views.lift(position)

  def init(newData: Seq[Data], position: Int = 0, forcePopulatePosition: Option[Int] = None): Unit = {

    statuses = statuses.copy(currentItem = position)

    // We creates the views for our workspace. We have a minimum of views, if our data don't have this minimum,
    // we must create the necessary empty views

    views = (newData.zipWithIndex map {
      case (itemData, index) =>
        val newScreen = data.lift(index).isEmpty
        val sameData = data.lift(index) contains itemData

        lazy val newView = createView(getItemViewType(itemData, index))

        val selectedView =
          if (newScreen) newView
          else views.lift(index) match {
            case Some(oldView) => oldView
            case _ => newView
          }

        (sameData, forcePopulatePosition) match {
          case (true, Some(forceIndex)) if index != forceIndex =>
          case _ => populateView(Some(selectedView), itemData, getItemViewType(itemData, index), index).run
        }
        selectedView
    }) ++ (newData.length until minimumViews map (_ => createEmptyView()))

    data = newData

    ((parentViewOne <~ vgRemoveAllViews) ~
      (parentViewTwo <~ vgRemoveAllViews) ~
      (parentViewThree <~ vgRemoveAllViews) ~
      reset()).run

  }

  def clean(): Unit = {
    data = Seq.empty
    views = Seq.empty
    ((parentViewOne <~ vgRemoveAllViews) ~
      (parentViewTwo <~ vgRemoveAllViews) ~
      (parentViewThree <~ vgRemoveAllViews)).run
  }

  def goToItem(): Int = (statuses.displacement, statuses.currentItem) match {
    case (disp, item) if disp < 0 && item >= data.size - 1 => 0
    case (disp, item) if disp < 0 => item + 1
    case (_, item) if item <= 0 => data.length - 1
    case (_, item) => item - 1
  }

  def notifyPageChangedObservers() = onPageChangedObservers foreach (observer => observer(goToItem()))

  def addPageChangedObservers(f: PageChangedObserver) = onPageChangedObservers = onPageChangedObservers :+ f

  def notifyMovementObservers(percent: Float) = for {
    from <- data.lift(currentPage())
    to <- data.lift(goToItem())
  } yield {
    onMovementObservers foreach (observer => observer(from, to, statuses.isFromLeft, percent))
  }

  def addMovementObservers(f: MovementObserver) = onMovementObservers = onMovementObservers :+ f

  private[this] def getSizeWidget = if (statuses.horizontalGallery) getWidth else getHeight

  def isPosition(position: Int): Boolean = statuses.currentItem == position

  def currentPage(): Int = statuses.currentItem

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

    applyTranslation(getFrontView, statuses.displacement) ~ transformPanelCanvas()
  }

  def selectPosition(position: Int): Unit = {
    statuses = statuses.copy(currentItem = position)
    (reset() ~ reset()).run // TODO Change that
  }

  private[this] def applyTranslation(view: Option[ViewGroup], translate: Float): Ui[_] =
    view <~ (if (statuses.horizontalGallery) vTranslationX(translate) else vTranslationY(translate))

  private[this] def transformPanelCanvas(): Ui[_] = {
    val percent = statuses.percent(getSizeWidget)
    val fromLeft = statuses.isFromLeft
    notifyMovementObservers(percent)
    applyTransformer(if (fromLeft) getPreviousView else getNextView, percent, fromLeft)
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
    (self <~ vInvalidate) ~
      (getFrontView <~~ moveItemsAnimator.move(statuses.displacement, dest, duration, attachTarget = true)) ~~
      resetAnimationEnd
  }

  private[this] def swapViews(): Ui[_] = {
    statuses = statuses.copy(currentItem = goToItem())
    (this <~ (if (statuses.isFromLeft) reloadPreviousPositionView else reloadNextPositionView)) ~
      (if (statuses.isFromLeft) recreate(PreviousView) else recreate(NextView)) ~
      Ui {
        statuses = statuses.copy(displacement = 0, enabled = data.nonEmpty && data.length > 1)
      }
  }

  private[this] def resetAnimationEnd(): Ui[_] =
    (if (statuses.swap) swapViews() else Ui.nop) ~
      (self <~ vLayerHardware(activate = false))

  def reset(): Ui[_] = {
    statuses = statuses.copy(displacement = 0, enabled = data.nonEmpty && data.length > 1)
    moveItemsAnimator.cancel()
    recreate(FrontView) ~
      recreate(PreviousView) ~
      recreate(NextView)
  }

  private[this] def recreate(positionView: PositionView): Ui[Any] = {
    val currentItem = statuses.currentItem

    val (position, displacement) = positionView match {
      case PreviousView =>  (if (currentItem - 1 < 0) views.length - 1 else currentItem - 1, -getSizeWidget)
      case NextView => (if (currentItem + 1 > views.length - 1) 0 else currentItem + 1, getSizeWidget)
      case FrontView => (currentItem, 0)
    }

    val view = getView(positionView)

    (view <~
      vgRemoveAllViews <~
      vgAddView(views(position), params)) ~
      applyTranslation(view, displacement)

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
          applyTranslation(getFrontView, 0).run
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

  private[this] def overScroll(deltaX: Float, deltaY: Float): Boolean = getFrontView exists { view =>
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

  private[this] def reloadPreviousPositionView = Transformer {
    case fl: FrameLayout if fl.getField[PositionView](positionViewKey).contains(PreviousView) =>
      fl <~ vAddField(positionViewKey, FrontView)
    case fl: FrameLayout if fl.getField[PositionView](positionViewKey).contains(NextView) =>
      fl <~ vAddField(positionViewKey, PreviousView)
    case fl: FrameLayout if fl.getField[PositionView](positionViewKey).contains(FrontView) =>
      fl <~ vAddField(positionViewKey, NextView)
  }

  private[this] def reloadNextPositionView = Transformer {
    case fl: FrameLayout if fl.getField[PositionView](positionViewKey).contains(PreviousView) =>
      fl <~ vAddField(positionViewKey, NextView)
    case fl: FrameLayout if fl.getField[PositionView](positionViewKey).contains(NextView) =>
      fl <~ vAddField(positionViewKey, FrontView)
    case fl: FrameLayout if fl.getField[PositionView](positionViewKey).contains(FrontView) =>
      fl <~ vAddField(positionViewKey, PreviousView)
  }

  protected def getPreviousView: Option[FrameLayout] = getView(PreviousView)

  protected def getNextView: Option[FrameLayout] = getView(NextView)

  protected def getFrontView: Option[FrameLayout] = getView(FrontView)

  private[this] def getView(positionView: PositionView): Option[FrameLayout] = {
    (parentViewThree flatMap (_.getField[PositionView](positionViewKey)),
      parentViewOne flatMap (_.getField[PositionView](positionViewKey)),
      parentViewTwo flatMap (_.getField[PositionView](positionViewKey))) match {
      case (Some(`positionView`), _, _) => parentViewThree
      case (_, Some(`positionView`), _) => parentViewOne
      case (_, _, Some(`positionView`)) => parentViewTwo
      case _ => None
    }
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

object AnimatedWorkSpaces {
  // First parameter [Int]: Position of the screen
  type PageChangedObserver = (Int => Unit)
}

case class AnimatedWorkSpacesListener(
  onLongClick: () => Unit = () => ())

case class Dimen(width: Int = 0, height: Int = 0)

sealed trait PositionView

case object PreviousView extends PositionView

case object NextView extends PositionView

case object FrontView extends PositionView


