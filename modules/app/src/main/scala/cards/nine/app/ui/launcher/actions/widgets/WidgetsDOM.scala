package cards.nine.app.ui.launcher.actions.widgets

import cards.nine.models.AppWidget
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait WidgetsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.widgets_actions_recycler)

  lazy val menu = findView(TR.widgets_actions_menu)

}

trait WidgetsListener {

  def loadWidgets(): Unit

  def hostWidget(widget: AppWidget): Unit
}