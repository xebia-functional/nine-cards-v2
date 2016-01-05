package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearLayoutManager._
import android.support.v7.widget.LinearSmoothScroller._
import android.support.v7.widget.RecyclerView.State
import android.support.v7.widget.{LinearLayoutManager, LinearSmoothScroller, RecyclerView}

trait ScrollingLinearLayoutManager {

  self: LinearLayoutManager =>

  var blockScroll = false

  override def smoothScrollToPosition(recyclerView: RecyclerView, state: State, position: Int): Unit = {
    val smoothScroller = new TopSmoothScroller(recyclerView.getContext)
    smoothScroller.setTargetPosition(position)
    startSmoothScroll(smoothScroller)
  }

  override def canScrollVertically: Boolean = if (blockScroll) false else getOrientation == VERTICAL

  private class TopSmoothScroller(context: Context)
    extends LinearSmoothScroller(context) {

    def computeScrollVectorForPosition(targetPosition: Int): PointF = self.computeScrollVectorForPosition(targetPosition)

    protected override def getVerticalSnapPreference: Int = SNAP_TO_START
  }

}
