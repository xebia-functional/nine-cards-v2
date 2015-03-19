package com.fortysevendeg.ninecardslauncher.ui.components

import android.content.Context
import android.graphics.PorterDuff.Mode
import android.graphics.{Rect, Color, PorterDuffColorFilter}
import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import macroid.{Tweak, ActivityContext, AppContext}

class TintableImageView(context: Context)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends ImageView(context, null, 0) {

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

  def tivDefaultColor(resColor: Int)(implicit appContext: AppContext): Tweak[W] = Tweak[W] {
    view =>
      val defaultColor = resGetColor(resColor)
      view.defaultColor = defaultColor
      view.setTint(defaultColor)
  }

  def tivPressedColor(resColor: Int)(implicit appContext: AppContext): Tweak[W] = Tweak[W](_.pressedColor = resGetColor(resColor))

}