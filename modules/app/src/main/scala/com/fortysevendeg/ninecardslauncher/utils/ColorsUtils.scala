package com.fortysevendeg.ninecardslauncher.utils

import android.graphics.Color

object ColorsUtils {

  def getColorDark(color: Int, ratio: Float = 0.1f) = {
    var colorHsv = Array(0f, 0f, 0f)
    Color.colorToHSV(color, colorHsv)
    colorHsv.update(2, math.max(colorHsv(2) - ratio, 0))
    Color.HSVToColor(colorHsv)
  }

}
