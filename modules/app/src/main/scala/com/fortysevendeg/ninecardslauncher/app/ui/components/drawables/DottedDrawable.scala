package com.fortysevendeg.ninecardslauncher.app.ui.components.drawables

import android.graphics._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class DottedDrawable(horizontal: Boolean = true)(implicit contextWrapper: ContextWrapper) extends Drawable {

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default).toFloat

  val paint: Paint = {
    val paint = new Paint
    paint.setColor(resGetColor(R.color.stroke_rules_moment))
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_thin))
    paint.setStyle(Paint.Style.STROKE)
    paint.setPathEffect(new DashPathEffect(Array(paddingDefault * 2, paddingDefault), 0))
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    if (horizontal) {
      canvas.drawLine(0, 0, bounds.width(), 0, paint)
    } else {
      canvas.drawLine(0, 0, 0, bounds.height(), paint)
    }
  }

  override def setColorFilter(cf: ColorFilter): Unit = paint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

}
