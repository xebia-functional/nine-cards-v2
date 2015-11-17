package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.graphics.drawable.{Drawable, GradientDrawable}
import android.os.Build.VERSION._
import android.os.Build.VERSION_CODES._
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, LayoutInflater, MotionEvent, View}
import android.widget.FrameLayout.LayoutParams
import android.widget.{FrameLayout, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, Ui}

class FastScrollerLayout(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  lazy val fastScroller = Option(new FastScrollerView(context))

  override def onFinishInflate(): Unit = {
    if (!getChildAt(0).isInstanceOf[RecyclerView]) {
      throw new IllegalStateException("FastScrollerLayout must contain a RecyclerView")
    }
    fastScroller map { fs =>
      val ll = new LayoutParams(WRAP_CONTENT, MATCH_PARENT)
      ll.gravity = Gravity.RIGHT
      runUi(this <~ vgAddView(fs, ll))
    }
    super.onFinishInflate()
  }

  def linkRecycler() = Option(getChildAt(0)) match {
    case Some(rv: RecyclerView) => runUi(fastScroller <~ fsRecyclerView(rv))
    case _ =>
  }

  def setColor(color: Int) = runUi(fastScroller <~ fsColor(color))

  def reset = runUi(fastScroller <~ fsReset)

  private[this] def fsReset = Tweak[FastScrollerView](_.reset())

  private[this] def fsRecyclerView(rv: RecyclerView) = Tweak[FastScrollerView](view => view.setRecyclerView(rv))

  private[this] def fsColor(color: Int) = Tweak[FastScrollerView] { view =>
    view.barOn = changeColor(R.drawable.fastscroller_bar_on, color)
    runUi(view.signal <~ Tweak[FrameLayout](_.setBackground(changeColor(R.drawable.fastscroller_signal, color))))
  }

  private[this] def changeColor(res: Int, color: Int): Drawable = getDrawable(res) match {
    case d: GradientDrawable =>
      d.setColor(color)
      d
    case d => d
  }

  private[this] def getDrawable(res: Int): Drawable = if (SDK_INT < LOLLIPOP_MR1) {
    context.getResources.getDrawable(res)
  } else {
    context.getResources.getDrawable(res, null)
  }

}

class FastScrollerView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  private[this] var recyclerView = slot[RecyclerView]

  private[this] var scrollListener: Option[ScrollListener] = None

  private[this] val indicator = new FastScrollerIndicator

  setOrientation(LinearLayout.HORIZONTAL)

  setClipChildren(false)

  LayoutInflater.from(context).inflate(R.layout.fastscroller, this)

  val bar = Option(findView(TR.fastscroller_bar))

  val signal = Option(findView(TR.fastscroller_signal))

  val text = Option(findView(TR.fastscroller_signal_text))

  val barOff = if (SDK_INT < LOLLIPOP_MR1) {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_off)
  } else {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_off, null)
  }

  var barOn = if (SDK_INT < LOLLIPOP_MR1) {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_on)
  } else {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_on, null)
  }

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    indicator.height = h
  }

  override def onTouchEvent(event: MotionEvent): Boolean = event.getAction match {
    case ACTION_DOWN | ACTION_MOVE =>
      indicator.startScroll()
      val y = event.getY
      runUi(changePosition(y) ~
        showSignal ~
        (recyclerView <~ rvScrollToPosition(y)))
      true
    case ACTION_UP | ACTION_CANCEL =>
      indicator.resetScroll()
      runUi(hideSignal)
      // Update scroll position in ScrollListener
      scrollListener foreach (_.y = indicator.projectToRecycler(event.getY))
      true
    case _ => super.onTouchEvent(event)
  }

  def show: Ui[_] = (signal <~ vGone) ~ (bar <~ vVisible)

  def hide: Ui[_] = (signal <~ vGone) ~ (bar <~ vGone)

  def showSignal: Ui[_] = (signal <~ vVisible) ~ (bar <~ ivSrc(barOn))

  def hideSignal: Ui[_] = (signal <~ vGone) ~ (bar <~ ivSrc(barOff))

  def setRecyclerView(rv: RecyclerView) = {
    indicator.setTotalHeight(rv, getHeight)
    scrollListener foreach rv.removeOnScrollListener
    val sl = new ScrollListener
    scrollListener = Option(sl)
    rv.addOnScrollListener(sl)
    recyclerView = Option(rv)
  }

  def reset() = {
    recyclerView foreach (rv => indicator.setTotalHeight(rv, getHeight))
    scrollListener foreach (_.y = 0)
    runUi(changePosition(0))
  }

  private[this] def changePosition(y: Float): Ui[_] = {
    val position = y / indicator.height
    (bar <~ vChangeY(position)) ~ (signal <~ vChangeY(position))
  }

  private[this] def vChangeY(position: Float) = Tweak[View]{ view =>
    val viewHeight = view.getHeight
    val value = ((indicator.height - viewHeight) * position).toInt
    val max = indicator.height - viewHeight
    val minimum = math.max(0, value)
    view.setY(math.min(minimum, max))
  }

  private[this] def rvScrollToPosition(y: Float) = Tweak[RecyclerView]{ view =>
    val itemCount = view.getAdapter.getItemCount
    val position = ((y * itemCount) / indicator.height).toInt
    if (position != indicator.lastScrollToPosition) {
      indicator.lastScrollToPosition = position
      view.scrollToPosition(position)
      val element = Option(view.getAdapter) match {
        case Some(listener: FastScrollerListener) => listener.getElement(position)
        case _ => None
      }
      val ui = element map { e =>
        val tag = if (e.length > 1) e.substring(0, 1) else e
        (text <~ tvText(tag)) ~ (signal <~ vVisible)
      } getOrElse signal <~ vGone
      runUi(ui)
    }
  }

  class ScrollListener
    extends OnScrollListener {
    var y = 0f

    override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit = if (!indicator.moving) {
      y = y + dy
      runUi(changePosition(indicator.projectToBar(y)))
    }

  }

}

case class FastScrollerIndicator(
  var height: Int = 0,
  var totalHeight: Int = 0,
  var moving: Boolean = false,
  var lastScrollToPosition: Int = -1) {

  def setTotalHeight(recyclerView: RecyclerView, height: Int) = {
    totalHeight = Option(recyclerView.getAdapter) match {
      case Some(listener: FastScrollerListener) => listener.getHeight - height
      case _ => 0
    }
  }

  def startScroll() = moving = true

  def resetScroll() = {
    lastScrollToPosition = -1
    moving = false
  }

  def projectToBar(y: Float) = (y * height) / totalHeight

  def projectToRecycler(y: Float) = (y * totalHeight) / height
}

trait FastScrollerListener {

  def getHeight: Int

  def getElement(position: Int): Option[String]

}

object FastScrollerLayoutTweak {
  // We should launch this tweak when the adapter has been added
  def fslLinkRecycler = Tweak[FastScrollerLayout](_.linkRecycler())

  def fslColor(color: Int) = Tweak[FastScrollerLayout](_.setColor(color))

  def fslInvisible = Tweak[FastScrollerLayout]{ view =>
    runUi(view.fastScroller map (fs => fs.hide) getOrElse Ui.nop)
  }

  def fslVisible = Tweak[FastScrollerLayout]{ view =>
    runUi(view.fastScroller map (fs => fs.show) getOrElse Ui.nop)
  }

  def fslReset = Tweak[FastScrollerLayout](_.reset)

}

