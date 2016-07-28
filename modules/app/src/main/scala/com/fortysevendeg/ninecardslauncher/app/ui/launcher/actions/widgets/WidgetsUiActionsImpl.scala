package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.device.models.Widget
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._
import macroid.FullDsl._

trait WidgetsUiActionsImpl
  extends WidgetsUiActions
  with Styles {

  self: TypedFindView with BaseActionFragment with Contexts[Fragment] =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  implicit val widgetsPresenter: WidgetsPresenter

  implicit val launcherPresenter: LauncherPresenter

  val widgetContentWidth: Int

  val widgetContentHeight: Int

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.widgetsTitle) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  override def loadWidgets(widgets: Seq[Widget]): Ui[Any] = {
    val adapter = WidgetsAdapter(widgets, widgetContentWidth, widgetContentHeight)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def close(): Ui[Any] = unreveal()

}

case class ViewHolderWidgetsLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, presenter: WidgetsPresenter, launcherPresenter: LauncherPresenter)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  override protected def findViewById(id: Int): View = content.findViewById(id)

  lazy val preview = findView(TR.widget_item_preview)

  lazy val title = findView(TR.widget_item_title)

  lazy val cells = findView(TR.widget_item_cells)

  def bind(widget: Widget, widgetContentWidth: Int, widgetContentHeight: Int): Ui[Any] = {
    val cell = widget.getCell(widgetContentWidth, widgetContentHeight)
    val size = s"${cell.spanX}x${cell.spanY}"
    (content <~
      On.click(Ui {
        launcherPresenter.hostWidget(widget)
        presenter.close()
      })) ~
      (preview <~ (widget.preview map ivSrc getOrElse ivSrc(widget.icon))) ~
      (title <~ tvText(widget.label)) ~
      (cells <~ tvText(size))
  }

}