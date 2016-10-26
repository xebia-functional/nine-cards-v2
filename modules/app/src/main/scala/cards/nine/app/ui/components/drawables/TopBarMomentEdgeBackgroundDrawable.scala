package cards.nine.app.ui.components.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.SearchBackgroundColor
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class TopBarMomentEdgeBackgroundDrawable(implicit theme: NineCardsTheme, contextWrapper: ContextWrapper)
  extends Drawable {

  val color = theme.get(SearchBackgroundColor).alpha(.2f)

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val height = resGetDimensionPixelSize(R.dimen.height_icon_content_top_bar_moment) - radius

  val width = resGetDimensionPixelSize(R.dimen.width_icon_content_top_bar_moment)

  val size = resGetDimensionPixelSize(R.dimen.padding_large) + (width / 2) - radius

  val paint: Paint = {
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setColor(color)
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val verticalPadding = (bounds.height() - height) / 2
    canvas.drawRect(
      bounds.left,
      verticalPadding + (radius / 2),
      size,
      bounds.height() - verticalPadding - (radius / 2),
      paint)
  }

  override def setColorFilter(cf: ColorFilter): Unit = paint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT
}
