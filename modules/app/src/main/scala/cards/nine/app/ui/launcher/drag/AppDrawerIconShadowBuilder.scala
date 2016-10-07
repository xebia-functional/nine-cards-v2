package cards.nine.app.ui.launcher.drag

import android.graphics.{Canvas, Point}
import android.view.View
import macroid.ContextWrapper
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R

class AppDrawerIconShadowBuilder(view: View)(implicit contextWrapper: ContextWrapper)
  extends View.DragShadowBuilder(view) {

  val size: Int = (resGetDimensionPixelSize(R.dimen.size_icon_app_medium) * 1.2f).toInt

  val scale: Float = size.toFloat / view.getWidth.toFloat

  override def onProvideShadowMetrics(shadowSize: Point, shadowTouchPoint: Point): Unit = {
    shadowSize.set(size, size)
    shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2)
  }

  override def onDrawShadow(canvas: Canvas): Unit = {
    Option(getView) foreach { view =>
      canvas.save()
      canvas.scale(scale, scale)
      view.draw(canvas)
      canvas.restore()
    }
  }
}
