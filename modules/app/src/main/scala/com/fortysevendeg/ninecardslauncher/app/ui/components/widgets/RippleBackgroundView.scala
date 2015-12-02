package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.ninecardslauncher.commons._

class RippleBackgroundView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val rippleView: View = {
    val rippleView = new View(context)
    rippleView.setVisibility(View.INVISIBLE)
    addView(rippleView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    rippleView
  }

}
