package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.view.MotionEvent._
import android.view.View.OnTouchListener
import android.view.{MotionEvent, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.ClicksHandler
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import macroid.FullDsl._
import macroid._

class LauncherWidgetView(widgetView: View, presenter: LauncherPresenter)(implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable)
  with ClicksHandler {

  override def onLongClick(): Unit = {
    uiVibrate().run
    presenter.openModeEditWidgets()
  }

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = touchEvent(event)

  override def onTouchEvent(event: MotionEvent): Boolean = touchEvent(event)

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

  private[this] def touchEvent(event: MotionEvent): Boolean = {
    event.getAction match {
      case ACTION_DOWN =>
        startLongClick()
      case ACTION_CANCEL | ACTION_UP =>
        resetLongClick()
      case _ =>
    }
    false
  }

}
