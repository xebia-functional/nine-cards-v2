package cards.nine.app.ui.components.widgets

import android.appwidget.AppWidgetHostView
import android.view.MotionEvent._
import android.view.View.{OnLongClickListener, OnTouchListener}
import android.view.{MotionEvent, View, ViewConfiguration}
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import cards.nine.app.ui.commons.CommonsTweak.vStartDrag
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.launcher.EditWidgetsMode
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.WidgetsJobs
import cards.nine.app.ui.launcher.types.{ReorderWidget, WidgetShadowBuilder}
import cards.nine.commons._
import cards.nine.models.Widget
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks.{vBackground, _}

class LauncherWidgetView(initialWidget: Widget, widgetView: AppWidgetHostView)(implicit contextWrapper: ContextWrapper, widgetJobs: WidgetsJobs)
  extends FrameLayout(contextWrapper.bestAvailable) {
  self =>

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  lazy val slop = ViewConfiguration.get(getContext).getScaledTouchSlop

  val longPressHelper = new CheckLongPressHelper(widgetView, new OnLongClickListener {
    override def onLongClick(v: View): Boolean = {
      widgetJobs.openModeEditWidgets(widgetStatuses.widget.id).resolveAsync()
      (self <~
        vStartDrag(ReorderWidget,
          new WidgetShadowBuilder(widgetView),
          Option(widgetStatuses.widget.id.toString), None)).run
      true
    }
  })

  case class LauncherWidgetViewStatuses(widget: Widget = initialWidget, lastX: Float = 0, lastY: Float = 0)

  var widgetStatuses = LauncherWidgetViewStatuses()

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    (event.getAction, longPressHelper.hasPerformedLongPress, isMoving(event.getX, event.getY)) match {
      case (ACTION_DOWN, _, _) =>
        longPressHelper.cancelLongPress()
        longPressHelper.postCheckForLongPress()
        widgetStatuses = widgetStatuses.copy(lastX = event.getX, lastY = event.getY)
        false
      case (_, true, _) =>
        longPressHelper.cancelLongPress()
        true
      case (ACTION_UP | ACTION_CANCEL, _, _) =>
        longPressHelper.cancelLongPress()
        false
      case (ACTION_MOVE, _, true) =>
        longPressHelper.cancelLongPress()
        false
      case _ => false
    }
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    (event.getAction, isMoving(event.getX, event.getY)) match {
      case (ACTION_UP | ACTION_CANCEL, _) =>
        longPressHelper.cancelLongPress()
      case (ACTION_DOWN, _) =>
        longPressHelper.cancelLongPress()
        widgetStatuses = widgetStatuses.copy(lastX = event.getX, lastY = event.getY)
      case (ACTION_MOVE, true) =>
        longPressHelper.cancelLongPress()
      case _ =>
    }
    true
  }

  val viewBlockTouch = w[FrameLayout].get
  viewBlockTouch.setOnTouchListener(new OnTouchListener {
    override def onTouch(v: View, event: MotionEvent): Boolean = {
      event.getAction match {
        case ACTION_DOWN =>
          statuses = statuses.copy(touchingWidget = true)
          if (statuses.mode == EditWidgetsMode) widgetJobs.closeModeEditWidgets().resolveAsync()
          false
        case _ => false
      }
    }
  })

  (this <~ vgAddViews(Seq(widgetView, viewBlockTouch))).run

  def activeSelected(): Ui[Any] = this <~ Transformer {
    case v: AppWidgetHostView => v <~ vInvisible
  }

  def deactivateSelected(): Ui[Any] = this <~ Transformer {
    case v: AppWidgetHostView => v <~ vVisible
  }

  def activeResize(): Ui[Any] = this <~ Transformer {
    case v: AppWidgetHostView => v <~ vVisible
  }

  @deprecated
  def activeResizing(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_resizing)

  @deprecated
  def activeMoving(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_moving)

  @deprecated
  def adaptSize(widget: Widget): Ui[Any] = this.getField[Cell](LauncherWidgetView.cellKey) match {
    case Some(cell) => Ui {
      updateWidgetSize(cell, widget)
      setLayoutParams(createParams(cell, widget))
    }
    case _ => Ui.nop
  }

  def addView(cell: Cell, widget: Widget): Tweak[FrameLayout] = {
    updateWidgetSize(cell, widget)
    vgAddView(this, createParams(cell, widget))
  }

  def updateView(widget: Widget): Ui[Any] = this.getField[Cell](LauncherWidgetView.cellKey) match {
    case Some(cell) => Ui {
      widgetStatuses = widgetStatuses.copy(widget = widget)
      updateWidgetSize(cell, widget)
      setLayoutParams(createParams(cell, widget))
    }
    case _ => Ui.nop
  }

  private[this] def createParams(cell: Cell, widget: Widget): LayoutParams = {
    val (width, height) = cell.getSize(widget.area.spanX, widget.area.spanY)
    val (startX, startY) = cell.getSize(widget.area.startX, widget.area.startY)
    val params = new LayoutParams(width + stroke, height + stroke)
    val left = paddingDefault + startX
    val top = paddingDefault + startY
    params.setMargins(left, top, paddingDefault, paddingDefault)
    params
  }

  private[this] def updateWidgetSize(cell: Cell, widget: Widget): Unit = {
    val density: Float = getResources.getDisplayMetrics.density
    val (width, height) = cell.getSize(widget.area.spanX, widget.area.spanY) match {
      case (w, h) => (((w - paddingDefault) / density).toInt, ((h - paddingDefault) / density).toInt)
    }
    widgetView.updateAppWidgetSize(javaNull, width, height, width, height)
    widgetView.requestLayout()
  }

  private[this] def isMoving(localX: Float, localY: Float): Boolean = {
    val moveX = math.abs(localX - widgetStatuses.lastX)
    val moveY = math.abs(localY - widgetStatuses.lastY)
    (moveX > slop) || (moveY > slop)
  }

  class CheckLongPressHelper(view: View, listener: View.OnLongClickListener) {

    case class CheckStatuses(
      hasPerformedLongPress: Boolean = false,
      pendingCheckForLongPress: Option[CheckForLongPress] = None)

    private[this] val longPressTimeout = 300

    private[this] var checkStatus = CheckStatuses()

    class CheckForLongPress extends Runnable {
      def run(): Unit = (Option(view.getParent), view.hasWindowFocus, checkStatus.hasPerformedLongPress) match {
        case (Some(_), true, false) =>
          if (listener.onLongClick(view)) {
            view.setPressed(false)
            checkStatus = checkStatus.copy(hasPerformedLongPress = true)
          }
        case _ =>
      }
    }

    def postCheckForLongPress(): Unit = {
      checkStatus = checkStatus.copy(hasPerformedLongPress = false)
      if (checkStatus.pendingCheckForLongPress.isEmpty) checkStatus = checkStatus.copy(pendingCheckForLongPress = Option(new CheckForLongPress()))
      checkStatus.pendingCheckForLongPress foreach (view.postDelayed(_, longPressTimeout))
    }

    def cancelLongPress(): Unit = {
      checkStatus = checkStatus.copy(hasPerformedLongPress = false)
      checkStatus.pendingCheckForLongPress foreach { longPress =>
        view.removeCallbacks(longPress)
        checkStatus = checkStatus.copy(pendingCheckForLongPress = None)
      }
    }

    def hasPerformedLongPress: Boolean = checkStatus.hasPerformedLongPress

  }

}

object LauncherWidgetView {
  // TODO We should remove that and store only width and height content in statuses
  val cellKey = "cell"
  val widgetKey = "widget"
}
