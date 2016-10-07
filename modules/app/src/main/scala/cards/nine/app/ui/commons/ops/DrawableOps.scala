package cards.nine.app.ui.commons.ops

import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat

object DrawableOps {

  implicit class DrawableColors(drawable: Drawable) {

    def colorize(color: Int) = {
      val colorizeDrawable = DrawableCompat.wrap(drawable).mutate()
      DrawableCompat.setTint(DrawableCompat.wrap(colorizeDrawable).mutate(), color)
      colorizeDrawable
    }

  }

}
