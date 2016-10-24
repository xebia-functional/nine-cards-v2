package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.models.{CardBackgroundColor, NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class BackgroundSelectedDrawable(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme) extends Drawable {

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin).toFloat

  val paintStroke: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setDither(true)
    paint
  }

  val paintBack: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setDither(true)
    paint
  }

  selected(s = false)

  def selected(s: Boolean) = {
    if (s) {
      paintStroke.setColor(theme.get(PrimaryColor))
      paintBack.setColor(theme.get(PrimaryColor))
    } else {
      paintStroke.setColor(theme.get(PrimaryColor))
      paintBack.setColor(theme.get(CardBackgroundColor))
    }
    invalidateSelf()
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val middleX = bounds.width() / 2
    val middleY = bounds.height() / 2
    val radius = if (middleX < middleY) middleX else middleY
    canvas.drawCircle(middleX, middleY, radius, paintStroke)
    canvas.drawCircle(middleX, middleY, radius - stroke, paintBack)
  }

  override def setColorFilter(cf: ColorFilter): Unit = paintBack.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paintBack.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

}
