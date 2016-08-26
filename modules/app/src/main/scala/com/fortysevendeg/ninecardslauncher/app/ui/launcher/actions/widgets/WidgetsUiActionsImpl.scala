package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import android.widget.{ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.device.models.{AppsWithWidgets, Widget}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import macroid._
import macroid.FullDsl._

trait WidgetsUiActionsImpl
  extends WidgetsUiActions
  with Styles
  with WidgetsStyles {

  self: TypedFindView with BaseActionFragment with Contexts[Fragment] =>

  val unselectedAlpha = .3f

  val selectedAlpha = 1

  implicit val widgetsPresenter: WidgetsPresenter

  implicit val launcherPresenter: LauncherPresenter

  lazy val recycler = findView(TR.widgets_actions_recycler)

  lazy val menu = findView(TR.widgets_actions_menu)

  def loadBackgroundColor = resGetColor(R.color.widgets_background)

  val widgetContentWidth: Int

  val widgetContentHeight: Int

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.widgetsTitle) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  override def loadWidgets(appsWithWidgets: Seq[AppsWithWidgets]): Ui[Any] = {
    val (tag, widgets) = appsWithWidgets.headOption map (app => (app.packageName, app.widgets)) getOrElse ("", Seq.empty)
    val adapter = WidgetsAdapter(Seq.empty, widgetContentWidth, widgetContentHeight)
    (recycler <~
        vVisible <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      loadMenuApps(appsWithWidgets) ~
      showWidgets(tag, widgets)
  }

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def showErrorLoadingWidgetsInScreen(): Ui[Any] = showMessageInScreen(R.string.widgetsErrorMessage, error = true, action = widgetsPresenter.loadWidgets())

  override def close(): Ui[Any] = unreveal()

  private[this] def loadMenuApps(appsWithWidgets: Seq[AppsWithWidgets]): Ui[Any] = {
    val views = appsWithWidgets map { app =>
      (l[LinearLayout](
        w[ImageView] <~ iconMenuItemStyle(app.packageName, app.name) <~ vTag(app.packageName),
        w[TextView] <~ textMenuItemStyle(app.name) <~ vTag(app.packageName)
      ) <~
        contentMenuItemStyle <~
        On.click(showWidgets(app.packageName, app.widgets))).get
    }
    menu <~ vgAddViews(views)
  }

  private[this] def showWidgets(tag: String, widgets: Seq[Widget]) = recycler.getAdapter match {
    case adapter: WidgetsAdapter =>
      (recycler <~ rvSwapAdapter(adapter.copy(widgets))) ~
        (menu <~ Transformer {
          case content: ImageView if content.getTag == tag =>
            content <~ vAlpha(selectedAlpha)
          case content: ImageView =>
            content <~ vAlpha(unselectedAlpha)
          case content: TextView if content.getTag == tag =>
            content <~ vAlpha(selectedAlpha)
          case content: TextView =>
            content <~ vAlpha(unselectedAlpha)
        })
    case _ => Ui.nop
  }

}

case class ViewHolderWidgetsLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], presenter: WidgetsPresenter, launcherPresenter: LauncherPresenter)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  override protected def findViewById(id: Int): View = content.findViewById(id)

  lazy val preview = findView(TR.widget_item_preview)

  lazy val title = findView(TR.widget_item_title)

  lazy val cells = findView(TR.widget_item_cells)

  def bind(widget: Widget, widgetContentWidth: Int, widgetContentHeight: Int): Ui[Any] = {
    val cell = widget.getCell(widgetContentWidth, widgetContentHeight)
    val size = s"${cell.spanX}x${cell.spanY}"
    val iconTweak = if (widget.preview > 0)
      ivSrcIconFromPackage(widget.packageName, widget.preview, widget.label)
    else ivSrcByPackageName(Some(widget.packageName), widget.label)
    (content <~
      On.click(Ui {
        launcherPresenter.hostWidget(widget)
        presenter.close()
      })) ~
      (preview <~ iconTweak) ~
      (title <~ tvText(widget.label)) ~
      (cells <~ tvText(size))
  }

}