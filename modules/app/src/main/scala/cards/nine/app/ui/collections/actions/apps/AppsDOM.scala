package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.ui.commons.adapters.apps.AppsAdapter
import cards.nine.models.ApplicationData
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait AppsDOM {

  finder: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  lazy val pullToTabsView = findView(TR.actions_pull_to_tabs)

  lazy val tabs = findView(TR.actions_tabs)

  def getAdapter: Option[AppsAdapter] = Option(recycler.getAdapter) match {
    case Some(a: AppsAdapter) => Some(a)
    case _ => None
  }

}

trait AppsUiListener {

  def loadApps(filter: AppsFilter): Unit

  def addApp(app: ApplicationData): Unit

  def swapFilter(): Unit
}
