package cards.nine.app.ui.commons.dialogs.apps

import android.support.v4.app.Fragment
import cards.nine.app.ui.commons.adapters.apps.AppsSelectionAdapter
import cards.nine.models.{ApplicationData, NotCategorizedPackage}
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}
import macroid.Contexts

trait AppsDOM { finder: TypedFindView with Contexts[Fragment] =>

  val searchingGooglePlayKey = "searching-google-play-key"

  lazy val recycler = findView(TR.apps_actions_recycler)

  lazy val selectedAppsContent = findView(TR.selected_apps_content)

  lazy val selectedApps = findView(TR.selected_apps)

  lazy val appsMessage = findView(TR.apps_action_message)

  def getAdapter: Option[AppsSelectionAdapter] =
    Option(recycler.getAdapter) match {
      case Some(a: AppsSelectionAdapter) => Some(a)
      case _                             => None
    }

}

trait AppsUiListener {

  def loadApps(): Unit

  def loadFilteredApps(keyword: String): Unit

  def loadSearch(query: String): Unit

  def launchGooglePlay(app: NotCategorizedPackage): Unit

  def updateSelectedApps(app: ApplicationData): Unit

  def updateCollectionApps(): Unit

}
