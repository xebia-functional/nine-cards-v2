package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager._
import android.support.v7.widget.LinearSmoothScroller._
import android.support.v7.widget.RecyclerView.State
import android.support.v7.widget.{LinearLayoutManager, LinearSmoothScroller, RecyclerView}
import android.util.DisplayMetrics

trait ScrollingLinearLayoutManager {

  self: LinearLayoutManager =>

  var blockScroll = false

  val maxPositions = 50f

  val minSpeedFactor = 5f

  val maxSpeedFactor = 25f

  val varSpeedFactor = maxSpeedFactor - minSpeedFactor

  override def smoothScrollToPosition(recyclerView: RecyclerView, state: State, position: Int): Unit = {
    val steps = math.min(recyclerView.getLayoutManager match {
      case lm: LinearLayoutManager => math.abs(position - lm.findFirstVisibleItemPosition()).toFloat
      case _ => 1
    }, maxPositions)
    val speedFactor: Float = (varSpeedFactor - (steps * varSpeedFactor / maxPositions)) + minSpeedFactor
    val smoothScroller = new TopSmoothScroller(recyclerView, speedFactor)
    smoothScroller.setTargetPosition(position)
    startSmoothScroll(smoothScroller)
  }

  override def canScrollVertically: Boolean = if (blockScroll) false else getOrientation == VERTICAL

  class TopSmoothScroller(
    recyclerView: RecyclerView,
    speedFactor: Float) // If the previous call is scrolling, we don't want to onStop in startSmoothScroll
    extends LinearSmoothScroller(recyclerView.getContext) {

    def computeScrollVectorForPosition(targetPosition: Int): PointF = self.computeScrollVectorForPosition(targetPosition)

    protected override def getVerticalSnapPreference: Int = SNAP_TO_START

    protected override def calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
      speedFactor / displayMetrics.densityDpi.toFloat

  }

}
