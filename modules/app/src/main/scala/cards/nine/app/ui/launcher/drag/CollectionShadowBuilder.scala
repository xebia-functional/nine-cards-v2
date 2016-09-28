package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drag

import android.graphics.Canvas
import android.view.View

class CollectionShadowBuilder(view: View)
  extends View.DragShadowBuilder(view) {

  override def onDrawShadow(canvas: Canvas): Unit = {
    Option(getView) foreach { view =>
      view.draw(canvas)
    }
  }
}
