package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.util.AttributeSet
import android.view.MotionEvent._
import android.view.{LayoutInflater, MotionEvent, View}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, Ui}

class FastScroller(context: Context, attr: AttributeSet, defStyleAttr: Int)
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

  val bubble = Option(findView(TR.fastscroller_bubble))

  val handle = Option(findView(TR.fastscroller_handle))

  override def onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int): Unit = {
    super.onSizeChanged(w, h, oldw, oldh)
    indicator.height = h
  }

  override def onTouchEvent(event: MotionEvent): Boolean = event.getAction match {
    case ACTION_DOWN | ACTION_MOVE =>
      indicator.startScroll()
      val y = event.getY
      runUi(changePosition(y) ~
        (handle <~ vVisible) ~
        (recyclerView <~ rvScrollToPosition(y)))
      true
    case ACTION_UP | ACTION_CANCEL =>
      indicator.resetScroll()
      runUi(handle <~ vGone)
      // Update scroll position in ScrollListener
      scrollListener foreach (_.y = indicator.projectToRecycler(event.getY))
      true
    case _ => super.onTouchEvent(event)
  }

  def setRecyclerView(rv: RecyclerView) = {
    indicator.setTotalHeight(rv)
    val sl = new ScrollListener
    scrollListener = Option(sl)
    rv.addOnScrollListener(sl)
    recyclerView = Option(rv)
  }

  private[this] def changePosition(y: Float): Ui[_] = {
    val position = y / indicator.height
    (bubble <~ vChangeY(position)) ~ (handle <~ vChangeY(position))
  }

  private[this] def getValueInRange(min: Int, max: Int, value: Int): Int = {
    val minimum = math.max(min, value)
    math.min(minimum, max)
  }

  private[this] def vChangeY(position: Float) = Tweak[View]{
    view =>
      val viewHeight = view.getHeight
      view.setY(getValueInRange(0, indicator.height - viewHeight, ((indicator.height - viewHeight) * position).toInt))
  }

  private[this] def rvScrollToPosition(y: Float) = Tweak[RecyclerView]{
    view =>
      val itemCount = view.getAdapter.getItemCount
      val position = ((y * itemCount) / indicator.height).toInt
      if (position != indicator.lastScrollToPosition) {
        indicator.lastScrollToPosition = position
        view.scrollToPosition(position)
      }
  }

  class ScrollListener
    extends OnScrollListener {
    var y = 0f

    override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit = {
      if (!indicator.moving) {
        y = y + dy
        runUi(changePosition(indicator.projectToBar(y)))
      }
    }

  }

}

case class FastScrollerIndicator(
  var height: Int = 0,
  var totalHeight: Int = 0,
  var moving: Boolean = false,
  var lastScrollToPosition: Int = -1) {

  def setTotalHeight(recyclerView: RecyclerView) = {
    totalHeight = Option(recyclerView.getAdapter) match {
      case Some(adapter: FastScrollerListener) => adapter.getHeight
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

}

object FastScrollerTweak {
  def fsRecyclerView(rv: Option[RecyclerView]) = Tweak[FastScroller](view => rv foreach view.setRecyclerView)
}

