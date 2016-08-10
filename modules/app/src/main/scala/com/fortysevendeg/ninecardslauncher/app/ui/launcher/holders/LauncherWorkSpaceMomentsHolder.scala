package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.ViewGroup.LayoutParams._
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.DottedDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.LauncherWidgetView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.LauncherWidgetView._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.{MoveTransformation, ResizeTransformation}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceMomentsHolder(context: Context, presenter: LauncherPresenter, theme: NineCardsTheme, parentDimen: Dimen)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View]
  with TypedFindView {

  LayoutInflater.from(context).inflate(R.layout.moment_workspace_layout, this)

  val ruleTag = "rule"

  val content = findView(TR.launcher_moment_content)

  val widgets = findView(TR.launcher_moment_widgets)

  val message = findView(TR.launcher_moment_message)

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  val drawable = {
    val s = 0 until 8 map (_ => radius.toFloat)
    val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
    d.getPaint.setColor(resGetColor(R.color.moment_workspace_background))
    d
  }

  def populate(moment: LauncherMoment): Ui[Any] = {
    (for {
      collection <- moment.collection
    } yield {
      (message <~ vGone) ~
        (widgets <~ vVisible) ~
        (moment.momentType map (moment => Ui(presenter.loadWidgetsForMoment(moment))) getOrElse clearWidgets)
    }) getOrElse
      ((message <~ vVisible) ~
        (widgets <~ vGone))
  }

  def reloadSelectedWidget: Ui[Any] = this <~ Transformer {
    case widget: LauncherWidgetView if presenter.statuses.idWidget.contains(widget.id) => widget.activeSelected()
    case widget: LauncherWidgetView => widget.deactivateSelected()
  }

  def resizeCurrentWidget: Ui[Any] = this <~ Transformer {
    case widget: LauncherWidgetView if presenter.statuses.idWidget.contains(widget.id) => widget.activeResizing()
    case widget: LauncherWidgetView => widget.deactivateSelected()
  }

  def moveCurrentWidget: Ui[Any] = this <~ Transformer {
    case widget: LauncherWidgetView if presenter.statuses.idWidget.contains(widget.id) => widget.activeMoving()
    case widget: LauncherWidgetView => widget.deactivateSelected()
  }

  def arrowWidget(arrow: Arrow): Ui[Any] = (presenter.statuses.idWidget, presenter.statuses.transformation) match {
    case (Some(id), ResizeTransformation) => applyResize(id, arrow)
    case (Some(id), MoveTransformation) => applyMove(id, arrow)
    case _ => Ui.nop
  }

  def addWidget(widgetView: View, cell: Cell, widget: AppWidget): Ui[Any] = {
    val params = createParams(cell, widget)
    val launcherWidgetView = (LauncherWidgetView(widget.id, widgetView, presenter) <~ saveInfoInTag(cell, widget)).get
    widgets <~ vgAddView(launcherWidgetView, params)
  }

  def clearWidgets: Ui[Any] = widgets <~ vgRemoveAllViews

  def unhostWiget(id: Int): Ui[Any] = widgets <~ Transformer {
    case i: LauncherWidgetView if i.id == id => widgets <~ vgRemoveView(i)
  }

  def createRules: Ui[Any] = {
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

  def removeRules: Ui[Any] = this <~ Transformer {
    case i: ImageView if i.getTag == ruleTag => this <~ vgRemoveView(i)
  }

  private[this] def applyMove(id: Int, arrow: Arrow) = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id =>
      (for {
        cell <- i.getField[Cell](cellKey)
        widget <- i.getField[AppWidget](widgetKey)
      } yield {
        val newWidget = arrow match {
          case ArrowUp => widget.copy(area = widget.area.copy(startY = widget.area.startY - 1))
          case ArrowDown => widget.copy(area = widget.area.copy(startY = widget.area.startY + 1))
          case ArrowLeft => widget.copy(area = widget.area.copy(startX = widget.area.startX - 1))
          case ArrowRight => widget.copy(area = widget.area.copy(startX = widget.area.startX + 1))
        }
        (i <~ vAddField(widgetKey, newWidget)) ~
          Ui(i.setLayoutParams(createParams(cell, newWidget)))
      }) getOrElse Ui.nop
  }

  private[this] def applyResize(id: Int, arrow: Arrow) = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id =>
      (for {
        cell <- i.getField[Cell](cellKey)
        widget <- i.getField[AppWidget](widgetKey)
      } yield {
        val newWidget = arrow match {
          case ArrowUp => widget.copy(area = widget.area.copy(spanY = widget.area.spanY - 1))
          case ArrowDown => widget.copy(area = widget.area.copy(spanY = widget.area.spanY + 1))
          case ArrowLeft => widget.copy(area = widget.area.copy(spanX = widget.area.spanX - 1))
          case ArrowRight => widget.copy(area = widget.area.copy(spanX = widget.area.spanX + 1))
        }
        (i <~ vAddField(widgetKey, newWidget)) ~
          Ui(i.setLayoutParams(createParams(cell, newWidget)))
      }) getOrElse Ui.nop
  }

  private[this] def createParams(cell: Cell, widget: AppWidget) = {
    val (width, height) = cell.getSize(widget.area.spanX, widget.area.spanY)
    val (startX, startY) = cell.getSize(widget.area.startX, widget.area.startY)
    val params = new LayoutParams(width  + stroke, height + stroke)
    val left = paddingDefault + startX
    val top = paddingDefault + startY
    params.setMargins(left, top, paddingDefault, paddingDefault)
    params
  }

  private[this] def saveInfoInTag(cell: Cell, widget: AppWidget) =
    vAddField(cellKey, cell) +
      vAddField(widgetKey, widget)

}

sealed trait Arrow

case object ArrowUp extends Arrow
case object ArrowDown extends Arrow
case object ArrowLeft extends Arrow
case object ArrowRight extends Arrow