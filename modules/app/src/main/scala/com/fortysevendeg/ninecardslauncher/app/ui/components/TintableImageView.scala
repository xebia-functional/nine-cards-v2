package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.graphics.PorterDuff.Mode
import android.graphics.{Rect, Color, PorterDuffColorFilter}
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import macroid.{Tweak, ActivityContextWrapper, ContextWrapper}

class TintableImageView(context: Context, attr: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
  extends ImageView(context, attr, defStyleAttr, defStyleRes) {

  def this(context: Context) = this(context, null, 0, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0, 0)

  def this(context: Context, attr: AttributeSet, defStyleAttr: Int) = this(context, attr, defStyleAttr, 0)

  var defaultColor = Color.WHITE

  var pressedColor = Color.WHITE

  var outSide = false

  def setTint(color: Int) = setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY))

  override def onTouchEvent(event: MotionEvent): Boolean = {
    val action = MotionEventCompat.getActionMasked(event)
    import MotionEvent._
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

object TintableImageViewTweaks {
  type W = TintableImageView

  def tivDefaultColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W] {
    view =>
      view.defaultColor = color
      view.setTint(color)
  }

  def tivPressedColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.pressedColor = color)

}