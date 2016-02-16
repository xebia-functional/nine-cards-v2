package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters

import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.Tweak

trait AdapterStyles {

  val selectedScale: Float

  val normalScale: Float = 1f

  val normalAlpha: Float = 1f

  val unselectedAlpha: Float

  def contentStyle(selectItems: Boolean, active: Boolean) = (selectItems, active) match {
    case (true, true) => selected
    case (true, false) => unselected
    case _ => normal
  }

  private[this] def normal = vAlpha(normalAlpha) + vNormalScale

  private[this] def selected = vAlpha(normalAlpha) + vSelectedScale

  private[this] def unselected = vAlpha(unselectedAlpha) + vNormalScale

  private[this] def vSelectedScale = if (selectedScale == 1f) Tweak.blank else vScaleX(selectedScale) + vScaleY(selectedScale)

  private[this] def vNormalScale = if (selectedScale == 1f) Tweak.blank else vScaleX(normalScale) + vScaleY(normalScale)

}
