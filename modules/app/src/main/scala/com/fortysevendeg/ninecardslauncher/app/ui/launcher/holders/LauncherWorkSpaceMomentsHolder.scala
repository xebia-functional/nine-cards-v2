package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.MotionEvent._
import android.view.View.OnTouchListener
import android.view.{LayoutInflater, MotionEvent, View}
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppWidgetProviderInfoOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.LauncherMoment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
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

  def addWidget(widgetView: View, cell: Cell): Ui[Any] = {
    val viewBlockTouch = w[FrameLayout].get
    viewBlockTouch.setOnTouchListener(new OnTouchListener {
      override def onTouch(v: View, event: MotionEvent): Boolean = {
        event.getAction match {
          case ACTION_DOWN => presenter.statuses = presenter.statuses.copy(touchingWidget = true)
        }
        false
      }
    })
    val (width, height) = cell.getSize
    val params = new LayoutParams(width, height)
    widgets <~ vgRemoveAllViews <~ vgAddViews(Seq(widgetView, viewBlockTouch), params)
  }

  def clearWidgets(): Ui[Any] = widgets <~ vgRemoveAllViews

}