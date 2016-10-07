package cards.nine.app.ui.launcher.actions.widgets

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher2.R

class WidgetsFragment(implicit lPresenter: LauncherPresenter)
  extends BaseActionFragment
  with WidgetsUiActionsImpl
  with AppNineCardsIntentConversions {

  override lazy val widgetContentWidth = getString(Seq(getArguments), WidgetsFragment.widgetContentWidth, "0").toInt

  override lazy val widgetContentHeight = getString(Seq(getArguments), WidgetsFragment.widgetContentHeight, "0").toInt

  override def getLayoutId: Int = R.layout.widgets_action_fragment

  override protected lazy val backgroundColor: Int = loadBackgroundColor

  override lazy val widgetsPresenter = new WidgetsPresenter(this)

  override val launcherPresenter: LauncherPresenter = lPresenter

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    widgetsPresenter.initialize()
  }

}

object WidgetsFragment {

  val widgetContentWidth = "widget-content-width"

  val widgetContentHeight = "widget-content-height"

}
