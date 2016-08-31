package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.View
import android.view.ViewGroup.LayoutParams._
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.WidgetsOps
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.WidgetsOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.DottedDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.LauncherWidgetView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.LauncherWidgetView._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
import com.fortysevendeg.ninecardslauncher.process.widget.{MoveWidgetRequest, ResizeWidgetRequest}
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceMomentsHolder(context: Context, presenter: LauncherPresenter, theme: NineCardsTheme, parentDimen: Dimen)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View]
  with TypedFindView {

  val ruleTag = "rule"

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  val drawable = {
    val s = 0 until 8 map (_ => radius.toFloat)
    val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
    d.getPaint.setColor(resGetColor(R.color.moment_workspace_background))
    d
  }

  def populate(moment: LauncherMoment): Ui[Any] =
     moment.momentType map (moment => Ui(presenter.loadWidgetsForMoment(moment))) getOrElse clearWidgets

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

  def resizeWidgetById(id: Int, resize: ResizeWidgetRequest): Ui[Any] = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id =>
      (for {
        cell <- i.getField[Cell](cellKey)
        widget <- i.getField[AppWidget](widgetKey)
      } yield {
        val newWidget = widget.copy(area = widget.area.copy(
          spanX = widget.area.spanX + resize.increaseX,
          spanY = widget.area.spanY + resize.increaseY))
        (i <~ vAddField(widgetKey, newWidget)) ~
          i.adaptSize(newWidget)
      }) getOrElse Ui.nop
  }

  def moveWidgetById(id: Int, move: MoveWidgetRequest): Ui[Any] = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id =>
      (for {
        cell <- i.getField[Cell](cellKey)
        widget <- i.getField[AppWidget](widgetKey)
      } yield {
        val newWidget = widget.copy(area = widget.area.copy(
          startX = widget.area.startX + move.displaceX,
          startY = widget.area.startY + move.displaceY))
        (i <~ vAddField(widgetKey, newWidget)) ~
          i.adaptSize(newWidget)
      }) getOrElse Ui.nop
  }

  def addWidget(widgetView: AppWidgetHostView, cell: Cell, widget: AppWidget): Ui[Any] = {
    val launcherWidgetView = (LauncherWidgetView(widget.id, widgetView, presenter) <~ saveInfoInTag(cell, widget)).get
    this <~ launcherWidgetView.addView(cell, widget)
  }

  def clearWidgets: Ui[Any] = this <~ vgRemoveAllViews

  def unhostWiget(id: Int): Ui[Any] = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id => this <~ vgRemoveView(i)
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

  def removeRules(): Ui[Any] = this <~ Transformer {
    case i: ImageView if i.getTag == ruleTag => this <~ vgRemoveView(i)
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