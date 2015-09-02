package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.graphics._
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher2.R
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import macroid.ContextWrapper

case class CharDrawable(char: String, circle: Boolean = false)(implicit contextWrapper: ContextWrapper)
  extends Drawable {

  val colors = List(
    resGetColor(R.color.background_default_1),
    resGetColor(R.color.background_default_2),
    resGetColor(R.color.background_default_3),
    resGetColor(R.color.background_default_4),
    resGetColor(R.color.background_default_5)
  )

  val backgroundColor = colors(positionByChar())

  var parentBounds: Option[Rect] = None

  lazy val backgroundPaint = {
    val paint = new Paint
    paint.setColor(backgroundColor)
    paint
  }

  val charPaint: Paint = {
    val paint = new Paint
    paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
    paint.setAntiAlias(true)
    paint.setColor(Color.WHITE)
    paint
  }

  override def onBoundsChange(b: Rect): Unit = {
    parentBounds = Option(b)
    charPaint.setTextSize(determineMaxTextSize(b.width() * .5f))
    super.onBoundsChange(b)
  }

  override def draw(canvas: Canvas): Unit = {
    parentBounds foreach {
      pb =>
        if (circle) {
          canvas.drawCircle(pb.centerX(), pb.centerY(), pb.width() / 2, backgroundPaint)
        } else {
          canvas.drawColor(backgroundColor)
        }
        val bounds = new Rect
        charPaint.getTextBounds(char, 0, 1, bounds)
        val x: Int = (pb.centerX() - bounds.exactCenterX).toInt
        val y: Int = (pb.centerY() - bounds.exactCenterY).toInt
        canvas.drawText(char, x, y, charPaint)
    }
  }

  override def setColorFilter(cf: ColorFilter): Unit = charPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = charPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  private[this] def determineMaxTextSize(maxWidth: Float): Int = {
    var size: Int = 0
    val paint: Paint = new Paint
    do {
      size = size + 1
      paint.setTextSize(size)
    } while (paint.measureText("M") < maxWidth)
    size
  }

  private[this] def positionByChar(): Int = {
    val abc = "abcdefghijklmnñopqrstvwxyz0123456789"
    abc.indexOf(char.toLowerCase) match {
      case i if i < 0 => 0
      case i => i % colors.length
    }
  }

}
