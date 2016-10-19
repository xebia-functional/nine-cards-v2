package cards.nine.app.ui.launcher.actions.widgets

import android.text.TextUtils.TruncateAt
import android.view.{Gravity, ViewGroup}
import android.widget.{ImageView, LinearLayout, TextView}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.launcher.actions.widgets.WidgetsFragment._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.AppWidget
import cards.nine.process.device.models.AppsWithWidgets
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

trait WidgetsDialogUiActions
  extends Styles {

  self: BaseActionFragment with WidgetsDialogDOM with WidgetsDialogListener =>

  val unselectedAlpha = .3f

  val selectedAlpha = 1

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.widgetsTitle) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService

  def loadWidgets(appsWithWidgets: Seq[AppsWithWidgets]): TaskService[Unit] = {
    val (tag, widgets) = appsWithWidgets.headOption map (app => (app.packageName, app.widgets)) getOrElse ("", Seq.empty)
    val adapter = WidgetsAdapter(Seq.empty, statuses.widgetContentWidth, statuses.widgetContentHeight, hostWidget)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      loadMenuApps(appsWithWidgets) ~
      showWidgets(tag, widgets)).toService
  }

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def showErrorLoadingWidgetsInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.widgetsErrorMessage, error = true, action = loadWidgets()).toService

  def close(): TaskService[Unit] = unreveal().toService

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

  private[this] def showWidgets(tag: String, widgets: Seq[AppWidget]) = recycler.getAdapter match {
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

  private[this] def contentMenuItemStyle: Tweak[LinearLayout] =
    vMatchWidth +
      llHorizontal +
      llGravity(Gravity.CENTER_VERTICAL)

  private[this] def iconMenuItemStyle(packageName: String, name: String)
    (implicit contextWrapper: ContextWrapper, uiContext: UiContext[_]): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_widget_icon)
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    lp[ViewGroup](size, size) +
      vPaddings(padding) +
      ivSrcByPackageName(Some(packageName), name)
  }

  private[this] def textMenuItemStyle(name: String)(implicit contextWrapper: ContextWrapper): Tweak[TextView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    llWrapWeightHorizontal +
      tvColorResource(R.color.widgets_text) +
      vPadding(paddingLeft = padding) +
      tvText(name) +
      tvLines(1) +
      tvNormalMedium +
      tvEllipsize(TruncateAt.END) +
      tvSizeResource(R.dimen.text_large)
  }

}