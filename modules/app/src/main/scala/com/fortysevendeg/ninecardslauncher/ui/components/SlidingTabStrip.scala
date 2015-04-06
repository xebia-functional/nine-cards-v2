package com.fortysevendeg.ninecardslauncher.ui.components

import android.content.Context
import android.graphics.{Color, Canvas, Paint}
import android.util.{TypedValue, AttributeSet}
import android.view.View
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher2.R
import macroid.AppContext
import com.fortysevendeg.macroid.extras.ResourcesExtras._

/**
 * Inspired in https://developer.android.com/samples/SlidingTabsBasic/index.html
 */
class SlidingTabStrip(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
  extends LinearLayout(context, attr, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  private var selectedPosition: Int = 0
  private var selectionOffset: Float = .0f

  setWillNotDraw(false)

  val selectedIndicatorThickness = resGetDimensionPixelSize(R.dimen.height_selected_tab)
  val selectedIndicatorPaint = new Paint
  selectedIndicatorPaint.setColor(Color.WHITE)

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
