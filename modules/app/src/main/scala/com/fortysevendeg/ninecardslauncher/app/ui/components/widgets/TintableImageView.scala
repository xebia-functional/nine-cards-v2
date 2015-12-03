package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.graphics.PorterDuff.Mode
import android.graphics.{Color, PorterDuffColorFilter, Rect}
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent._
import android.widget.ImageView
import com.fortysevendeg.ninecardslauncher.commons._

class TintableImageView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends ImageView(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  var defaultColor = Color.WHITE

  var pressedColor = Color.WHITE

  var outSide = false

  def setTint(color: Int) = setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY))

  override def onTouchEvent(event: MotionEvent): Boolean = {
    val action = MotionEventCompat.getActionMasked(event)
    action match {
      case ACTION_DOWN => setTint(pressedColor); outSide = false
      case ACTION_MOVE => {
        val rect = new Rect(getLeft, getTop, getRight, getBottom)
        val aux = rect.contains(getLeft + event.getX.toInt, getTop + event.getY.toInt)
        if (aux != outSide) {
          outSide = aux
          if (outSide) setTint(pressedColor) else setTint(defaultColor)
        }
      }
      case ACTION_UP | ACTION_CANCEL => setTint(defaultColor)
      case _ =>
    }
    super.onTouchEvent(event)
  }

}
