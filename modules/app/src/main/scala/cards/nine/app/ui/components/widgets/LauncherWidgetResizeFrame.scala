package cards.nine.app.ui.components.widgets

import android.graphics.Rect
import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent.{ACTION_CANCEL, ACTION_DOWN, ACTION_MOVE, ACTION_UP}
import android.view.{Gravity, MotionEvent}
import android.widget.FrameLayout.LayoutParams
import android.widget.{FrameLayout, ImageView}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.ops.WidgetsOps._
import cards.nine.models.WidgetArea
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid.extras.FrameLayoutTweaks._
import macroid.extras.ImageViewTweaks.ivSrc
import macroid.extras.ResourcesExtras.resGetDimensionPixelSize
import macroid.extras.ViewGroupTweaks.vgAddView
import macroid.extras.ViewTweaks.{vBackground, _}
import macroid.{ContextWrapper, Ui, _}

class LauncherWidgetResizeFrame(
  widgetArea: WidgetArea,
  widthCell: Int,
  heightCell: Int,
  onResizeChangeArea: (WidgetArea) => Boolean,
  onResizeFinished: () => Unit)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable) {

  val resizeHandleType = "resize-handle"

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  lazy val resizeHandleSize = paddingDefault * 2

  case class LauncherResizeFrameStatuses(
    area: WidgetArea = widgetArea,
    draggingResizeType: Option[ResizeType] = None,
    draggingArea: WidgetArea = widgetArea,
    startDragX: Int = 0,
    startDragY: Int = 0) {

    def start(x: Int, y: Int): LauncherResizeFrameStatuses =
      copy(draggingResizeType = getResizeType(x, y), startDragX = x, startDragY = y, draggingArea = area)

    def calculateNewArea(x: Int, y: Int, resizeType: ResizeType): Option[WidgetArea] = {
      val horizontal = (x - startDragX) / widthCell
      val vertical = (y - startDragY) / heightCell
      val newArea = resizeType match {
        case TopResize => area.copy(startY = area.startY + vertical, spanY = area.spanY - vertical)
        case BottomResize => area.copy(spanY =  area.spanY + vertical)
        case LeftResize => area.copy(startX = area.startX + horizontal, spanX = area.spanX - horizontal)
        case RightResize => area.copy(spanX = area.spanX + horizontal)
      }
      if (newArea != draggingArea && newArea.isValid(columns, rows)) {
        Option(newArea)
      } else {
        None
      }
    }

  }

  var frameStatuses = LauncherResizeFrameStatuses()

  private[this] val frame = {

    def addResizeHandle() = {
      val paramsLeft = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_VERTICAL)
      val left = (w[ImageView] <~ vSetType(resizeHandleType) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      val paramsRight = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_VERTICAL | Gravity.RIGHT)
      val right = (w[ImageView] <~ vSetType(resizeHandleType) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      val paramsTop = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_HORIZONTAL)
      val top = (w[ImageView] <~ vSetType(resizeHandleType) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      val paramsBottom = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
      val bottom = (w[ImageView] <~ vSetType(resizeHandleType) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      vgAddView(left, paramsLeft) +
        vgAddView(right, paramsRight) +
        vgAddView(top, paramsTop) +
        vgAddView(bottom, paramsBottom)
    }
    val content = l[FrameLayout](
      w[ImageView] <~
        vMatchParent <~
        flLayoutMargin(marginLeft = paddingDefault, marginTop = paddingDefault, marginRight = paddingDefault, marginBottom = paddingDefault) <~
        vBackground(R.drawable.stroke_widget_selected)
    ).get
    (content <~ addResizeHandle()).run
    content
  }

  ((this <~ vgAddView(frame)) ~ hideResizeHandle()).run

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    val action = MotionEventCompat.getActionMasked(event)
    val x = MotionEventCompat.getX(event, 0).toInt
    val y = MotionEventCompat.getY(event, 0).toInt

    action match {
      case ACTION_DOWN =>
        frameStatuses = frameStatuses.start(x.toInt, y.toInt)
        false
      case ACTION_MOVE =>
        requestDisallowInterceptTouchEvent(true)
        updateFrame(x, y).run
        changeAreaIfNecessary(x, y)
        frameStatuses.draggingResizeType.isDefined
      case ACTION_UP | ACTION_CANCEL =>
        if (frameStatuses.draggingResizeType.isEmpty) {
          onResizeFinished()
        } else {
          updateView(frameStatuses.draggingArea).run
        }
        frameStatuses = frameStatuses.copy(draggingResizeType = None)
        false
      case _ => false
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    val action = MotionEventCompat.getActionMasked(event)
    val x = MotionEventCompat.getX(event, 0).toInt
    val y = MotionEventCompat.getY(event, 0).toInt

    action match {
      case ACTION_DOWN =>
        Option(getParent) foreach (_.requestDisallowInterceptTouchEvent(true))
        frameStatuses = frameStatuses.start(x.toInt, y.toInt)
      case ACTION_MOVE =>
        requestDisallowInterceptTouchEvent(true)
        updateFrame(x, y).run
        changeAreaIfNecessary(x, y)
      case ACTION_UP | ACTION_CANCEL =>
        if (frameStatuses.draggingResizeType.isEmpty) {
          onResizeFinished()
        } else {
          updateView(frameStatuses.draggingArea).run
        }
        frameStatuses = frameStatuses.copy(draggingResizeType = None)
      case _ =>
    }
    true
  }

  def getResizeType(x: Int, y: Int): Option[ResizeType] = {
    val rect = getRect
    if (x >= rect.left - paddingDefault && x <= rect.left + resizeHandleSize + paddingDefault) {
      Option(LeftResize)
    } else if (x >= rect.right - paddingDefault - resizeHandleSize && x <= rect.right + paddingDefault) {
      Option(RightResize)
    } else if (y >= rect.top - paddingDefault && y <= rect.top + resizeHandleSize + paddingDefault) {
      Option(TopResize)
    } else if (y >= rect.bottom - paddingDefault - resizeHandleSize && y <= rect.bottom + paddingDefault) {
      Option(BottomResize)
    } else {
      None
    }
  }

  def activeResize(): Ui[Any] = showResizeHandle()

  def updateView(area: WidgetArea): Ui[Any] = {
    frameStatuses = frameStatuses.copy(area = area)
    Ui(frame.setLayoutParams(createParams()))
  }

  private[this] def createParams(): LayoutParams = {
    val rect = getRect
    val params = new LayoutParams(rect.width(), rect.height())
    params.setMargins(rect.left, rect.top, 0, 0)
    params
  }

  private[this] def updateFrame(x: Int, y: Int): Ui[Any] = {
    val params = createParams()
    frameStatuses.draggingResizeType match {
      case Some(LeftResize) =>
        val left = x - frameStatuses.startDragX
        params.leftMargin = params.leftMargin + left
        params.width = params.width - left
      case Some(RightResize) =>
        val right = x - frameStatuses.startDragX
        params.width = params.width + right
      case Some(TopResize) =>
        val top = y - frameStatuses.startDragY
        params.topMargin = params.topMargin + top
        params.height = params.height - top
      case Some(BottomResize) =>
        val bottom = y - frameStatuses.startDragY
        params.height = params.height + bottom
      case _ =>
    }
    Ui(frame.setLayoutParams(params))
  }

  private[this] def changeAreaIfNecessary(x: Int, y: Int) = {
    for {
      resizeType <- frameStatuses.draggingResizeType
      area <- frameStatuses.calculateNewArea(x, y, resizeType)
    } yield {
      if (onResizeChangeArea(area)) {
        frameStatuses = frameStatuses.copy(draggingArea = area)
      }
    }
  }

  private[this] def getRect: Rect = {
    val cell = Cell(frameStatuses.area.spanX, frameStatuses.area.spanY, widthCell, heightCell)
    val (width, height) = cell.getSize(frameStatuses.area.spanX, frameStatuses.area.spanY)
    val (startX, startY) = cell.getSize(frameStatuses.area.startX, frameStatuses.area.startY)
    new Rect(startX, startY, startX + width + resizeHandleSize + stroke, startY + height + resizeHandleSize + stroke)
  }

  private[this] def showResizeHandle() = this <~ Transformer {
    case i: ImageView if i.isType(resizeHandleType) => i <~ vVisible
  }

  private[this] def hideResizeHandle() = this <~ Transformer {
    case i: ImageView if i.isType(resizeHandleType) => i <~ vInvisible
  }

}

sealed trait ResizeType

case object TopResize extends ResizeType
case object BottomResize extends ResizeType
case object LeftResize extends ResizeType
case object RightResize extends ResizeType
