package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat

object ColorOps {

  implicit class IntColors(color: Int) {

    def light(ratio: Float = 0.1f) = {
      val colorHsv = Array(0f, 0f, 0f)
      Color.colorToHSV(color, colorHsv)
      colorHsv.update(2, math.min(colorHsv(2) + ratio, 1))
      Color.HSVToColor(colorHsv)
    }

    def dark(ratio: Float = 0.1f) = {
      val colorHsv = Array(0f, 0f, 0f)
      Color.colorToHSV(color, colorHsv)
      colorHsv.update(2, math.max(colorHsv(2) - ratio, 0))
      Color.HSVToColor(colorHsv)
    }

    def alpha(alpha: Float): Int = Color.argb((255 * alpha).toInt, Color.red(color), Color.green(color), Color.blue(color))

    def colorToString(): String = s"#${0xFFFFFF & color}"

  }

  implicit class DrawableColors(drawable: Drawable) {

    def colorize(color: Int) = {
      val colorizeDrawable = DrawableCompat.wrap(drawable).mutate()
      DrawableCompat.setTint(DrawableCompat.wrap(colorizeDrawable).mutate(), color)
      colorizeDrawable
    }

  }

  implicit class InterpolateColors(colors: (Int, Int)) {

    def interpolateColors(fraction: Float): Int = {
      val startValue: Int = colors._1
      val endValue: Int = colors._2
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

}
