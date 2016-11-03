package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.ui.commons.adapters.apps.AppsAdapter
import cards.nine.models.ApplicationData
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait AppsDOM {

  finder: TypedFindView =>

  lazy val recycler = findView(TR.apps_actions_recycler)

  lazy val selectedApps = findView(TR.selected_apps)

  lazy val scrollerLayout = findView(TR.apps_action_scroller_layout)

  def getAdapter: Option[AppsAdapter] = Option(recycler.getAdapter) match {
    case Some(a: AppsAdapter) => Some(a)
    case _ => None
  }

}

trait AppsUiListener {

  def loadApps(): Unit

  def addApp(app: ApplicationData): Unit

}
