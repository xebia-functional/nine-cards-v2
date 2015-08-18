package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import macroid.{Tweak, ContextWrapper}

class NineRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var disableScroll = false

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = if(disableScroll) {
    true
  } else {
    super.dispatchTouchEvent(ev)
  }
}

object NineRecyclerViewTweaks {
  type W = NineRecyclerView

  def nrvDisableScroll(disable: Boolean) = Tweak[W](_.disableScroll = disable)

}