package com.fortysevendeg.ninecardslauncher.ui.collections

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class CollectionItemDecorator (implicit contextWrapper: ContextWrapper)
  extends RecyclerView.ItemDecoration {

  override def getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State): Unit = {
    outRect.top = resGetDimensionPixelSize(R.dimen.padding_small)
    outRect.bottom = resGetDimensionPixelSize(R.dimen.padding_small)
    outRect.left = resGetDimensionPixelSize(R.dimen.padding_small)
    outRect.right = resGetDimensionPixelSize(R.dimen.padding_small)
  }

}