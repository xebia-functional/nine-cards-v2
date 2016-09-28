package cards.nine.app.ui.components.drawables

import android.graphics._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class DottedDrawable(horizontal: Boolean = true)(implicit contextWrapper: ContextWrapper) extends Drawable {

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default).toFloat

  val paint: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setDither(true)
    paint.setColor(resGetColor(R.color.stroke_rules_moment))
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_thin))
    paint.setStyle(Paint.Style.STROKE)
    paint.setPathEffect(new DashPathEffect(Array(paddingDefault, paddingDefault), 0))
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val path = new Path()
    path.moveTo(0, 0)
    if (horizontal) path.lineTo(bounds.width(), 0) else path.lineTo(0, bounds.height())
    canvas.drawPath(path, paint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = paint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

}
