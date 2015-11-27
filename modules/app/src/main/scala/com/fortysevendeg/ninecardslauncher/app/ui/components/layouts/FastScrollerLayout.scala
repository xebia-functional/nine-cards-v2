package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.{Drawable, GradientDrawable}
import android.os.Build.VERSION._
import android.os.Build.VERSION_CODES._
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.{View, MotionEvent, LayoutInflater, Gravity}
import android.view.ViewGroup.LayoutParams._
import android.widget.{LinearLayout, FrameLayout}
import android.widget.FrameLayout.LayoutParams
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView, R}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.FullDsl._
import macroid.{Ui, Tweak}

class FastScrollerLayout(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  lazy val fastScroller = Option(new FastScrollerView(context))

  override def onFinishInflate(): Unit = {
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
    context.getResources.getDrawable(res, javaNull)
  }

}

class FastScrollerView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attr, defStyleAttr)
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  private[this] var recyclerView = slot[RecyclerView]

  private[this] var scrollListener: Option[ScrollListener] = None

  var statuses = new FastScrollerStatuses

  setOrientation(LinearLayout.HORIZONTAL)

  setClipChildren(false)

  LayoutInflater.from(context).inflate(R.layout.fastscroller, this)

  val bar = Option(findView(TR.fastscroller_bar))

  val signal = Option(findView(TR.fastscroller_signal))

  val text = Option(findView(TR.fastscroller_signal_text))

  val barOff = if (SDK_INT < LOLLIPOP_MR1) {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_off)
  } else {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_off, javaNull)
  }

  var barOn = if (SDK_INT < LOLLIPOP_MR1) {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_on)
  } else {
    context.getResources.getDrawable(R.drawable.fastscroller_bar_on, javaNull)
  }

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    statuses = statuses.copy(height = h)
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    val action = MotionEventCompat.getActionMasked(event)
    val y = MotionEventCompat.getY(event, 0)
    action match {
      case ACTION_DOWN =>
        statuses = statuses.startScroll()
        true
      case ACTION_MOVE =>
        runUi(changePosition(y) ~
          showSignal ~
          (recyclerView <~ rvScrollToPosition(y)))
        true
      case ACTION_UP | ACTION_CANCEL =>
        statuses = statuses.resetScroll()
        runUi(hideSignal)
        // Update scroll position in ScrollListener
        scrollListener foreach (_.y = statuses.projectToRecycler(event.getY))
        true
      case _ => super.onTouchEvent(event)
    }
  }

  def show: Ui[_] = (signal <~ vGone) ~ (bar <~ vVisible)

  def hide: Ui[_] = (signal <~ vGone) ~ (bar <~ vGone)

  def showSignal: Ui[_] = (signal <~ vVisible) ~ (bar <~ ivSrc(barOn))

  def hideSignal: Ui[_] = (signal <~ vGone) ~ (bar <~ ivSrc(barOff))

  def setRecyclerView(rv: RecyclerView) = {
    statuses = statuses.setTotalHeight(rv, getHeight)
    scrollListener foreach rv.removeOnScrollListener
    val sl = new ScrollListener
    scrollListener = Option(sl)
    rv.addOnScrollListener(sl)
    recyclerView = Option(rv)
  }

  def reset() = {
    recyclerView foreach (rv => statuses = statuses.setTotalHeight(rv, getHeight))
    scrollListener foreach (_.y = 0)
    runUi(changePosition(0))
  }

  private[this] def changePosition(y: Float): Ui[_] = {
    val position = y / statuses.height
    (bar <~ vChangeY(position)) ~ (signal <~ vChangeY(position))
  }

  private[this] def vChangeY(position: Float) = Tweak[View]{ view =>
    val viewHeight = view.getHeight
    val value = ((statuses.height - viewHeight) * position).toInt
    val max = statuses.height - viewHeight
    val minimum = math.max(0, value)
    view.setY(math.min(minimum, max))
  }

  private[this] def rvScrollToPosition(y: Float) = Tweak[RecyclerView]{ view =>
    val itemCount = view.getAdapter.getItemCount
    val position = ((y * itemCount) / statuses.height).toInt
    if (position != statuses.lastScrollToPosition) {
      statuses = statuses.copy(lastScrollToPosition = position)
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

    override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit = if (!statuses.moving) {
      y = y + dy
      runUi(changePosition(statuses.projectToBar(y)))
    }

  }

}

case class FastScrollerStatuses(
  height: Int = 0,
  totalHeight: Int = 0,
  moving: Boolean = false,
  lastScrollToPosition: Int = -1) {

  def setTotalHeight(recyclerView: RecyclerView, height: Int): FastScrollerStatuses = copy(
    totalHeight = Option(recyclerView.getAdapter) match {
      case Some(listener: FastScrollerListener) => listener.getHeight - height
      case _ => 0
    })

  def startScroll(): FastScrollerStatuses = copy(moving = true)

  def resetScroll(): FastScrollerStatuses = copy(
    lastScrollToPosition = -1,
    moving = false)

  def projectToBar(y: Float) = (y * height) / totalHeight

  def projectToRecycler(y: Float) = (y * totalHeight) / height
}

trait FastScrollerListener {

  def getHeight: Int

  def getElement(position: Int): Option[String]

}