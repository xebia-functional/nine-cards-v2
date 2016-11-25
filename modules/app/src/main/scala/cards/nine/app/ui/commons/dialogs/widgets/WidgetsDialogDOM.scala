package cards.nine.app.ui.commons.dialogs.widgets

import cards.nine.models.AppWidget
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait WidgetsDialogDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.widgets_actions_recycler)

  lazy val menu = findView(TR.widgets_actions_menu)

}

trait WidgetsDialogListener {

  def loadWidgets(): Unit

  def hostWidget(widget: AppWidget): Unit
}