package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager._
import android.support.v7.widget.LinearSmoothScroller._
import android.support.v7.widget.RecyclerView.State
import android.support.v7.widget.{LinearLayoutManager, LinearSmoothScroller, RecyclerView}
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerTransformsListener
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Ui
import macroid.FullDsl._

trait ScrollingLinearLayoutManager {

  self: LinearLayoutManager =>

  var blockScroll = false

  override def smoothScrollToPosition(recyclerView: RecyclerView, state: State, position: Int): Unit = {
    val smoothScroller = new TopSmoothScroller(recyclerView, isSmoothScrolling)
    smoothScroller.setTargetPosition(position)
    startSmoothScroll(smoothScroller)
  }

  override def canScrollVertically: Boolean = if (blockScroll) false else getOrientation == VERTICAL

  class TopSmoothScroller(
    recyclerView: RecyclerView,
    var flagScrolling: Boolean) // If the previous call is scrolling, we don't want to onStop in startSmoothScroll
    extends LinearSmoothScroller(recyclerView.getContext) {

    def computeScrollVectorForPosition(targetPosition: Int): PointF = self.computeScrollVectorForPosition(targetPosition)

    protected override def getVerticalSnapPreference: Int = SNAP_TO_START

    protected override def calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
      12f / displayMetrics.densityDpi.toFloat

    override def onStop(): Unit = {
      super.onStop()
      if (flagScrolling) {
        flagScrolling = false
      } else {
        runUi(recyclerView match {
          case listener: FastScrollerTransformsListener =>
            Option(recyclerView.getTag(R.id.max)) map { max =>
              listener.feedbackItems(getTargetPosition, Int.unbox(max))
            } getOrElse Ui.nop
          case _ => Ui.nop
        })
      }
    }
  }

}
