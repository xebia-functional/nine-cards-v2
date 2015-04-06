package com.fortysevendeg.ninecardslauncher.ui.commons

import android.graphics.Color

object Constants {

  val NumSpaces = 9

  val NumInLine = 3

  val MinVelocity: Int = 250

  val MaxRatioVelocity: Int = 3000

  val MaxVelocity: Int = 700

  val SpaceVelocity: Int = MaxVelocity - MinVelocity

}

object ColorsUtils {

  def getColorDark(color: Int, ratio: Float = 0.1f) = {
    var colorHsv = Array(0f, 0f, 0f)
    Color.colorToHSV(color, colorHsv)
    colorHsv.update(2, math.max(colorHsv(2) - ratio, 0))
    Color.HSVToColor(colorHsv)
  }

  def setAlpha (color: Int, alpha: Byte): Int = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

}

object AnimationsUtils {

  def calculateDurationByVelocity(velocity: Float, defaultVelocity: Int): Int = {
    import Constants._
    velocity match {
      case 0 => defaultVelocity
      case _ => (SpaceVelocity - ((math.min(math.abs(velocity), MaxRatioVelocity) * SpaceVelocity) / MaxRatioVelocity) + MinVelocity).toInt
    }
  }

}