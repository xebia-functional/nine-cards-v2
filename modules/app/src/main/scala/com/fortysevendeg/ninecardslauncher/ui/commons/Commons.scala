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

object ActivityResult {

  val Wizard = 1

}

object GoogleServicesConstants {

  val AccountType = "com.google"

  val AndroidId = "android_id"

  val ContentGServices = "content://com.google.android.gsf.gservices"

}

object ColorsUtils {

  def getColorDark(color: Int, ratio: Float = 0.1f) = {
    var colorHsv = Array(0f, 0f, 0f)
    Color.colorToHSV(color, colorHsv)
    colorHsv.update(2, math.max(colorHsv(2) - ratio, 0))
    Color.HSVToColor(colorHsv)
  }

  def setAlpha(color: Int, alpha: Float): Int = Color.argb((255 * alpha).toInt, Color.red(color), Color.green(color), Color.blue(color))

  def interpolateColors(fraction: Float, startValue: Int, endValue: Int): Int = {
    val startInt: Int = startValue
    val startA: Int = (startInt >> 24) & 0xff
    val startR: Int = (startInt >> 16) & 0xff
    val startG: Int = (startInt >> 8) & 0xff
    val startB: Int = startInt & 0xff
    val endInt: Int = endValue
    val endA: Int = (endInt >> 24) & 0xff
    val endR: Int = (endInt >> 16) & 0xff
    val endG: Int = (endInt >> 8) & 0xff
    val endB: Int = endInt & 0xff
    ((startA + (fraction * (endA - startA)).toInt) << 24) |
      ((startR + (fraction * (endR - startR)).toInt) << 16) |
      (startG + (fraction * (endG - startG)).toInt) << 8 |
      (startB + (fraction * (endB - startB)).toInt)
  }

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