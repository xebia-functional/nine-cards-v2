package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable

class DrawerBackgroundDrawable(
    color: Int = 0,
    horizontalPadding: Int,
    verticalPadding: Int,
    radius: Int)
    extends Drawable {

  lazy val backgroundPaint = {
    val paint = new Paint
    paint.setColor(color)
    paint
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    backgroundPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = backgroundPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawRoundRect(
      new RectF(
        horizontalPadding,
        verticalPadding,
        bounds.width() - horizontalPadding,
        bounds.height() - verticalPadding),
      radius,
      radius,
      backgroundPaint)
  }

}
