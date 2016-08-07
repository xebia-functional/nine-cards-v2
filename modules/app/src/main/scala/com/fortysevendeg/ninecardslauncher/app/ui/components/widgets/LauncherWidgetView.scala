package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.view.MotionEvent._
import android.view.View.OnTouchListener
import android.view.{GestureDetector, MotionEvent, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

case class LauncherWidgetView(id: Int, widgetView: View, presenter: LauncherPresenter)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable) {

  val gestureDetector = new GestureDetector(getContext, new GestureDetector.SimpleOnGestureListener() {
    override def onLongPress(e: MotionEvent): Unit = presenter.openModeEditWidgets(id)
  })

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    gestureDetector.onTouchEvent(event)
    false
  }

  override def onTouchEvent(event: MotionEvent): Boolean = true

  val viewBlockTouch = w[FrameLayout].get
  viewBlockTouch.setOnTouchListener(new OnTouchListener {
    override def onTouch(v: View, event: MotionEvent): Boolean = {
      event.getAction match {
        case ACTION_DOWN => presenter.statuses = presenter.statuses.copy(touchingWidget = true)
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

}

object LauncherWidgetView {
  val cellKey = "cell"
  val widgetKey = "widget"
}
