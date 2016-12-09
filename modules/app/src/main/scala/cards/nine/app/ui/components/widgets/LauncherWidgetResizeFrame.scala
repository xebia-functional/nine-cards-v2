package cards.nine.app.ui.components.widgets

import android.view.Gravity
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

class LauncherWidgetResizeFrame(area: WidgetArea, widthCell: Int, heightCell: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable) {

  val strokeType = "stroke"

  val resizeHandleType = "resize-handle"

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  lazy val resizeHandleSize = paddingDefault * 2

  case class LauncherResizeFrameStatuses(area: WidgetArea = area)

  var frameStatuses = LauncherResizeFrameStatuses()

  val frame = {

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
        vSetType(strokeType) <~
        vBackground(R.drawable.stroke_widget_selected)
    ).get
    (content <~ addResizeHandle()).run
    content
  }

  ((this <~ vgAddView(frame)) ~ hideResizeHandle()).run

  def activeResize(): Ui[Any] = showResizeHandle()

  def updateView(area: WidgetArea): Ui[Any] = {
    frameStatuses = frameStatuses.copy(area = area)
    Ui(frame.setLayoutParams(createParams()))
  }

  private[this] def createParams(): LayoutParams = {
    val cell = Cell(frameStatuses.area.spanX, frameStatuses.area.spanY, widthCell, heightCell)
    val (width, height) = cell.getSize(frameStatuses.area.spanX, frameStatuses.area.spanY)
    val (startX, startY) = cell.getSize(frameStatuses.area.startX, frameStatuses.area.startY)
    val params = new LayoutParams(width + resizeHandleSize + stroke, height + resizeHandleSize + stroke)
    val left = startX
    val top = startY
    params.setMargins(left, top, 0, 0)
    params
  }

  private[this] def showResizeHandle() = this <~ Transformer {
    case i: ImageView if i.isType(resizeHandleType) => i <~ vVisible
  }

  private[this] def hideResizeHandle() = this <~ Transformer {
    case i: ImageView if i.isType(resizeHandleType) => i <~ vInvisible
  }

}
