package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.appwidget.AppWidgetProviderInfo
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object AppWidgetProviderInfoOps {

  implicit class AppWidgetProviderInfoOp(info: AppWidgetProviderInfo) {

    val rows = 5

    val columns = 5

    def getCell(widthContent: Int, heightContent: Int)(implicit contextWrapper: ContextWrapper): Cell = {
      val padding = resGetDimensionPixelSize(R.dimen.padding_default)

      val widthW = widthContent - (padding * 2)
      val heightW = heightContent - (padding * 2)

      val smallestCellWidth = (widthW / columns).toFloat
      val smallestCellHeight = (heightW / rows).toFloat

      val widthWidget = info.minWidth.toFloat + (padding * 2)
      val heightWidget = info.minHeight.toFloat + (padding * 2)

      val spanX = math.max(1, math.ceil(widthWidget / smallestCellWidth).toInt)
      val spanY = math.max(1, math.ceil(heightWidget / smallestCellHeight).toInt)

      val widthResizeWidget = info.minResizeHeight.toFloat + (padding * 2)
      val heightResizeWidget = info.minResizeHeight.toFloat + (padding * 2)

      val spanResizeX = math.max(1, math.ceil(widthResizeWidget / smallestCellWidth).toInt)
      val spanResizeY = math.max(1, math.ceil(heightResizeWidget / smallestCellHeight).toInt)

      Cell(spanX, spanY, spanResizeX, spanResizeY, smallestCellWidth.toInt, smallestCellHeight.toInt)
    }

  }

  case class Cell(
    spanX: Int,
    spanY: Int,
    spanResizeX: Int,
    spanResizeY: Int,
    widthCell: Int,
    heightCell: Int) {

    def getSize: (Int, Int) = (spanX * widthCell, spanY * heightCell)

    def getResizeSize: (Int, Int) = (spanResizeX * widthCell, spanResizeY * heightCell)

  }

}
