package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.ViewGroup.LayoutParams._
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.DottedDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.LauncherWidgetView
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.{MoveTransformation, ResizeTransformation}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceMomentsHolder(context: Context, presenter: LauncherPresenter, theme: NineCardsTheme, parentDimen: Dimen)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View]
  with TypedFindView {

  LayoutInflater.from(context).inflate(R.layout.moment_workspace_layout, this)

  val ruleTag = "rule"

  val content = Option(findView(TR.launcher_moment_content))

  val widgets = Option(findView(TR.launcher_moment_widgets))

  val message = Option(findView(TR.launcher_moment_message))

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

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
        (moment.momentType map (moment => Ui(presenter.loadWidgetsForMoment(moment))) getOrElse clearWidgets())
    }) getOrElse
      ((message <~ vVisible) ~
        (widgets <~ vGone))
  }

  def reloadSelectedWidget(): Ui[Any] = this <~ Transformer {
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
  }

  def addWidget(widgetView: View, cell: Cell): Ui[Any] = {
    val (width, height) = cell.getSize
    val params = new LayoutParams(width, height)
    params.setMargins(paddingDefault, paddingDefault, paddingDefault, paddingDefault)
    widgets <~
      vgRemoveAllViews <~
      vgAddView(LauncherWidgetView(1, widgetView, presenter), params) // Id is 1 for now, we only have one widget
  }

  def clearWidgets(): Ui[Any] = widgets <~ vgRemoveAllViews

  def createRules(): Ui[Any] = {
    val size = resGetDimensionPixelSize(R.dimen.stroke_thin)
    val spaceWidth = (getWidth - (paddingDefault * 2)) / WidgetsOps.columns
    val spaceHeight = (getHeight - (paddingDefault * 2)) / WidgetsOps.rows

    def createView(horizontal: Boolean = true) =
      (w[ImageView] <~
        vWrapContent <~
        vTag(ruleTag) <~
        vBackground(new DottedDrawable(horizontal))).get

    def horizontalRules(pos: Int) = {
      val params = new LayoutParams(MATCH_PARENT, size)
      params.leftMargin = paddingDefault
      params.rightMargin = paddingDefault
      params.topMargin = (pos * spaceHeight) + paddingDefault - size
      vgAddViewByIndexParams(createView(), 0, params)
    }

    def verticalRules(pos: Int) = {
      val params = new LayoutParams(size, MATCH_PARENT)
      params.topMargin = paddingDefault
      params.bottomMargin = paddingDefault
      params.leftMargin = (pos * spaceWidth) + paddingDefault - size
      vgAddViewByIndexParams(createView(horizontal = false), 0, params)
    }

    val tweaks = ((1 until WidgetsOps.rows) map horizontalRules) ++ ((1 until WidgetsOps.columns) map verticalRules)
    val uis = tweaks map (tweak => this <~ tweak)
    Ui.sequence(uis: _*)
  }

  def removeRules(): Ui[Any] = this <~ Transformer {
    case i: ImageView if i.getTag == ruleTag => this <~ vgRemoveView(i)
  }

  private[this] def applyMove(id: Int, arrow: Arrow) = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id =>
      arrow match {
        case ArrowUp => uiShortToast("Move Up")
        case ArrowDown => uiShortToast("Move Down")
        case ArrowLeft => uiShortToast("Move Left")
        case ArrowRight => uiShortToast("Move Right")
      }
  }

  private[this] def applyResize(id: Int, arrow: Arrow) = this <~ Transformer {
    case i: LauncherWidgetView if i.id == id =>
      arrow match {
        case ArrowUp => uiShortToast("Resize Up")
        case ArrowDown => uiShortToast("Resize Down")
        case ArrowLeft => uiShortToast("Resize Left")
        case ArrowRight => uiShortToast("Resize Right")
      }
  }

}

sealed trait Arrow

case object ArrowUp extends Arrow
case object ArrowDown extends Arrow
case object ArrowLeft extends Arrow
case object ArrowRight extends Arrow