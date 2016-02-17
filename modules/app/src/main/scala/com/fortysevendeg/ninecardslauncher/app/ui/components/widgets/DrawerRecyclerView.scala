package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.support.v4.view.{MotionEventCompat, ViewConfigurationCompat}
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.{VelocityTracker, MotionEvent, ViewConfiguration}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.{TranslationAnimator, Scrolling, Stopped, ViewState}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{FastScrollerTransformsListener, SearchBoxAnimatedController}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.snails.HighlightSnails
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class DrawerRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr)
  with FastScrollerTransformsListener { self =>

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  val computeUnitsTracker = 1000

  val durationAnimation = resGetInteger(android.R.integer.config_shortAnimTime)

  var drawerRecyclerListener = DrawerRecyclerViewListener()

  var animatedController: Option[SearchBoxAnimatedController] = None

  var statuses = DrawerRecyclerStatuses()

  val mainAnimator = new TranslationAnimator(
    update = (value: Float) => {
      statuses = statuses.copy(displacement = value)
      transformPanelCanvas(value)
    })

  val (touchSlop, maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (ViewConfigurationCompat.getScaledPagingTouchSlop(configuration),
      configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = statuses.disableScroll || super.dispatchTouchEvent(ev)

  addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
    override def onTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Unit = {
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      initVelocityTracker(event)
      (MotionEventCompat.getActionMasked(event), statuses.touchState) match {
        case (ACTION_MOVE, Scrolling) =>
          requestDisallowInterceptTouchEvent(true)
          val delta = statuses.deltaX(x)
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          runUi(
            movementByOverScroll(delta) ~
              drawerRecyclerListener.move(delta))
        case (ACTION_MOVE, Stopped) =>
          setStateIfNeeded(x, y)
        case (ACTION_DOWN, _) =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
        case (ACTION_CANCEL | ACTION_UP, _) =>
          reset()
        case _ =>
      }
    }

    override def onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean = {
      initVelocityTracker(event)
      val x = MotionEventCompat.getX(event, 0)
      val y = MotionEventCompat.getY(event, 0)
      (MotionEventCompat.getActionMasked(event), statuses.touchState) match {
        case (ACTION_MOVE, Scrolling) =>
          requestDisallowInterceptTouchEvent(true)
          true
        case (ACTION_MOVE, _) =>
          setStateIfNeeded(x, y)
          statuses.touchState != Stopped
        case (ACTION_DOWN, _) =>
          statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
          false
        case (ACTION_CANCEL | ACTION_UP, _) =>
          reset()
          statuses.touchState != Stopped
        case _ => statuses.touchState != Stopped
      }
    }

    override def onRequestDisallowInterceptTouchEvent(b: Boolean): Unit = {}
  })

  private[this] def reset() = {
    computeFling()
    runUi(drawerRecyclerListener.end())
    statuses = statuses.copy(touchState = Stopped)
    blockScroll(false)
  }

  private[this] def setStateIfNeeded(x: Float, y: Float) = {
    val xDiff = math.abs(x - statuses.lastMotionX)
    val yDiff = math.abs(y - statuses.lastMotionY)

    val xMoved = xDiff > touchSlop

    if (xMoved) {
      val isScrolling = (xDiff > yDiff) && !mainAnimator.isRunning && statuses.enabled
      if (isScrolling) {
        animatedController foreach (controller => runUi(controller.startMovement))
        runUi(drawerRecyclerListener.start())
        statuses = statuses.copy(touchState = Scrolling)
        blockScroll(true)
      }
      statuses = statuses.copy(lastMotionX = x, lastMotionY = y)
    }
  }

  private[this] def blockScroll(bs: Boolean) = getLayoutManager match {
    case lm: ScrollingLinearLayoutManager => lm.blockScroll = bs
    case _ =>
  }

  override def inactiveItems: Ui[_] =
    getLayoutManager match {
      case lm: LinearLayoutManager =>
        Ui.sequence(0 to getChildCount map { item =>
          val view = Option(getChildAt(item))
          view <~ HighlightSnails.opaque
        }:_*)
      case _ => Ui.nop
    }

  def movementByOverScroll(delta: Float): Ui[_] = if (overScroll(delta)) {
    transformPanelCanvas(0f)
  } else {
    performScroll(delta)
  }

  def overScroll(delta: Float) = animatedController exists(_.overScroll(delta))

  private[this] def performScroll(delta: Float): Ui[_] = {
    mainAnimator.cancel()
    statuses = statuses.copy(displacement = statuses.calculateDisplacement(getWidth, delta))
    transformPanelCanvas(statuses.displacement)
  }

  private[this] def transformPanelCanvas(position: Float) =
    animatedController map (_.updateMovement(position)) getOrElse Ui.nop

  private[this] def initVelocityTracker(event: MotionEvent): Unit = {
    if (statuses.velocityTracker.isEmpty) statuses = statuses.copy(velocityTracker = Some(VelocityTracker.obtain()))
    statuses.velocityTracker foreach (_.addMovement(event))
  }

  private[this] def computeFling() = {
    statuses.velocityTracker foreach { tracker =>
      tracker.computeCurrentVelocity(computeUnitsTracker, maximumVelocity)
      if (statuses.touchState == Scrolling && !overScroll(-tracker.getXVelocity)) {
        val velocity = tracker.getXVelocity
        runUi(if (math.abs(velocity) > minimumVelocity) snap(velocity) else snapDestination())
      }
      tracker.recycle()
      statuses = statuses.copy(velocityTracker = None)
    }
    statuses = statuses.copy(touchState = Stopped)
  }

  private[this] def snap(velocity: Float): Ui[_] = {
    mainAnimator.cancel()
    val destiny = (velocity, statuses.displacement) match {
      case (v, d) if v > 0 && d > 0 => getWidth
      case (v, d) if v <= 0 && d < 0 => -getWidth
      case _ => 0
    }
    animateViews(destiny, calculateDurationByVelocity(velocity, durationAnimation))
  }

  private[this] def snapDestination(): Ui[_] = {
    val destiny = statuses.displacement match {
      case d if d > getWidth * .6f => getWidth
      case d if d < -getWidth * .6f => -getWidth
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
  }

  private[this] def animateViews(dest: Int, duration: Int): Ui[_] = {
    statuses = statuses.copy(swap = dest != 0)
    (self <~
      vInvalidate <~~
      mainAnimator.move(statuses.displacement, dest, duration)) ~~
      finishMovement
  }

  private[this] def finishMovement: Ui[_] =
    (animatedController map (_.resetAnimationEnd(statuses.swap)) getOrElse Ui.nop) ~
      Ui(statuses = statuses.copy(displacement = 0f))

}

case class DrawerRecyclerViewListener(
  start: () => Ui[_] = () => Ui.nop,
  move: (Float) => Ui[_] = (_) => Ui.nop,
  end: () => Ui[_] = () => Ui.nop)

case class DrawerRecyclerStatuses(
  disableScroll: Boolean = false,
  lastMotionX: Float = 0,
  lastMotionY: Float = 0,
  displacement: Float = 0,
  enabled: Boolean = true,
  velocityTracker: Option[VelocityTracker] = None,
  swap: Boolean = false,
  touchState: ViewState = Stopped) {

  def deltaX(x: Float): Float = lastMotionX - x

  def deltaY(y: Float): Float = lastMotionY - y

  def calculateDisplacement(width: Int, delta: Float): Float = math.max(-width, Math.min(width, displacement - delta))

  def calculatePercent(width: Int) = math.abs(displacement) / width

}
