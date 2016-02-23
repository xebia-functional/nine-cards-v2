package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.TranslationAnimator
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.commons._
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class DrawerRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) { self =>

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  val computeUnitsTracker = 1000

  val durationAnimation = resGetInteger(android.R.integer.config_shortAnimTime)

  var drawerRecyclerListener = DrawerRecyclerViewListener()

  var animatedController: Option[SearchBoxAnimatedController] = None

  var statuses = DrawerRecyclerStatuses()

  val horizontalMovementListener = HorizontalMovementListener(
    start = () => {
      animatedController foreach (controller => runUi(controller.startMovement))
      runUi(drawerRecyclerListener.start())
      statuses = statuses.copy(disableClickItems = true)
      blockScroll(true)
    },
    end = (swiping: Swiping, displacement: Int) => {
      statuses = statuses.copy(disableClickItems = false, displacement = displacement)
      runUi(
        drawerRecyclerListener.end() ~
          (swiping match {
            case NoSwipe() => snapDestination()
            case s => snap(s)
          }))
      blockScroll(false)
    },
    scroll = (deltaX: Int) => {
      offsetLeftAndRight(deltaX)
      runUi(drawerRecyclerListener.move(-deltaX))
    })

  val mainAnimator = new TranslationAnimator(
    update = (value: Float) => Ui {
      val offset = statuses.displacement.toInt - value.toInt
      statuses = statuses.copy(displacement = statuses.displacement.toInt - offset)
      offsetLeftAndRight(offset)
      invalidate()
    })

  addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
    override def onTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Unit = {}

    override def onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean = {
      requestDisallowInterceptTouchEvent(statuses.disableClickItems)
      statuses.disableClickItems
    }

    override def onRequestDisallowInterceptTouchEvent(b: Boolean): Unit = {}
  })

  private[this] def blockScroll(bs: Boolean) = getLayoutManager match {
    case lm: ScrollingLinearLayoutManager => lm.blockScroll = bs
    case _ =>
  }

  private[this] def snap(swiping: Swiping): Ui[_] = {
    mainAnimator.cancel()
    val destiny = (swiping, statuses.displacement) match {
      case (SwipeRight(_), d) if d > 0 => getWidth
      case (SwipeLeft(_), d) if d < 0 => -getWidth
      case _ => 0
    }
    animateViews(destiny, calculateDurationByVelocity(swiping.getVelocity, durationAnimation))
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
  end: () => Ui[_] = () => Ui.nop
)

case class DrawerRecyclerStatuses(
  disableClickItems: Boolean = false,
  displacement: Float = 0,
  swap: Boolean = false)
