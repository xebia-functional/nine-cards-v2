package cards.nine.app.ui.commons.ops

import android.appwidget.AppWidgetProviderInfo
import cards.nine.models.AppWidget
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

object WidgetsOps {

  val rows = 5

  val columns = 5

  def sizeCell(
    widthContent: Int,
    heightContent: Int)(implicit contextWrapper: ContextWrapper) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    val widthW = widthContent - (padding * 2)
    val heightW = heightContent - (padding * 2)
    (widthW / columns, heightW / rows)
  }

  def dimensionToCell(
    widthContent: Int,
    heightContent: Int,
    minWidth: Int,
    minHeight: Int)(implicit contextWrapper: ContextWrapper) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)

    val widthW = widthContent - (padding * 2)
    val heightW = heightContent - (padding * 2)

    val smallestCellWidth = (widthW / columns).toFloat
    val smallestCellHeight = (heightW / rows).toFloat

    val widthWidget = minWidth.toFloat + (padding * 2)
    val heightWidget = minHeight.toFloat + (padding * 2)

    val spanX = math.max(1, math.ceil(widthWidget / smallestCellWidth).toInt)
    val spanY = math.max(1, math.ceil(heightWidget / smallestCellHeight).toInt)

    Cell(spanX, spanY, smallestCellWidth.toInt, smallestCellHeight.toInt)
  }

  implicit class AppWidgetProviderInfoOp(info: AppWidgetProviderInfo) {

    def getCell(widthContent: Int, heightContent: Int)(implicit contextWrapper: ContextWrapper): Cell =
      dimensionToCell(
        widthContent = widthContent,
        heightContent = heightContent,
        minWidth = info.minWidth,
        minHeight = info.minHeight)

  }

  implicit class WidgetsOp(widget: AppWidget) {

    def getCell(widthContent: Int, heightContent: Int)(implicit contextWrapper: ContextWrapper): Cell =
      dimensionToCell(
        widthContent = widthContent,
        heightContent = heightContent,
        minWidth = widget.minWidth,
        minHeight = widget.minHeight)

  }

  case class Cell(
    spanX: Int,
    spanY: Int,
    widthCell: Int,
    heightCell: Int) {

    def getSize(): (Int, Int) = (spanX * widthCell, spanY * heightCell)

    def getSize(sX: Int, sY: Int): (Int, Int) = (sX * widthCell, sY * heightCell)

  }

}
