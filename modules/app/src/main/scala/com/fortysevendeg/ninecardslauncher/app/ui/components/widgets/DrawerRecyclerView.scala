package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import android.view.animation.GridLayoutAnimationController.AnimationParameters
import android.view.{MotionEvent, View}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AnimationsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.commons._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class DrawerRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends RecyclerView(context, attr, defStyleAttr)
  with Contexts[View] { self =>

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val durationAnimation = resGetInteger(android.R.integer.config_shortAnimTime)

  var drawerRecyclerListener = DrawerRecyclerViewListener()

  var statuses = DrawerRecyclerStatuses()

  val horizontalMovementListener = HorizontalMovementListener(
    start = () => {
      drawerRecyclerListener.start().run
      statuses = statuses.copy(disableClickItems = true)
      blockScroll(true)
    },
    end = (swiping: Swiping, displacement: Int) => snap(swiping).run,
    scroll = (deltaX: Int) => {
      if (!overScroll(deltaX)) {
        statuses = statuses.move(deltaX)
        offsetLeftAndRight(deltaX)
        drawerRecyclerListener.move(statuses.displacement).run
      }
    })

  val mainAnimator = new TranslationAnimator(
    update = (value: Float) => Ui {
      val offset = statuses.displacement.toInt - value.toInt
      statuses = statuses.copy(displacement = statuses.displacement.toInt - offset)
      offsetLeftAndRight(offset)
      invalidate()
    } ~ drawerRecyclerListener.move(statuses.displacement),
    end = () => Ui {
      statuses = statuses.copy(disableClickItems = false)
      blockScroll(false)
    })

  addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
    override def onTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Unit = {}

    override def onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean = {
      requestDisallowInterceptTouchEvent(statuses.disableClickItems)
      statuses.disableClickItems
    }

    override def onRequestDisallowInterceptTouchEvent(b: Boolean): Unit = {}
  })

  override def attachLayoutAnimationParameters(child: View, params: LayoutParams, index: Int, count: Int): Unit =
    Option(getLayoutManager) match {
      case (Some(layoutManager: GridLayoutManager)) =>
        val animationParams = Option(params.layoutAnimationParameters) match {
          case Some(animParams: AnimationParameters) => animParams
          case _ =>
            val animParams = new AnimationParameters()
            params.layoutAnimationParameters = animParams
            animParams
        }
        val columns = layoutManager.getSpanCount
        animationParams.count = count
        animationParams.index = index
        animationParams.columnsCount = columns
        animationParams.rowsCount = count / columns
        val invertedIndex = count - 1 - index
        animationParams.column = columns - 1 - (invertedIndex % columns)
        animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns
      case _ => super.attachLayoutAnimationParameters(child, params, index, count)
    }

  private[this] def blockScroll(bs: Boolean) = getLayoutManager match {
    case lm: ScrollingLinearLayoutManager => lm.blockScroll = bs
    case _ =>
  }

  private[this] def snap(swiping: Swiping): Ui[_] = {
    mainAnimator.cancel()
    val (destiny, velocity) = (swiping, statuses.contentView, statuses.displacement) match {
      case (SwipeRight(_), AppsView, d) if d > 0 =>
        (getWidth, calculateDurationByVelocity(swiping.getVelocity, durationAnimation))
      case (SwipeLeft(_), ContactView, d) if d < 0 =>
        (-getWidth, calculateDurationByVelocity(swiping.getVelocity, durationAnimation))
      case (NoSwipe(), AppsView, d) if d > getWidth * .6f =>
        (getWidth, durationAnimation)
      case (NoSwipe(), ContactView, d) if d < -getWidth * .6f =>
        (-getWidth, durationAnimation)
      case _ =>
        (0, durationAnimation)
    }
    statuses = statuses.copy(swap = destiny != 0)
    animateViews(destiny, velocity)
  }

  private[this] def animateViews(dest: Int, duration: Int): Ui[_] = {
    statuses = statuses.copy(swap = dest != 0)
    (self <~
      vInvalidate <~~
      mainAnimator.move(statuses.displacement, dest, duration)) ~~
      finishMovement(duration)
  }

  private[this] def finishMovement(duration: Int): Ui[_] = {
    val contentView = (statuses.swap, statuses.contentView) match {
      case (true, AppsView) => ContactView
      case (true, ContactView) => AppsView
      case (false, view) => view
    }
    (if (statuses.contentView != contentView) drawerRecyclerListener.changeContentView(contentView) else Ui.nop) ~
      Ui (statuses = statuses.reset) ~ drawerRecyclerListener.end(duration)
  }

  private[this] def overScroll(deltaX: Float): Boolean = (statuses.contentView, statuses.displacement, deltaX) match {
    case (AppsView, x, d) if positiveTranslation(x, d) => false
    case (ContactView, x, d) if !positiveTranslation(x, d) => false
    case _ => true
  }

  private[this] def positiveTranslation(translation: Float, delta: Float): Boolean =
    translation > 0 || (translation == 0 && delta < 0)

}

case class DrawerRecyclerViewListener(
  start: () => Ui[_] = () => Ui.nop,
  move: (Float) => Ui[_] = (_) => Ui.nop,
  end: (Int) => Ui[_] = (_) => Ui.nop,
  changeContentView: (ContentView) => Ui[_] = (_) => Ui.nop)

case class DrawerRecyclerStatuses(
  contentView: ContentView = AppsView,
  lastTimeContentViewWasChanged: Boolean = false,
  disableClickItems: Boolean = false,
  displacement: Float = 0,
  swap: Boolean = false) {

  def move(deltaX: Int) = copy(displacement = displacement - deltaX)

  def reset: DrawerRecyclerStatuses =
    copy(displacement = 0f, swap = false)

}

sealed trait ContentView

case object AppsView extends ContentView

case object ContactView extends ContentView