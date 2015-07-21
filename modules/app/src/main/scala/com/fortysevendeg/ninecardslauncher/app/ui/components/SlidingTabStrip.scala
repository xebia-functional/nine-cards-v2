package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.graphics.{Canvas, Color, Paint}
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

/**
 * Inspired in https://developer.android.com/samples/SlidingTabsBasic/index.html
 */
class SlidingTabStrip(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends LinearLayout(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  private var selectedPosition: Int = 0
  private var selectionOffset: Float = .0f

  setWillNotDraw(false)

  val selectedIndicatorThickness = resGetDimensionPixelSize(R.dimen.height_selected_tab)
  val selectedIndicatorPaint = new Paint
  setColor(Color.WHITE)

  def setColor(color: Int) = selectedIndicatorPaint.setColor(color)

  def onViewPagerPageChanged(position: Int, positionOffset: Float) {
    selectedPosition = position
    selectionOffset = positionOffset
    invalidate()
  }

  protected override def onDraw(canvas: Canvas) {
    val height: Int = getHeight
    val childCount: Int = getChildCount

    if (childCount > 0) {
      val selectedTitle: View = getChildAt(selectedPosition)
      var left: Int = selectedTitle.getLeft
      var right: Int = selectedTitle.getRight

      if (selectionOffset > 0f && selectedPosition < (getChildCount - 1)) {
        val nextTitle = getChildAt(selectedPosition + 1)
        left = (selectionOffset * nextTitle.getLeft + (1.0f - selectionOffset) * left).toInt
        right = (selectionOffset * nextTitle.getRight + (1.0f - selectionOffset) * right).toInt
      }
      canvas.drawRect(left, height - selectedIndicatorThickness, right, height, selectedIndicatorPaint)
    }

  }


}
