package com.fortysevendeg.ninecardslauncher.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable

class CircleDrawable(backgroundColor: Int)
  extends Drawable {

  var percentage = 0f

  lazy val circlePaint = {
    val paint = new Paint
    paint.setColor(backgroundColor)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawCircle(bounds.centerX(), bounds.centerY(), (bounds.width() / 2) * percentage, circlePaint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = circlePaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = circlePaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  def setPercentage(p: Float) = {
    percentage = p
    invalidateSelf()
  }

}
