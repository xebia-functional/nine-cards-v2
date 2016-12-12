package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import macroid.extras.SnailsUtils

class DrawerAnimationBackgroundDrawable(backgroundColor: Int, circleColor: Int)
  extends Drawable {

  private[this] var statuses = BackgroundDrawerAnimationStatuses()

  lazy val circlePaint = {
    val paint = new Paint
    paint.setColor(circleColor)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val radius = SnailsUtils.calculateRadius(statuses.x, bounds.centerY(), bounds.width(), bounds.height())
    canvas.drawColor(backgroundColor)
    canvas.drawCircle(
      statuses.x,
      bounds.centerY(),
      radius * statuses.percentage,
      circlePaint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = circlePaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = circlePaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  def setData(p: Float, x: Int) = {
    statuses = statuses.copy(percentage = p, x = x)
    invalidateSelf()
  }

}

case class BackgroundDrawerAnimationStatuses(percentage: Float = 0, x: Int = 0)
