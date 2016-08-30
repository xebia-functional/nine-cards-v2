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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.commons._
import AnimatedWorkSpaces._
import com.fortysevendeg.ninecardslauncher.app.commons.{AppearBehindWorkspaceAnimation, HorizontalSlideWorkspaceAnimation, NineCardsPreferencesValue, WorkspaceAnimations}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data]
  (context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr)
  with Contexts[View] { self =>

  // First parameter  [Data]    : Current data of the screen
  // Second parameter [Data]    : The data where you go
  // Third parameter  [Boolean] : movement to left?
  // Fourth parameter [Float]   : Fraction of the movement
  type MovementObserver = ((Data, Data, Boolean, Float) => Unit)

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val gestureDetector = new GestureDetector(getContext, new GestureDetector.SimpleOnGestureListener() {
    override def onLongPress(e: MotionEvent): Unit = listener.onLongClick()

    override def onSingleTapConfirmed(e: MotionEvent): Boolean = {
      listener.onClick()
      true
    }
  })

  val minimumViews = 3

  val positionViewKey = "position-view"

  var listener = new AnimatedWorkSpacesListener

  var data: Seq[Data] = Seq.empty

  private[this] var views: Seq[Holder] = Seq.empty

  var statuses = AnimatedWorkSpacesStatuses(infinite = false)

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
    translation = NoTranslation,
    update = (value: Float) => {
      statuses = statuses.copy(displacement = value)
      transformOutPanel() ~ transformInPanel()
    }
  )

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var parentViewOne = slot[FrameLayout]

  var parentViewTwo = slot[FrameLayout]

  var parentViewThree = slot[FrameLayout]

  (self <~ vgAddViews(Seq(
    (w[FrameLayout] <~
      wire(parentViewOne) <~
      vAddField(positionViewKey, NextView)).get,
    (w[FrameLayout] <~
      wire(parentViewTwo) <~
      vAddField(positionViewKey, FrontView)).get,
    (w[FrameLayout] <~
      wire(parentViewThree) <~
      vAddField(positionViewKey, PreviousView)).get), params)).run

  def animationPref = WorkspaceAnimations.readValue(new NineCardsPreferencesValue)

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

  private[this] def getSizeWidget = getWidth

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

    transformOutPanel() ~ transformInPanel()
  }

  def selectPosition(position: Int): Unit = {
    statuses = statuses.copy(currentItem = position)
    (reset() ~ reset()).run // TODO Change that
  }

  private[this] def transformOutPanel(): Ui[_] = {
    val percent = statuses.percent(getSizeWidget)
    getFrontView <~ ((animationPref, statuses.isFromLeft) match {
      case (HorizontalSlideWorkspaceAnimation, _) =>
        vTranslationX(statuses.displacement)
      case (AppearBehindWorkspaceAnimation, true) =>
        val alpha = 1 - percent
        val scale = .5f + (alpha / 2)
        vScaleX(scale) + vScaleY(scale) + vAlpha(alpha)
      case (AppearBehindWorkspaceAnimation, false) =>
        vTranslationX(statuses.displacement)
    })
  }

  private[this] def transformInPanel(): Ui[_] = {
    val percent = statuses.percent(getSizeWidget)
    val fromLeft = statuses.isFromLeft
    val view = if (fromLeft) getPreviousView else getNextView
    notifyMovementObservers(percent)

    view <~ ((animationPref, fromLeft) match {
      case (HorizontalSlideWorkspaceAnimation, _) =>
        val translate = {
          val start = if (fromLeft) -getSizeWidget else getSizeWidget
          start - (start * percent)
        }
        vTranslationX(translate)
      case (AppearBehindWorkspaceAnimation, true) =>
        val translate = {
          val start = -getSizeWidget
          start - (start * percent)
        }
        vTranslationX(translate)
      case (AppearBehindWorkspaceAnimation, false) =>
        val scale = .5f + (percent / 2)
        vTranslationX(0) + vScaleX(scale) + vScaleY(scale) + vAlpha(percent)
    })

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
    (if (statuses.swap) swapViews() else Ui.nop) ~ layerHardware(true)

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
      resetView(view, displacement)
  }

  def resetView(view: Option[FrameLayout], displacement: Int = 0) =
    view <~ vTranslationX(displacement) <~ vScaleX(1) <~ vScaleY(1) <~ vAlpha(1)

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
        statuses.isScrolling
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
    gestureDetector.onTouchEvent(event)
    (action, statuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        val deltaX = statuses.deltaX(x)
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        if (overScroll()) {
          resetView(getFrontView).run
        } else {
          performScroll(deltaX).run
        }
      case (ACTION_MOVE, Stopped) => setStateIfNeeded(x, y)
      case (ACTION_DOWN, _) =>
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
      case (ACTION_CANCEL | ACTION_UP, _) =>
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

  private[this] def overScroll(deltaX: Option[Float] = None): Boolean = getFrontView exists { view =>
    (statuses.infinite, view.getX, deltaX) match {
      case (false, x, Some(dx)) if x >= 0 && dx < 0 && isFirst => true
      case (false, x, Some(dx)) if x <= 0 && dx > 0 && isLast => true
      case (false, x, None) if x > 0 && isFirst => true
      case (false, x, None) if x < 0 && isLast => true
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
        val penultimate = data.length - 2
        val isScrolling = (statuses.infinite, xDiff > yDiff, moveItemsAnimator.isRunning) match {
          case (true, true, _) => true
          case (true, false, _) => true
          case (false, true, true) if x - statuses.lastMotionX > 0 && isPosition(1) => false
          case (false, true, true) if x - statuses.lastMotionX < 0 && isPosition(penultimate) => false
          case (false, false, true) if y - statuses.lastMotionY > 0 && isPosition(1) => false
          case (false, false, true) if y - statuses.lastMotionY < 0 && isPosition(penultimate) => false
          case (false, true, _) if x - statuses.lastMotionX > 0 && !isFirst => true
          case (false, true, _) if x - statuses.lastMotionX < 0 && !isLast => true
          case (false, false, _) if y - statuses.lastMotionY > 0 && !isFirst => true
          case (false, false, _) if y - statuses.lastMotionY < 0 && !isLast => true
          case _ => false
        }
        if (isScrolling) {
          statuses = statuses.copy(touchState = Scrolling)
          layerHardware(true).run
        }
        statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
      }
    }
  }

  private[this] def layerHardware(activate: Boolean) =
    (getFrontView <~ vLayerHardware(activate = activate)) ~
      (getNextView <~ vLayerHardware(activate = activate)) ~
      (getPreviousView <~ vLayerHardware(activate = activate))

  private[this] def computeFling() = statuses.velocityTracker foreach {
    tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      val velocity = tracker.getXVelocity
      if (statuses.isScrolling && !overScroll(Some(-velocity))) {
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
  onClick: () => Unit = () => (),
  onLongClick: () => Unit = () => ())

case class Dimen(width: Int = 0, height: Int = 0)

sealed trait PositionView

case object PreviousView extends PositionView

case object NextView extends PositionView

case object FrontView extends PositionView


