package com.fortysevendeg.ninecardslauncher.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import com.fortysevendeg.macroid.extras.SnailsUtils

class DrawerAnimationBackgroundDrawable()
  extends Drawable {

  var statuses = BackgroundDrawerAnimationStatuses()

  lazy val circlePaint = {
    val paint = new Paint
    paint.setColor(statuses.circleColor)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val radius = SnailsUtils.calculateRadius(statuses.x, bounds.centerY(), bounds.width(), bounds.height())
    canvas.drawColor(statuses.backgroundColor)
    canvas.drawCircle(
      statuses.x,
      bounds.centerY(),
      radius * statuses.percentage,
      circlePaint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = circlePaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = circlePaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  def setColors(backgroundColor: Int, circleColor: Int) = {
    statuses = statuses.copy(backgroundColor = backgroundColor, circleColor = circleColor)
  }

  def setData(p: Float, x: Int) = {
    statuses = statuses.copy(percentage = p, x = x)
    invalidateSelf()
  }

}

case class BackgroundDrawerAnimationStatuses(
  percentage: Float = 0,
  x: Int = 0,
  backgroundColor: Int = 0,
  circleColor: Int = 0)
