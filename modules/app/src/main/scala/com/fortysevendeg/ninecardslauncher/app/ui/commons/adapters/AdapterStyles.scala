package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters

import android.view.View
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import macroid.Tweak

trait AdapterStyles {

  val normalScale: Float = 1f

  val normalAlpha: Float = 1f

  val unselectedAlpha: Float

  def contentStyle(selectItems: Boolean, active: Boolean) = (selectItems, active) match {
    case (true, true) => selected
    case (true, false) => unselected
    case _ => normal
  }

  private[this] def normal = vAlpha(normalAlpha) + vScaleX(normalScale) + vScaleY(normalScale)

  private[this] def selected = vAlpha(normalAlpha) + scale

  private[this] def unselected = vAlpha(unselectedAlpha) + vScaleX(normalScale) + vScaleY(normalScale)

  private[this] def scale = Tweak[View] { view =>
    val scale = view.calculateDefaultScale
    view.setScaleX(scale)
    view.setScaleY(scale)
  }

}
