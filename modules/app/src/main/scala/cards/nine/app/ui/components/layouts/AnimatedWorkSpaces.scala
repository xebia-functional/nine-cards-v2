package cards.nine.app.ui.components.layouts

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view._
import android.widget.FrameLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.commons._
import cards.nine.app.ui.components.layouts.AnimatedWorkSpaces._
import cards.nine.app.ui.preferences.commons.WorkspaceAnimations
import cards.nine.commons._
import macroid.FullDsl._
import macroid._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global

abstract class AnimatedWorkSpaces[Holder <: ViewGroup, Data](
    context: Context,
    attr: AttributeSet,
    defStyleAttr: Int)
    extends FrameLayout(context, attr, defStyleAttr)
    with Contexts[View] { self =>

  // First parameter  [Data]    : Current data of the screen
  // Second parameter [Data]    : The data where you go
  // Third parameter  [Boolean] : movement to left?
  // Fourth parameter [Float]   : Fraction of the movement
  type MovementObserver = ((Data, Data, Boolean, Float) => Unit)

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val gestureDetector =
    new GestureDetector(getContext, new GestureDetector.SimpleOnGestureListener() {
      override def onLongPress(e: MotionEvent): Unit = listener.onLongClick()

      override def onSingleTapConfirmed(e: MotionEvent): Boolean = {
        listener.onClick()
        true
      }
    })

  val minVelocity: Int = 250

  val maxRatioVelocity: Int = 3000

  val maxVelocity: Int = 700

  val spaceVelocity: Int = maxVelocity - minVelocity

  val minimumViews = 3

  val positionViewKey = "position-view"

  var listener = new AnimatedWorkSpacesListener

  var data: Seq[Data] = Seq.empty

  private[this] var views: Seq[Holder] = Seq.empty

  var animatedWorkspaceStatuses = AnimatedWorkSpacesStatuses()

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
      animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(displacement = value)
      applyTransforms()
    }
  )

  val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)

  var parentViewOne = slot[FrameLayout]

  var parentViewTwo = slot[FrameLayout]

  var parentViewThree = slot[FrameLayout]

  (self <~ vgAddViews(
    Seq(
      (w[FrameLayout] <~
        wire(parentViewOne) <~
        vAddField(positionViewKey, NextView)).get,
      (w[FrameLayout] <~
        wire(parentViewTwo) <~
        vAddField(positionViewKey, FrontView)).get,
      (w[FrameLayout] <~
        wire(parentViewThree) <~
        vAddField(positionViewKey, PreviousView)).get),
    params)).run

  def animationPref = WorkspaceAnimations.readValue

  def createEmptyView(): Holder

  def createView(viewType: Int): Holder

  def populateView(view: Option[Holder], data: Data, viewType: Int, position: Int): Ui[Any]

  def getItemViewTypeCount: Int = 0

  def getItemViewType(data: Data, position: Int): Int = 0

  def getWorksSpacesCount = data.length

  def getCurrentView: Option[Holder] =
    views.lift(animatedWorkspaceStatuses.currentItem)

  def getView(position: Int): Option[Holder] = views.lift(position)

  def init(
      newData: Seq[Data],
      position: Int = 0,
      forcePopulatePosition: Option[Int] = None): Unit = {

    animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(currentItem = position)

    // We creates the views for our workspace. We have a minimum of views, if our data don't have this minimum,
    // we must create the necessary empty views

    views = (newData.zipWithIndex map {
      case (itemData, index) =>
        val newScreen = data.lift(index).isEmpty
        val sameData  = data.lift(index) contains itemData

        lazy val newView = createView(getItemViewType(itemData, index))

        val selectedView =
          if (newScreen) newView
          else
            views.lift(index) match {
              case Some(oldView) => oldView
              case _             => newView
            }

        (sameData, forcePopulatePosition) match {
          case (true, Some(forceIndex)) if index != forceIndex =>
          case _ =>
            populateView(Some(selectedView), itemData, getItemViewType(itemData, index), index).run
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

  def goToItem(): Int =
    (animatedWorkspaceStatuses.displacement, animatedWorkspaceStatuses.currentItem) match {
      case (disp, item) if disp < 0 && item >= data.size - 1 => 0
      case (disp, item) if disp < 0                          => item + 1
      case (_, item) if item <= 0                            => data.length - 1
      case (_, item)                                         => item - 1
    }

  def notifyPageChangedObservers() =
    onPageChangedObservers foreach (observer => observer(goToItem()))

  def addPageChangedObservers(f: PageChangedObserver) =
    onPageChangedObservers = onPageChangedObservers :+ f

  def notifyMovementObservers(percent: Float) =
    for {
      from <- data.lift(currentPage())
      to   <- data.lift(goToItem())
    } yield {
      onMovementObservers foreach (observer =>
                                     observer(
                                       from,
                                       to,
                                       animatedWorkspaceStatuses.isFromLeft,
                                       percent))
    }

  def addMovementObservers(f: MovementObserver) =
    onMovementObservers = onMovementObservers :+ f

  protected def getSizeWidget = getWidth

  def isPosition(position: Int): Boolean =
    animatedWorkspaceStatuses.currentItem == position

  def currentPage(): Int = animatedWorkspaceStatuses.currentItem

  def isFirst: Boolean = isPosition(0)

  def isLast: Boolean = isPosition(data.length - 1)

  def canGoToPrevious = !isFirst

  def canGoToNext = !isLast

  private[this] def snap(velocity: Float): Ui[Any] = {
    moveItemsAnimator.cancel()
    val destiny = (velocity, animatedWorkspaceStatuses.displacement) match {
      case (v, d) if v > 0 && d > 0  => getSizeWidget
      case (v, d) if v <= 0 && d < 0 => -getSizeWidget
      case _                         => 0
    }
    animateViews(destiny, calculateDurationByVelocity(velocity, durationAnimation))
  }
  protected def calculateDurationByVelocity(velocity: Float, defaultVelocity: Int): Int = {
    velocity match {
      case 0 => defaultVelocity
      case _ =>
        (spaceVelocity - ((math.min(math.abs(velocity), maxRatioVelocity) * spaceVelocity) / maxRatioVelocity) + minVelocity).toInt
    }
  }

  private[this] def snapDestination(): Ui[Any] = {
    val destiny = animatedWorkspaceStatuses.displacement match {
      case d if d > getSizeWidget * .6f  => getSizeWidget
      case d if d < -getSizeWidget * .6f => -getSizeWidget
      case _                             => 0
    }
    animateViews(destiny, durationAnimation)
  }

  private[this] def performScroll(delta: Float): Ui[Any] = {
    moveItemsAnimator.cancel()
    animatedWorkspaceStatuses = animatedWorkspaceStatuses.updateDisplacement(getSizeWidget, delta)
    applyTransforms()
  }

  def selectPosition(position: Int): Unit = {
    animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(currentItem = position)
    (reset() ~ reset()).run // TODO Change that
  }

  def applyTransforms(): Ui[Any] =
    transformOutPanelDefault() ~ transformInPanelDefault()

  private[this] def transformOutPanelDefault(): Ui[Any] =
    getFrontView <~ vTranslationX(animatedWorkspaceStatuses.displacement)

  private[this] def transformInPanelDefault(): Ui[Any] = {
    val percent  = animatedWorkspaceStatuses.percent(getSizeWidget)
    val fromLeft = animatedWorkspaceStatuses.isFromLeft
    val view     = if (fromLeft) getPreviousView else getNextView
    notifyMovementObservers(percent)

    val translate = {
      val start = if (fromLeft) -getSizeWidget else getSizeWidget
      start - (start * percent)
    }

    view <~ vTranslationX(translate)
  }

  private[this] def animateViews(dest: Int, duration: Int): Ui[Any] = {
    animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(swap = dest != 0)
    if (animatedWorkspaceStatuses.swap) notifyPageChangedObservers()
    (self <~ vInvalidate) ~
      (getFrontView <~~ moveItemsAnimator
        .move(animatedWorkspaceStatuses.displacement, dest, duration, attachTarget = true)) ~~
      resetAnimationEnd
  }

  private[this] def swapViews(): Ui[Any] = {
    animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(currentItem = goToItem())
    (this <~ (if (animatedWorkspaceStatuses.isFromLeft)
                reloadPreviousPositionView
              else reloadNextPositionView)) ~
      (if (animatedWorkspaceStatuses.isFromLeft)
         recreate(PreviousView) ~ resetView(NextView)
       else
         recreate(NextView) ~ resetView(PreviousView)) ~
      Ui {
        animatedWorkspaceStatuses = animatedWorkspaceStatuses
          .copy(displacement = 0, enabled = data.nonEmpty && data.length > 1)
      }
  }

  private[this] def resetAnimationEnd(): Ui[Any] =
    (if (animatedWorkspaceStatuses.swap) swapViews() else Ui.nop) ~ layerHardware(true)

  def reset(): Ui[Any] = {
    animatedWorkspaceStatuses =
      animatedWorkspaceStatuses.copy(displacement = 0, enabled = data.nonEmpty && data.length > 1)
    moveItemsAnimator.cancel()
    recreate(FrontView) ~
      recreate(PreviousView) ~
      recreate(NextView)
  }

  def resetItem(position: Int): Ui[Any] =
    getPositionView(position) match {
      case Some(positionView) =>
        val itemData = data(position)
        populateView(
          Option(views(position)),
          itemData,
          getItemViewType(itemData, position),
          position) ~
          recreate(positionView, resetPosition = false)
      case _ => Ui.nop
    }

  private[this] def recreate(positionView: PositionView, resetPosition: Boolean = true): Ui[Any] = {
    val currentItem = animatedWorkspaceStatuses.currentItem

    val position = positionView match {
      case PreviousView =>
        if (currentItem - 1 < 0) views.length - 1 else currentItem - 1
      case NextView =>
        if (currentItem + 1 > views.length - 1) 0 else currentItem + 1
      case FrontView => currentItem
    }

    val view = getView(positionView)

    (view <~
      vgRemoveAllViews <~
      vgAddView(views(position), params)) ~
      (if (resetPosition) resetView(positionView) else Ui.nop)
  }

  def resetView(positionView: PositionView) = {
    val view = getView(positionView)
    val displacement = positionView match {
      case PreviousView => -getSizeWidget
      case NextView     => getSizeWidget
      case FrontView    => 0
    }
    view <~ vTranslationX(displacement) <~ vScaleX(1) <~ vScaleY(1) <~ vAlpha(1)
  }

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(dimen = Dimen(w, h))
  }

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    super.onInterceptTouchEvent(event)
    val (action, x, y) = updateTouch(event)
    (action, animatedWorkspaceStatuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        true
      case (ACTION_MOVE, _) =>
        setStateIfNeeded(x, y)
        !animatedWorkspaceStatuses.enabled || animatedWorkspaceStatuses.isScrolling
      case (ACTION_DOWN, _) =>
        animatedWorkspaceStatuses =
          animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
        animatedWorkspaceStatuses.isScrolling
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
        animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(touchState = Stopped)
        !animatedWorkspaceStatuses.enabled || animatedWorkspaceStatuses.isScrolling
      case _ =>
        !animatedWorkspaceStatuses.enabled || animatedWorkspaceStatuses.isScrolling
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    super.onTouchEvent(event)
    val (action, x, y) = updateTouch(event)
    gestureDetector.onTouchEvent(event)
    (action, animatedWorkspaceStatuses.touchState) match {
      case (ACTION_MOVE, Scrolling) =>
        requestDisallowInterceptTouchEvent(true)
        val deltaX = animatedWorkspaceStatuses.deltaX(x)
        animatedWorkspaceStatuses =
          animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
        if (overScroll(deltaX)) {
          notifyMovementObservers(0f)
          resetView(FrontView).run
        } else {
          performScroll(deltaX).run
        }
      case (ACTION_MOVE, Stopped) => setStateIfNeeded(x, y)
      case (ACTION_DOWN, _) =>
        animatedWorkspaceStatuses =
          animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
      case (ACTION_CANCEL | ACTION_UP, _) =>
        computeFling()
        animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(touchState = Stopped)
      case _ =>
    }
    true
  }

  protected def updateTouch(event: MotionEvent) = {
    if (animatedWorkspaceStatuses.velocityTracker.isEmpty)
      animatedWorkspaceStatuses =
        animatedWorkspaceStatuses.copy(velocityTracker = Some(VelocityTracker.obtain()))
    animatedWorkspaceStatuses.velocityTracker foreach (_.addMovement(event))
    val action = MotionEventCompat.getActionMasked(event)
    val x      = MotionEventCompat.getX(event, 0)
    val y      = MotionEventCompat.getY(event, 0)
    (action, x, y)
  }

  private[this] def overScroll(deltaX: Float): Boolean =
    (animatedWorkspaceStatuses.displacement, deltaX) match {
      case (x, dx) if isFirst && dx < 0 && x - dx >= 0 => true
      case (x, dx) if isLast && dx > 0 && x - dx <= 0  => true
      case _                                           => false
    }

  def setStateIfNeeded(x: Float, y: Float) = {
    if (animatedWorkspaceStatuses.enabled) {
      val xDiff = math.abs(x - animatedWorkspaceStatuses.lastMotionX)
      val yDiff = math.abs(y - animatedWorkspaceStatuses.lastMotionY)

      val xMoved = xDiff > touchSlop
      val yMoved = yDiff > touchSlop

      if (xMoved || yMoved) {
        val penultimate = data.length - 2
        val isScrolling = (xDiff > yDiff, moveItemsAnimator.isRunning) match {
          case (true, true) if x - animatedWorkspaceStatuses.lastMotionX > 0 && isPosition(1) =>
            false
          case (true, true)
              if x - animatedWorkspaceStatuses.lastMotionX < 0 && isPosition(penultimate) =>
            false
          case (false, true) if y - animatedWorkspaceStatuses.lastMotionY > 0 && isPosition(1) =>
            false
          case (false, true)
              if y - animatedWorkspaceStatuses.lastMotionY < 0 && isPosition(penultimate) =>
            false
          case (true, _) if x - animatedWorkspaceStatuses.lastMotionX > 0 && !isFirst =>
            true
          case (true, _) if x - animatedWorkspaceStatuses.lastMotionX < 0 && !isLast =>
            true
          case (false, _) if y - animatedWorkspaceStatuses.lastMotionY > 0 && !isFirst =>
            true
          case (false, _) if y - animatedWorkspaceStatuses.lastMotionY < 0 && !isLast =>
            true
          case _ => false
        }
        if (isScrolling) {
          animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(touchState = Scrolling)
          layerHardware(true).run
        }
        animatedWorkspaceStatuses =
          animatedWorkspaceStatuses.copy(lastMotionX = x, lastMotionY = y)
      }
    }
  }

  private[this] def layerHardware(activate: Boolean) =
    (getFrontView <~ vLayerHardware(activate = activate)) ~
      (getNextView <~ vLayerHardware(activate = activate)) ~
      (getPreviousView <~ vLayerHardware(activate = activate))

  private[this] def computeFling() =
    animatedWorkspaceStatuses.velocityTracker foreach { tracker =>
      tracker.computeCurrentVelocity(1000, maximumVelocity)
      val velocity = tracker.getXVelocity
      ((animatedWorkspaceStatuses.isScrolling, math.abs(velocity) > minimumVelocity) match {
        case (true, true) => snap(velocity)
        case _            => snapDestination()
      }).run
      tracker.recycle()
      animatedWorkspaceStatuses = animatedWorkspaceStatuses.copy(velocityTracker = None)
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
      case _                            => None
    }
  }

  private[this] def getPositionView(position: Int): Option[PositionView] = {
    val currentItem = currentPage()
    if (currentItem == position) {
      Option(FrontView)
    } else if (currentItem - 1 == position) {
      Option(PreviousView)
    } else if (currentItem + 1 == position) {
      Option(NextView)
    } else {
      None
    }
  }

}

case class AnimatedWorkSpacesStatuses(
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

  def totalXPercent(size: Int, numberOfScreens: Int): Float = {
    val stepX = math.abs(displacement) / size
    val curX  = currentItem + (if (isFromLeft) -stepX else stepX)
    curX / numberOfScreens
  }

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
