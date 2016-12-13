package cards.nine.app.ui.launcher.holders

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.DragEvent._
import android.view.View
import android.view.ViewGroup.LayoutParams._
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.WidgetsOps._
import cards.nine.app.ui.commons.ops.WidgetsOps
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.components.drawables.DottedDrawable
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import cards.nine.app.ui.components.models.LauncherMoment
import cards.nine.app.ui.components.widgets.LauncherWidgetView._
import cards.nine.app.ui.components.widgets.{LauncherNoConfiguredWidgetView, LauncherWidgetResizeFrame, LauncherWidgetView}
import cards.nine.app.ui.commons.ops.ViewGroupOps._
import cards.nine.commons._
import cards.nine.models.{NineCardsTheme, Widget, WidgetArea}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import macroid.FullDsl._
import macroid._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.WidgetsJobs

class LauncherWorkSpaceMomentsHolder(context: Context, parentDimen: Dimen)(implicit widgetJobs: WidgetsJobs, theme: NineCardsTheme)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View]
  with TypedFindView {

  val ruleTag = "rule"

  case class WidgetStatuses(lastX: Option[Int] = None, lastY: Option[Int] = None) {
    def reset(): WidgetStatuses = copy(None, None)
  }

  var widgetStatuses = WidgetStatuses()

  lazy val onResizeChangeArea: (WidgetArea) => Boolean = (area) => {

    val (current, others) = partitionWidgets()

    current.headOption match {
      case Some(_) =>
        val hasSpaceAfterMovement: Boolean = (others map { w =>
          !w.widgetStatuses.widget.area.intersect(area)
        }).foldLeft(true) { (hasSpace, elem) =>
          if (!hasSpace) false else elem
        }
        resizeWidget(area).ifUi(hasSpaceAfterMovement).run
        hasSpaceAfterMovement
      case _ => false
    }
  }

  lazy val onResizeFinished: () => Unit = () => {
    widgetJobs.closeModeEditWidgets().resolveAsync()
  }

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  val (widthCell: Int, heightCell: Int) = {
    val widthW = parentDimen.width - (paddingDefault * 2)
    val heightW = parentDimen.height - (paddingDefault * 2)
    (widthW / columns, heightW / rows)
  }

  val drawable = {
    val s = 0 until 8 map (_ => radius.toFloat)
    val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
    d.getPaint.setColor(resGetColor(R.color.moment_workspace_background))
    d
  }

  def getWidgets: Seq[Widget] = this.children.collect {
    case lwv: LauncherWidgetView => lwv.widgetStatuses.widget
  }

  def populate(moment: LauncherMoment): Ui[Any] =
     moment.momentType map (moment => Ui {
       widgetJobs.loadWidgetsForMoment(moment).resolveAsyncServiceOr(_ =>
         widgetJobs.navigationUiActions.showContactUsError())
     }) getOrElse clearWidgets

  def dragReorderController(action: Int, x: Float, y: Float): Unit = {
    val (sx, sy) = calculatePosition(x, y)
    ((action, sx, sy, widgetStatuses.lastX, widgetStatuses.lastY) match {
      case (ACTION_DRAG_LOCATION, currentX, currentY, lastX, lastY)
        if lastX.isEmpty || lastY.isEmpty || !lastX.contains(currentX) || !lastY.contains(currentY) =>
        widgetStatuses = widgetStatuses.copy(lastX = Option(currentX), lastY = Option(currentY))
        relocatedWidget(currentX, currentY)
      case (ACTION_DROP | ACTION_DRAG_ENDED, _, _, _, _) =>
        widgetStatuses = widgetStatuses.reset()
        activeResizeWidget()
      case _ => Ui.nop
    }).run
  }

  def relocatedWidget(newCellX: Int, newCellY: Int): Ui[Any] = {
    val (current, others) = partitionWidgets()

    current.headOption match {
      case Some(currentWidget) =>
        val newWidget = currentWidget.widgetStatuses.widget.convert(newCellX, newCellY)
        val newMovement = currentWidget.widgetStatuses.widget.getMovement(newWidget.area)

        val hasSpaceAfterMovement: Boolean = (others map { w =>
          w.widgetStatuses.widget.hasSpaceAfterMovement(
            newWidget.area,
            others.filterNot(_ == w) map (_.widgetStatuses.widget.area),
            newMovement)
        }).foldLeft(true) { (hasSpace, elem) =>
          if (!hasSpace) false else elem
        }

        if (hasSpaceAfterMovement) {
          val updateOtherWidgets = others flatMap { w =>
            val betterPlace = w.widgetStatuses.widget.moveToBetterPlace(
              newWidget.area,
              others.filterNot(_ == w) map (_.widgetStatuses.widget.area),
              newMovement)
            betterPlace map w.updateView
          }
          Ui.sequence(updateOtherWidgets: _*) ~
            currentWidget.updateView(newWidget) ~
            reloadResizeFrame(newWidget.area)
        } else {
          Ui.nop
        }
      case _ => Ui.nop
    }
  }

  def activeResizeWidget(): Ui[Any] = this <~ Transformer {
    case widgetView: LauncherWidgetView if statuses.idWidget.contains(widgetView.widgetStatuses.widget.id) =>
      widgetView.activeResize()
    case frame: LauncherWidgetResizeFrame => frame.activeResize()
  }

  def resizeWidget(area: WidgetArea): Ui[Any] = this <~ Transformer {
    case widgetView: LauncherWidgetView if statuses.idWidget.contains(widgetView.widgetStatuses.widget.id) =>
      widgetView.updateView(widgetView.widgetStatuses.widget.copy(area = area))
  }

  def startEditWidget(): Ui[Any] = (this <~ Transformer {
    case widgetView: LauncherWidgetView if statuses.idWidget.contains(widgetView.widgetStatuses.widget.id) =>
      val frame = new LauncherWidgetResizeFrame(widgetView.widgetStatuses.widget.area, widthCell, heightCell, onResizeChangeArea, onResizeFinished)
      widgetView.activeSelected() ~ (this <~ vgAddView(frame)) ~ frame.updateView(widgetView.widgetStatuses.widget.area)
    case widgetView: LauncherWidgetView => widgetView.deactivateSelected()
  }) ~ createRules

  def closeEditWidget(): Ui[Any] = (this <~ Transformer {
    case widgetView: LauncherWidgetView if statuses.idWidget.contains(widgetView.widgetStatuses.widget.id) =>
      widgetView.activeSelected()
    case widgetView: LauncherWidgetView => widgetView.deactivateSelected()
  }) ~ removeRulesAnResizeFrame()

  def reloadSelectedWidget: Ui[Any] = this <~ Transformer {
    case widgetView: LauncherWidgetView if statuses.idWidget.contains(widgetView.widgetStatuses.widget.id) => widgetView.activeSelected()
    case widgetView: LauncherWidgetView => widgetView.deactivateSelected()
  }

  def reloadResizeFrame(area: WidgetArea): Ui[Any] = this <~ Transformer {
    case frame: LauncherWidgetResizeFrame => frame.updateView(area)
  }

  def addWidget(widgetView: AppWidgetHostView, cell: Cell, widget: Widget): Ui[Any] = {
    val launcherWidgetView = (new LauncherWidgetView(widget, widgetView) <~ saveInfoInTag(cell, widget)).get
    this <~ launcherWidgetView.addView(cell, widget)
  }

  def addNoConfiguredWidget(wCell: Int, hCell: Int, widget: Widget): Ui[Any] = {
    val noConfiguredWidgetView = LauncherNoConfiguredWidgetView(widget.id, wCell, hCell, widget)
    this <~ noConfiguredWidgetView.addView()
  }

  def addReplaceWidget(widgetView: AppWidgetHostView, wCell: Int, hCell: Int, widget: Widget): Ui[Any] = {
    val cell = Cell(widget.area.spanX, widget.area.spanY, wCell, hCell)
    (this <~ Transformer {
      case i: LauncherNoConfiguredWidgetView if i.id == widget.id => this <~ vgRemoveView(i)
    }) ~ addWidget(widgetView, cell, widget)
  }

  def clearWidgets: Ui[Any] = this <~ vgRemoveAllViews

  def unhostWiget(id: Int): Ui[Any] = this <~ Transformer {
    case widgetView: LauncherWidgetView if widgetView.widgetStatuses.widget.id == id => this <~ vgRemoveView(widgetView)
  }

  private[this] def partitionWidgets(): (Seq[LauncherWidgetView], Seq[LauncherWidgetView]) = {
    this.children.collect{
      case lwv: LauncherWidgetView => lwv
    }.partition(lwv => statuses.idWidget.contains(lwv.widgetStatuses.widget.id))
  }

  private[this] def createRules: Ui[Any] = {
    val spaceWidth = (getWidth - (paddingDefault * 2)) / WidgetsOps.columns
    val spaceHeight = (getHeight - (paddingDefault * 2)) / WidgetsOps.rows

    def createView(horizontal: Boolean = true) =
      (w[ImageView] <~
        vWrapContent <~
        vTag(ruleTag) <~
        vBackground(new DottedDrawable(horizontal))).get

    def horizontalRules(pos: Int) = {
      val params = new LayoutParams(MATCH_PARENT, stroke)
      params.leftMargin = paddingDefault
      params.rightMargin = paddingDefault
      params.topMargin = (pos * spaceHeight) + paddingDefault
      vgAddViewByIndexParams(createView(), 0, params)
    }

    def verticalRules(pos: Int) = {
      val params = new LayoutParams(stroke, MATCH_PARENT)
      params.topMargin = paddingDefault
      params.bottomMargin = paddingDefault
      params.leftMargin = (pos * spaceWidth) + paddingDefault
      vgAddViewByIndexParams(createView(horizontal = false), 0, params)
    }

    val tweaks = ((1 until WidgetsOps.rows) map horizontalRules) ++ ((1 until WidgetsOps.columns) map verticalRules)
    val uis = tweaks map (tweak => this <~ tweak)
    Ui.sequence(uis: _*)
  }

  private[this] def removeRulesAnResizeFrame(): Ui[Any] = this <~ Transformer {
    case i: ImageView if i.getTag == ruleTag => this <~ vgRemoveView(i)
    case i: LauncherWidgetResizeFrame => this <~ vgRemoveView(i)
  }

  private[this] def saveInfoInTag(cell: Cell, widget: Widget) =
    vAddField(cellKey, cell) +
      vAddField(widgetKey, widget)

  private[this] def calculatePosition(x: Float, y: Float) = {
    val w = getWidth
    val h = getHeight
    val spaceX = w / 5
    val spaceY = h / 5
    val sx = (x / spaceX).toInt
    val sy = (y / spaceY).toInt
    (sx, sy)
  }

}