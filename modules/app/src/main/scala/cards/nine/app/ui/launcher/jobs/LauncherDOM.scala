package cards.nine.app.ui.launcher.jobs

import android.app.Activity
import android.support.v4.view.GravityCompat
import android.view.View
import cards.nine.app.ui.commons.ActivityFindViews
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.layouts.tweaks.TabsViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.components.models.LauncherData
import cards.nine.app.ui.components.widgets.ContentView
import cards.nine.models.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.TR
import macroid._

class LauncherDOM(activity: Activity) {

  import ActivityFindViews._

  lazy val foreground = findView(TR.launcher_foreground).run(activity)

  lazy val appsMoment = findView(TR.launcher_apps_moment).run(activity)

  lazy val drawerLayout = findView(TR.launcher_drawer_layout).run(activity)

  lazy val navigationView = findView(TR.launcher_navigation_view).run(activity)

  lazy val menuName = findView(TR.menu_name).run(activity)

  lazy val menuEmail = findView(TR.menu_email).run(activity)

  lazy val menuAvatar = findView(TR.menu_avatar).run(activity)

  lazy val menuCover = findView(TR.menu_cover).run(activity)

  lazy val loading = findView(TR.launcher_loading).run(activity)

  lazy val root = findView(TR.launcher_root).run(activity)

  lazy val dockAppsPanel = findView(TR.launcher_dock_apps_panel).run(activity)

  lazy val content = findView(TR.launcher_content).run(activity)

  lazy val workspaces = findView(TR.launcher_work_spaces).run(activity)

  lazy val workspacesEdgeLeft = findView(TR.launcher_work_spaces_edge_left).run(activity)

  lazy val workspacesEdgeRight = findView(TR.launcher_work_spaces_edge_right).run(activity)

  lazy val paginationPanel = findView(TR.launcher_pagination_panel).run(activity)

  lazy val topBarPanel = findView(TR.launcher_top_bar_panel).run(activity)

  lazy val editWidgetsTopPanel = findView(TR.launcher_edit_widgets_top_panel).run(activity)

  lazy val editWidgetsBottomPanel = findView(TR.launcher_edit_widgets_bottom_panel).run(activity)

  lazy val collectionActionsPanel = findView(TR.launcher_collections_actions_panel).run(activity)

  lazy val actionFragmentContent = findView(TR.action_fragment_content).run(activity)

  lazy val menuCollectionRoot = findView(TR.menu_collection_root).run(activity)

  lazy val menuWorkspaceContent = findView(TR.menu_workspace_content).run(activity)

  lazy val menuLauncherContent = findView(TR.menu_launcher_content).run(activity)

  lazy val menuLauncherWallpaper = findView(TR.menu_launcher_wallpaper).run(activity)

  lazy val menuLauncherWidgets = findView(TR.menu_launcher_widgets).run(activity)

  lazy val menuLauncherSettings = findView(TR.menu_launcher_settings).run(activity)

  lazy val fragmentContent = findView(TR.action_fragment_content).run(activity)

  lazy val appDrawerMain = findView(TR.launcher_app_drawer).run(activity)

  lazy val drawerContent = findView(TR.launcher_drawer_content).run(activity)

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout).run(activity)

  lazy val paginationDrawerPanel = findView(TR.launcher_drawer_pagination_panel).run(activity)

  lazy val recycler = findView(TR.launcher_drawer_recycler).run(activity)

  lazy val tabs = findView(TR.launcher_drawer_tabs).run(activity)

  lazy val pullToTabsView = findView(TR.launcher_drawer_pull_to_tabs).run(activity)

  lazy val screenAnimation = findView(TR.launcher_drawer_swipe_animated).run(activity)

  lazy val searchBoxView = findView(TR.launcher_search_box_content).run(activity)

  def getData: Seq[LauncherData] = workspaces.data

  def getCurrentMomentType: Option[NineCardsMoment] = getData.headOption flatMap (_.moment) flatMap (_.momentType)

  def isMenuVisible: Boolean = drawerLayout.isDrawerOpen(GravityCompat.START)

  def isCollectionMenuVisible: Boolean = workspaces.workSpacesStatuses.openedMenu

  def isDrawerTabsOpened: Boolean = (tabs ~> isOpened).get

  def getStatus: Option[String] = recycler.getType

  def getTypeView: Option[ContentView] = Option(recycler.statuses.contentView)

  def getItemsCount: Int = Option(recycler.getAdapter) map (_.getItemCount) getOrElse 0

  def getDrawerWidth: Int = drawerContent.getWidth

  def isDrawerVisible = drawerContent.getVisibility == View.VISIBLE

  def isEmptyCollections: Boolean = (workspaces ~> lwsEmptyCollections).get

}
