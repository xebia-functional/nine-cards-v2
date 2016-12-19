package cards.nine.app.ui.components.drawables.tweaks

import android.widget.ImageView
import cards.nine.app.ui.components.drawables.PathMorphDrawable
import macroid.Tweak

import scala.util.Try

object PathMorphDrawableTweaks {
  type W = ImageView

  def pmdAnimIcon(icon: Int) = Tweak[W] { view =>
    view.getDrawable.asInstanceOf[PathMorphDrawable].setToTypeIcon(icon)
    view.getDrawable.asInstanceOf[PathMorphDrawable].start()
  }

  def pmdChangeIcon(icon: Int) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setTypeIcon(icon)))

  def pmdColor(color: Int) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setColor(color)))

  def pmdColorResource(color: Int) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setColorResource(color)))

  def pmdStroke(stroke: Float) =
    Tweak[W](view => Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setStroke(stroke)))
}
