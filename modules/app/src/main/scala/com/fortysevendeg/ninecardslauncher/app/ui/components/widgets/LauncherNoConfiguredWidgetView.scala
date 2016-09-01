package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.{FrameLayout, ImageView}
import android.widget.FrameLayout.LayoutParams
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{GenericUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
import com.fortysevendeg.ninecardslauncher2.R
import macroid._
import macroid.FullDsl._

case class LauncherNoConfiguredWidgetView(id: Int, wCell: Int, hCell: Int, widget: AppWidget, presenter: LauncherPresenter)
  (implicit contextWrapper: ContextWrapper)
  extends FrameLayout(contextWrapper.bestAvailable) {

  implicit lazy val uiContext: UiContext[Context] = GenericUiContext(getContext)

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  val icon = (
    w[ImageView] <~
      vWrapContent <~
      ivSrcByPackageName(Some(widget.packageName), "W") <~
      flLayoutGravity(Gravity.CENTER)).get

  (this <~
    vBackgroundColor(Color.GRAY) <~
    vgAddView(icon) <~
    On.click(Ui(presenter.hostNoConfiguredWidget(widget)))).run

  def addView(): Tweak[FrameLayout] = {
    vgAddView(this, createParams())
  }

  private[this] def createParams(): LayoutParams = {
    val (width, height) = (widget.area.spanX * wCell, widget.area.spanY * hCell)
    val (startX, startY) = (widget.area.startX * wCell, widget.area.startY * hCell)
    val params = new LayoutParams(width  + stroke, height + stroke)
    val left = paddingDefault + startX
    val top = paddingDefault + startY
    params.setMargins(left, top, paddingDefault, paddingDefault)
    params
  }

}
