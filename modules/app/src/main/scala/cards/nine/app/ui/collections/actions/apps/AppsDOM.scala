package cards.nine.app.ui.collections.actions.apps

import android.support.v4.app.Fragment
import android.widget.EditText
import cards.nine.app.ui.commons.adapters.apps.AppsSelectionAdapter
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.models.ApplicationData
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.Contexts
import macroid.FullDsl._

trait AppsDOM {

  finder: TypedFindView with Contexts[Fragment]  =>

  val searchingGooglePlayKey = "searching-google-play-key"

  lazy val recycler = findView(TR.apps_actions_recycler)

  lazy val selectedAppsContent = findView(TR.selected_apps_content)

  lazy val selectedApps = findView(TR.selected_apps)

  lazy val appsMessage = findView(TR.apps_action_message)

  lazy val scrollerLayout = findView(TR.apps_action_scroller_layout)

  var appKeyword = slot[EditText]

  lazy val searchAppKeyword = (w[EditText] <~ wire(appKeyword)).get

  lazy val headerIconDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CLOSE,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_default))

  def getAdapter: Option[AppsSelectionAdapter] = Option(recycler.getAdapter) match {
    case Some(a: AppsSelectionAdapter) => Some(a)
    case _ => None
  }

}

trait AppsUiListener {

  def loadApps(): Unit

  def loadFilteredApps(keyword: String): Unit

  def loadSearch(query: String): Unit

  def launchGooglePlay(packageName: String): Unit

  def updateSelectedApps(app: ApplicationData): Unit

  def updateCollectionApps(): Unit

}
