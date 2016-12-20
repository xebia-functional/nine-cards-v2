package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

import scala.annotation.tailrec

case class CharDrawable(char: String, circle: Boolean = false, background: Option[Int] = None)(
    implicit contextWrapper: ContextWrapper)
    extends Drawable {

  val ratioChars = .3f

  val colors = List(
    resGetColor(R.color.background_default_1),
    resGetColor(R.color.background_default_2),
    resGetColor(R.color.background_default_3),
    resGetColor(R.color.background_default_4),
    resGetColor(R.color.background_default_5))

  val backgroundColor = background getOrElse colors(positionByChar())

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
    paint.setTextAlign(Paint.Align.CENTER)
    paint
  }

  override def onBoundsChange(b: Rect): Unit = {
    parentBounds = Option(b)
    charPaint.setTextSize(determineMaxTextSize(b.width() * .5f))
    super.onBoundsChange(b)
  }

  override def draw(canvas: Canvas): Unit = {
    parentBounds foreach { pb =>
      if (circle) {
        canvas.drawCircle(pb.centerX(), pb.centerY(), pb.width() / 2, backgroundPaint)
      } else {
        canvas.drawColor(backgroundColor)
      }
      val bounds = new Rect
      charPaint.getTextBounds(char, 0, 1, bounds)
      val x: Int = pb.centerX()
      val y: Int = (pb.centerY() - bounds.exactCenterY).toInt
      canvas.drawText(char.toUpperCase, x, y, charPaint)
    }
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    charPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = charPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  private[this] def determineMaxTextSize(maxWidth: Float): Int = {
    val paint: Paint = new Paint
    @tailrec
    def calculateSize(size: Int): Int = {
      paint.setTextSize(size)
      if (paint.measureText("M") >= maxWidth) {
        size
      } else {
        calculateSize(size + 1)
      }
    }
    (calculateSize(0) / calculateRatioChars).toInt
  }

  private[this] def calculateRatioChars = 1 + ((char.length - 1) * ratioChars)

  private[this] def positionByChar(): Int = {
    val abc = "abcdefghijklmnñopqrstuvwxyz0123456789"
    abc.indexOf(char.toLowerCase) match {
      case i if i < 0 => 0
      case i          => i % colors.length
    }
  }

}
