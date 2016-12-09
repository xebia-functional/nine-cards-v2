package cards.nine.app.ui.components.widgets

import android.view.Gravity
import android.widget.FrameLayout.LayoutParams
import android.widget.{FrameLayout, ImageView}
import cards.nine.app.ui.commons.ops.WidgetsOps._
import cards.nine.models.WidgetArea
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid.extras.ImageViewTweaks.ivSrc
import macroid.extras.ResourcesExtras.resGetDimensionPixelSize
import macroid.extras.ViewGroupTweaks.vgAddView
import macroid.extras.ViewTweaks.{vBackground, vTag}
import macroid.{ContextWrapper, Ui, _}

class LauncherWidgetResizeFrame(area: WidgetArea, widthCell: Int, heightCell: Int)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable) {

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  lazy val resizeHandleSize = resGetDimensionPixelSize(R.dimen.size_widget_resize_handle)

  val tagResizeHandle = "resize-handle"

  case class LauncherResizeFrameStatuses(area: WidgetArea = area)

  var frameStatuses = LauncherResizeFrameStatuses()

  (this <~ vBackground(R.drawable.stroke_widget_selected)).run

  def addView(): Tweak[FrameLayout] = {
    vgAddView(this, createParams())
  }

  def activeResize(): Ui[Any] = {

    def addResizeHandle() = {
      val paramsLeft = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_VERTICAL)
      paramsLeft.leftMargin = -resizeHandleSize / 2
      val left = (w[ImageView] <~ vTag(tagResizeHandle) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      val paramsRight = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_VERTICAL | Gravity.RIGHT)
      paramsRight.rightMargin = -resizeHandleSize / 2
      val right = (w[ImageView] <~ vTag(tagResizeHandle) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      val paramsTop = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_HORIZONTAL)
      paramsTop.topMargin = -resizeHandleSize / 2
      val top = (w[ImageView] <~ vTag(tagResizeHandle) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      val paramsBottom = new LayoutParams(resizeHandleSize, resizeHandleSize, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
      paramsBottom.bottomMargin = -resizeHandleSize / 2
      val bottom = (w[ImageView] <~ vTag(tagResizeHandle) <~ ivSrc(R.drawable.mark_widget_resizing)).get
      vgAddView(left, paramsLeft) +
        vgAddView(right, paramsRight) +
        vgAddView(top, paramsTop) +
        vgAddView(bottom, paramsBottom)
    }

    this <~
      vBackground(R.drawable.stroke_widget_selected) <~
      addResizeHandle
  }

  def updateView(area: WidgetArea): Ui[Any] = {
    frameStatuses = frameStatuses.copy(area = area)
    Ui(setLayoutParams(createParams()))
  }

  private[this] def createParams(): LayoutParams = {
    val cell = Cell(frameStatuses.area.spanX, frameStatuses.area.spanY, widthCell, heightCell)
    val (width, height) = cell.getSize(frameStatuses.area.spanX, frameStatuses.area.spanY)
    val (startX, startY) = cell.getSize(frameStatuses.area.startX, frameStatuses.area.startY)
    val params = new LayoutParams(width + stroke, height + stroke)
    val left = paddingDefault + startX
    val top = paddingDefault + startY
    params.setMargins(left, top, paddingDefault, paddingDefault)
    params
  }

}
