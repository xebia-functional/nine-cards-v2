package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class EdgeWorkspaceDrawable(left: Boolean)(implicit contextWrapper: ContextWrapper)
  extends Drawable {

  val color = resGetColor(R.color.collection_workspace_feedback_drop)

  lazy val fillPaint = {
    val paint = new Paint
    paint.setColor(color)
    paint.setAntiAlias(true)
    paint
  }

  lazy val strokePaint = {
    val paint = new Paint
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_default))
    paint.setColor(color.alpha(1f))
    paint.setAntiAlias(true)
    paint
  }

  override def setColorFilter(cf: ColorFilter): Unit = fillPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = fillPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawRect(bounds, fillPaint)
    // top line
    canvas.drawLine(0, 0, bounds.width(), 0, strokePaint)
    // bottom line
    canvas.drawLine(0, bounds.height(), bounds.width(), bounds.height(), strokePaint)
    // left or right line
    if (left) {
      canvas.drawLine(bounds.width(), 0, bounds.width(), bounds.height(), strokePaint)
    } else {
      canvas.drawLine(0, 0, 0, bounds.height(), strokePaint)
    }
  }
}
