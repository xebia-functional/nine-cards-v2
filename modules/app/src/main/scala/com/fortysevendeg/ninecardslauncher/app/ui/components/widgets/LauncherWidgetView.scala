package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.appwidget.AppWidgetHostView
import android.view.MotionEvent._
import android.view.View.OnTouchListener
import android.view.{GestureDetector, MotionEvent, View}
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses.EditWidgetsMode
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

case class LauncherWidgetView(id: Int, widgetView: AppWidgetHostView, presenter: LauncherPresenter)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable) {

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  val gestureDetector = new GestureDetector(getContext, new GestureDetector.SimpleOnGestureListener() {
    override def onLongPress(e: MotionEvent): Unit = presenter.openModeEditWidgets(id)
  })

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = gestureDetector.onTouchEvent(event)

  override def onTouchEvent(event: MotionEvent): Boolean = true

  val viewBlockTouch = w[FrameLayout].get
  viewBlockTouch.setOnTouchListener(new OnTouchListener {
    override def onTouch(v: View, event: MotionEvent): Boolean = {
      event.getAction match {
        case ACTION_DOWN =>
          presenter.statuses = presenter.statuses.copy(touchingWidget = true)
          if (presenter.statuses.mode == EditWidgetsMode) presenter.loadViewEditWidgets(id)
        case _ =>
      }
      false
    }
  })

  (this <~ vgAddViews(Seq(widgetView, viewBlockTouch))).run

  def activeSelected(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_selected)

  def activeResizing(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_resizing)

  def activeMoving(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_moving)

  def deactivateSelected(): Ui[Any] = this <~ vBlankBackground

  def adaptSize(widget: AppWidget): Ui[Any] = this.getField[Cell](LauncherWidgetView.cellKey) match {
    case Some(cell) => Ui {
      widgetView.updateAppWidgetSize(javaNull, 0, 0, cell.widthCell * cell.spanX, cell.heightCell * cell.spanY)
      setLayoutParams(createParams(cell, widget))
    }
    case _ => Ui.nop
  }

  def addView(cell: Cell, widget: AppWidget): Tweak[FrameLayout] = {
    widgetView.updateAppWidgetSize(javaNull, 0, 0, cell.widthCell * cell.spanX, cell.heightCell * cell.spanY)
    vgAddView(this, createParams(cell, widget))
  }

  private[this] def createParams(cell: Cell, widget: AppWidget): LayoutParams = {
    val (width, height) = cell.getSize(widget.area.spanX, widget.area.spanY)
    val (startX, startY) = cell.getSize(widget.area.startX, widget.area.startY)
    val params = new LayoutParams(width  + stroke, height + stroke)
    val left = paddingDefault + startX
    val top = paddingDefault + startY
    params.setMargins(left, top, paddingDefault, paddingDefault)
    params
  }

}

object LauncherWidgetView {
  val cellKey = "cell"
  val widgetKey = "widget"
}
