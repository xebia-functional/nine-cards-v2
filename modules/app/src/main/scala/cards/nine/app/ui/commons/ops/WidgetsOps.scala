package cards.nine.app.ui.commons.ops

import android.appwidget.AppWidgetProviderInfo
import cards.nine.app.ui.commons.SystemBarsTint
import cards.nine.models.{AppWidget, Widget, WidgetArea}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ActivityContextWrapper, ContextWrapper}

object WidgetsOps {

  val rows = 5

  val columns = 5

  def sizeCell(
    widthContent: Int,
    heightContent: Int)(implicit contextWrapper: ContextWrapper): (Int, Int) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    val widthW = widthContent - (padding * 2)
    val heightW = heightContent - (padding * 2)
    (widthW / columns, heightW / rows)
  }

  def dimensionToCell(
    widthContent: Int,
    heightContent: Int,
    minWidth: Int,
    minHeight: Int)(implicit contextWrapper: ContextWrapper): Cell = {
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

  implicit class AppWidgetOp(widget: AppWidget) {

    def getCell(widthContent: Int, heightContent: Int)(implicit contextWrapper: ContextWrapper): Cell =
      dimensionToCell(
        widthContent = widthContent,
        heightContent = heightContent,
        minWidth = widget.minWidth,
        minHeight = widget.minHeight)

    def getSimulateCell(implicit contextWrapper: ActivityContextWrapper): Cell = {
      val systemBarsTint = new SystemBarsTint
      val metrics = contextWrapper.bestAvailable.getResources.getDisplayMetrics
      val widthContent = metrics.widthPixels
      val heightContent =
        metrics.heightPixels -
          systemBarsTint.getNavigationBarHeight -
          systemBarsTint.getStatusBarHeight -
          resGetDimensionPixelSize(R.dimen.size_icon_app_drawer) -
          resGetDimensionPixelSize(R.dimen.height_search_box)
      dimensionToCell(
        widthContent = widthContent,
        heightContent = heightContent,
        minWidth = widget.minWidth,
        minHeight = widget.minHeight)
    }

  }

  implicit class WidgetOp(widget: Widget) {

    def convert(newCellX: Int, newCellY: Int): Widget = {
      val spanX = widget.area.spanX
      val spanY = widget.area.spanY

      val startX = newCellX - (spanX / 2) match {
        case sx if sx < 0 => 0
        case sx if sx + spanX >= WidgetsOps.columns => WidgetsOps.columns - spanX
        case sx => sx
      }
      val startY = newCellY - (spanY / 2) match {
        case sy if sy < 0 => 0
        case sy if sy + spanY >= WidgetsOps.rows => WidgetsOps.rows - spanY
        case sy => sy
      }
      widget.copy(area = widget.area.copy(startX = startX, startY = startY))
    }

    def getMovement(to: WidgetArea): WidgetMovement = {
      val current = widget.area
      if (current.startX < to.startX) {
        LeftMovement
      } else if (current.startX > to.startX) {
        RightMovement
      } else if (current.startY < to.startY) {
        DownMovement
      } else {
        UpMovement
      }
    }

    def hasSpaceAfterMovement(
      movedArea: WidgetArea,
      otherAreas: Seq[WidgetArea],
      movement: WidgetMovement): Boolean = if (widget.area.intersect(movedArea)) {
      searchSpaceForMoveWidget(steps(movement), movedArea +: otherAreas).isDefined
    } else {
      true
    }

    def moveToBetterPlace(
      movedArea: WidgetArea,
      otherAreas: Seq[WidgetArea],
      movement: WidgetMovement): Option[Widget] = if (widget.area.intersect(movedArea)) {
      searchSpaceForMoveWidget(steps(movement), movedArea +: otherAreas) match {
        case Some(area) => Option(widget.copy(area = area))
        case _ => None
      }
    } else {
      None
    }

    lazy val limits = Option((WidgetsOps.rows, WidgetsOps.columns))

    @scala.annotation.tailrec
    final def searchSpaceForMoveWidget(
      movements: List[(Int, Int)],
      areas: Seq[WidgetArea]): Option[WidgetArea] =
      movements match {
        case Nil => None
        case head :: tail =>
          val (displaceX, displaceY) = head
          val newPosition = widget.area.copy(
            startX = widget.area.startX + displaceX,
            startY = widget.area.startY + displaceY)
          if (outOfTheLimit(newPosition)) {
            None
          } else {
            val widgetsIntersected = areas.filter(a => newPosition.intersect(a, limits))
            widgetsIntersected match {
              case Nil => Option(newPosition)
              case _ => searchSpaceForMoveWidget(tail, areas)
            }
          }
      }

    private[this] def outOfTheLimit(area: WidgetArea): Boolean =
      area.spanX <= 0 ||
        area.spanY <= 0 ||
        area.startX + area.spanX > WidgetsOps.columns ||
        area.startY + area.spanY > WidgetsOps.rows

    private[this] def steps(movement: WidgetMovement): List[(Int, Int)] = (movement match {
      case DownMovement => 1 to widget.area.startY map (p => (0, -p))
      case UpMovement => 1 until (WidgetsOps.rows - widget.area.startY) map (p => (0, p))
      case RightMovement => 1 until (WidgetsOps.columns - widget.area.startX) map (p => (p, 0))
      case LeftMovement => 1 to widget.area.startX map (p => (-p, 0))
    }).toList

  }

  case class Cell(spanX: Int, spanY: Int, widthCell: Int, heightCell: Int) {

    def getSize: (Int, Int) = (spanX * widthCell, spanY * heightCell)

    def getSize(sX: Int, sY: Int): (Int, Int) = (sX * widthCell, sY * heightCell)

  }

  sealed trait WidgetMovement

  case object UpMovement extends WidgetMovement
  case object DownMovement extends WidgetMovement
  case object LeftMovement extends WidgetMovement
  case object RightMovement extends WidgetMovement

}
