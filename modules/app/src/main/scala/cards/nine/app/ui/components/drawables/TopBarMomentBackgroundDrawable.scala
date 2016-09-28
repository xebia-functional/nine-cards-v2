package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.process.theme.models.{NineCardsTheme, SearchBackgroundColor}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

class TopBarMomentBackgroundDrawable(implicit theme: NineCardsTheme, contextWrapper: ContextWrapper)
  extends Drawable {

  val color = theme.get(SearchBackgroundColor)

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val radiusIcon = resGetDimensionPixelSize(R.dimen.radius_icon_top_bar_moment)

  val paint: Paint = {
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setColor(color)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    canvas.drawRoundRect(
      new RectF(
        bounds.left + radius,
        bounds.top + radius,
        bounds.right - radius,
        bounds.bottom - radius),
      radiusIcon,
      radiusIcon,
      paint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = paint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT
}
