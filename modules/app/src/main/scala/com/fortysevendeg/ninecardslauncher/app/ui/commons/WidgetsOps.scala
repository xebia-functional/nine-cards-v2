package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.appwidget.AppWidgetProviderInfo
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.process.device.models.Widget
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object WidgetsOps {

  val rows = 5

  val columns = 5

  def dimensionToCell(
    widthContent: Int,
    heightContent: Int,
    minWidth: Int,
    minHeight: Int,
    minResizeWidth: Int,
    minResizeHeight: Int)(implicit contextWrapper: ContextWrapper) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)

    val widthW = widthContent - (padding * 2)
    val heightW = heightContent - (padding * 2)

    val smallestCellWidth = (widthW / columns).toFloat
    val smallestCellHeight = (heightW / rows).toFloat

    val widthWidget = minWidth.toFloat + (padding * 2)
    val heightWidget = minHeight.toFloat + (padding * 2)

    val spanX = math.max(1, math.ceil(widthWidget / smallestCellWidth).toInt)
    val spanY = math.max(1, math.ceil(heightWidget / smallestCellHeight).toInt)

    val widthResizeWidget = minResizeWidth.toFloat + (padding * 2)
    val heightResizeWidget = minResizeHeight.toFloat + (padding * 2)

    val spanResizeX = math.max(1, math.ceil(widthResizeWidget / smallestCellWidth).toInt)
    val spanResizeY = math.max(1, math.ceil(heightResizeWidget / smallestCellHeight).toInt)

    Cell(spanX, spanY, spanResizeX, spanResizeY, smallestCellWidth.toInt, smallestCellHeight.toInt)
  }

  implicit class AppWidgetProviderInfoOp(info: AppWidgetProviderInfo) {

    def getCell(widthContent: Int, heightContent: Int)(implicit contextWrapper: ContextWrapper): Cell =
      dimensionToCell(
        widthContent = widthContent,
        heightContent = heightContent,
        minWidth = info.minWidth,
        minHeight = info.minHeight,
        minResizeWidth = info.minResizeWidth,
        minResizeHeight = info.minResizeHeight)

  }

  implicit class WidgetsOp(widget: Widget) {

    def getCell(widthContent: Int, heightContent: Int)(implicit contextWrapper: ContextWrapper): Cell =
      dimensionToCell(
        widthContent = widthContent,
        heightContent = heightContent,
        minWidth = widget.minWidth,
        minHeight = widget.minHeight,
        minResizeWidth = widget.minResizeWidth,
        minResizeHeight = widget.minResizeHeight)

  }

  case class Cell(
    spanX: Int,
    spanY: Int,
    spanResizeX: Int,
    spanResizeY: Int,
    widthCell: Int,
    heightCell: Int) {

    def getSize: (Int, Int) = (spanX * widthCell, spanY * heightCell)

    def getSize(sX: Int, sY: Int): (Int, Int) = (sX * widthCell, sY * heightCell)

    def getResizeSize: (Int, Int) = (spanResizeX * widthCell, spanResizeY * heightCell)

  }

}
