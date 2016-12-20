package cards.nine.app.ui.launcher.types

import android.graphics.Canvas
import android.view.View

class WidgetShadowBuilder(view: View) extends View.DragShadowBuilder(view) {

  override def onDrawShadow(canvas: Canvas): Unit = {
    Option(getView) foreach { view =>
      view.draw(canvas)
    }
  }
}
