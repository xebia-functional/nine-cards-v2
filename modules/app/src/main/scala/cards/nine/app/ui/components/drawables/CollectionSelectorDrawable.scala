package cards.nine.app.ui.components.drawables

import android.graphics.drawable.Drawable
import android.graphics._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid._
import cards.nine.commons.ops.ColorOps._

case class CollectionSelectorDrawable(
  color: Int = Color.WHITE)(implicit contextWrapper: ContextWrapper)
  extends Drawable {

  val padding = resGetDimensionPixelSize(R.dimen.padding_selector_collection_details)

  var selected = 0

  var numberOfItems = 0

  val paintSelected: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setColor(color)
    paint.setStrokeWidth(resGetDimensionPixelSize(R.dimen.stroke_large))
    paint.setStyle(Paint.Style.STROKE)
    paint
  }

  val paintDefault: Paint = {
    val paint = new Paint(paintSelected)
    paint.setColor(color.alpha(.2f))
    paint
  }

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val sizeSpace = bounds.width() / numberOfItems
    val sizeItem = sizeSpace - (padding * 2)

    (0 to numberOfItems) foreach { item =>
      val path = new Path()
      val startX = (sizeSpace * item) + padding
      val endX = startX + sizeItem
      path.moveTo(startX, 0)
      path.lineTo(endX, 0)
      canvas.drawPath(path, if (item == selected) paintSelected else paintDefault)
    }
  }

  override def setColorFilter(cf: ColorFilter): Unit = paintSelected.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = paintSelected.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  def setSelected(s: Int): Unit = {
    selected = s
    invalidateSelf()
  }

  def setNumberOfItems(items: Int): Unit = {
    numberOfItems = items
    invalidateSelf()
  }

}
