package cards.nine.app.ui.launcher.actions.widgets

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.{LauncherActivity, LauncherPresenter}
import com.fortysevendeg.ninecardslauncher.R
import WidgetsFragment._
import cards.nine.models.AppWidget

class WidgetsFragment
  extends BaseActionFragment
  with WidgetsUiActions
  with WidgetsDOM
  with WidgetsListener
  with AppNineCardIntentConversions {

  // TODO First implementation in order to remove LauncherPresenter
  def launcherPresenter: LauncherPresenter = getActivity match {
    case activity: LauncherActivity => activity.presenter
    case _ => throw new RuntimeException("LauncherPresenter not found")
  }

  lazy val widgetContentWidth = getString(Seq(getArguments), WidgetsFragment.widgetContentWidth, "0").toInt

  lazy val widgetContentHeight = getString(Seq(getArguments), WidgetsFragment.widgetContentHeight, "0").toInt

  override def getLayoutId: Int = R.layout.widgets_action_fragment

  override protected lazy val backgroundColor: Int = resGetColor(R.color.widgets_background)

  lazy val widgetsJobs = new WidgetsJobs(this)

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    statuses = statuses.copy(widgetContentWidth = widgetContentWidth, widgetContentHeight = widgetContentHeight)
    widgetsJobs.initialize().resolveAsyncServiceOr(_ => showErrorLoadingWidgetsInScreen())
  }

  override def loadWidgets(): Unit =
    widgetsJobs.loadWidgets().resolveAsyncServiceOr(_ => showErrorLoadingWidgetsInScreen())

  override def hostWidget(widget: AppWidget): Unit = {
    launcherPresenter.hostWidget(widget)
    widgetsJobs.close().resolveAsync()
  }
}

object WidgetsFragment {

  var statuses = WidgetStatuses()

  val widgetContentWidth = "widget-content-width"

  val widgetContentHeight = "widget-content-height"

}

case class WidgetStatuses(widgetContentWidth: Int = 0, widgetContentHeight: Int = 0)
