package cards.nine.app.ui.launcher.jobs.uiactions

import android.content.Intent
import android.graphics.Color
import android.support.v4.app.{Fragment, FragmentManager}
import android.view.View
import android.widget.ImageView
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
import cards.nine.app.ui.components.drawables.EdgeWorkspaceDrawable
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.AppsMomentLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.EditWidgetsBottomPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.WorkSpaceItemMenuTweaks._
import cards.nine.app.ui.components.layouts.{AnimatedWorkSpacesListener, LauncherWorkSpacesListener, WorkspaceItemMenu}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, MomentWorkSpace, WorkSpaceType}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.actions.widgets.WidgetsFragment
import cards.nine.app.ui.launcher.jobs.{LauncherJobs, NavigationJobs}
import cards.nine.app.ui.launcher.snails.LauncherSnails._
import cards.nine.app.ui.preferences.NineCardsPreferencesActivity
import cards.nine.app.ui.preferences.commons.IsDeveloper
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{ConditionWeather, UnknownCondition}
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit def theme: NineCardsTheme = statuses.theme

  implicit lazy val launcherJobs: LauncherJobs = createLauncherJobs

  implicit lazy val navigationJobs: NavigationJobs = createNavigationJobs

  val maxBackgroundPercent: Float = 0.7f

  val typeWorkspaceButtonKey = "type-workspace-button-key"

  val collectionId = "collectionId"

  def initialize(): TaskService[Unit] = {

    def goToSettings(): Ui[Any] = {
      closeCollectionMenu() ~~ uiStartIntentForResult(
        intent = new Intent(activityContextWrapper.getOriginal, classOf[NineCardsPreferencesActivity]),
        requestCode = goToPreferences)
    }

    ((dom.paginationPanel <~ On.longClick((dom.workspaces <~ lwsOpenMenu) ~ Ui(true))) ~
      (dom.workspacesEdgeLeft <~ vBackground(new EdgeWorkspaceDrawable(left = true))) ~
      (dom.workspacesEdgeRight <~ vBackground(new EdgeWorkspaceDrawable(left = false))) ~
      (dom.menuCollectionRoot <~ vGone) ~
      (dom.editWidgetsBottomPanel <~ ewbInit) ~
      (dom.workspaces <~
        lwsListener(
          LauncherWorkSpacesListener(
            onStartOpenMenu = startOpenCollectionMenu,
            onUpdateOpenMenu = updateOpenCollectionMenu,
            onEndOpenMenu = closeCollectionMenu
          )
        ) <~
        awsListener(AnimatedWorkSpacesListener(
          onClick = () => navigationJobs.clickWorkspaceBackground().resolveAsync(),
          onLongClick = () => (dom.workspaces <~ lwsOpenMenu).run)
        )) ~
      (dom.menuWorkspaceContent <~ vgAddViews(getItemsForFabMenu)) ~
      (dom.menuLauncherWallpaper <~ On.click {
        closeCollectionMenu() ~~ uiStartIntent(new Intent(Intent.ACTION_SET_WALLPAPER))
      }) ~
      (dom.menuLauncherWidgets <~ On.click {
        val widthContent = dom.workspaces.getWidth
        val heightContent = dom.workspaces.getHeight
        val map = Map(
          WidgetsFragment.widgetContentWidth -> widthContent.toString,
          WidgetsFragment.widgetContentHeight -> heightContent.toString
        )
        val bundle = dom.createBundle(Option(dom.menuLauncherWidgets), resGetColor(R.color.primary), map)
        closeCollectionMenu() ~~ Ui(navigationJobs.launchWidgets(bundle).resolveAsync())
      }) ~
      (dom.menuLauncherSettings <~ On.click {
        goToSettings()
      } <~ On.longClick {
        Ui(IsDeveloper.convertToDeveloper) ~
          uiShortToast2(R.string.developerOptionsActivated) ~
          goToSettings() ~
          Ui(true)
      })).toService
  }

  def reloadMoment(data: LauncherData): TaskService[Unit] = {
    val momentType = data.moment.flatMap(_.momentType)
    val launcherMoment = data.moment
    ((dom.workspaces <~ lwsDataMoment(data)) ~
      (dom.appsMoment <~ (launcherMoment map amlPopulate getOrElse Tweak.blank)) ~
      (dom.topBarPanel <~ (momentType map tblReloadMoment getOrElse Tweak.blank))).toService
  }

  def showWeather(condition: ConditionWeather): TaskService[Unit] = (dom.topBarPanel <~ tblWeather(condition)).toService

  def loadLauncherInfo(data: Seq[LauncherData]): TaskService[Unit] = {
    ((dom.loading <~ vGone) ~
      (dom.workspaces <~
        vGlobalLayoutListener(_ =>
          (dom.workspaces <~
            lwsData(data, selectedPageDefault) <~
            lwsAddPageChangedObserver(dom.topBarPanel.movement) <~
            awsAddPageChangedObserver(currentPage => {
              (dom.paginationPanel <~ ivReloadPager(currentPage)).run
            })) ~
            createPager(selectedPageDefault)
        ))).toService
  }

  def closeMenu(): TaskService[Unit] = closeCollectionMenu().toService

  def reloadWorkspaces(data: Seq[LauncherData], page: Option[Int] = None): TaskService[Unit] =
    ((dom.workspaces <~ lwsDataCollections(data, page)) ~ reloadWorkspacePager).toService

  def cleanWorkspaces(): TaskService[Unit] = (dom.workspaces <~ lwsClean).toService

  private[this] def reloadWorkspacePager: Ui[Any] = createPager((dom.workspaces ~> lwsCurrentPage()).get)

  private[this] def createPager(activePosition: Int): Ui[Any] = {
    def pagination(position: Int) = {
      val margin = resGetDimensionPixelSize(R.dimen.margin_pager_collection)
      (w[ImageView] <~
        vWrapContent <~
        llLayoutMargin(marginLeft = margin, marginTop = 0, marginRight = margin, marginBottom = 0) <~
        ivSrc(R.drawable.workspaces_pager) <~ vSetPosition(position)).get
    }

    val pagerViews = 0 until dom.getWorksSpacesCount map { position =>
      val view = pagination(position)
      view.setActivated(activePosition == position)
      view
    }
    dom.paginationPanel <~ vgRemoveAllViews <~ vgAddViews(pagerViews)
  }

  private[this] def closeCollectionMenu(): Ui[Future[Any]] = dom.workspaces <~~ lwsCloseMenu

  private[this] def startOpenCollectionMenu(): Ui[Any] = {

    def showItemsWorkspace(workspaceType: WorkSpaceType) = Transformer {
      case item: WorkspaceItemMenu if item.getField[WorkSpaceType](typeWorkspaceButtonKey).contains(workspaceType) =>
        item <~ vVisible
      case item: WorkspaceItemMenu => item <~ vGone
    }

    val height = dom.menuLauncherContent.getHeight + systemBarsTint.getNavigationBarHeight
    val isCollectionWorkspace = (dom.workspaces ~> lwsIsCollectionWorkspace).get
    val workspaceType = if (isCollectionWorkspace) CollectionsWorkSpace else MomentWorkSpace
    (dom.menuCollectionRoot <~ vVisible <~ vClearClick) ~
      (dom.menuWorkspaceContent <~ showItemsWorkspace(workspaceType) <~ vAlpha(0) <~ vTranslationY(height)) ~
      (dom.menuLauncherContent <~ vTranslationY(height)) ~
      (dom.dockAppsPanel <~ fade(out = true)) ~
      (dom.paginationPanel <~ fade(out = true)) ~
      (dom.topBarPanel <~ fade(out = true))
  }

  private[this] def updateOpenCollectionMenu(percent: Float): Ui[Any] = {
    val backgroundPercent = maxBackgroundPercent * percent
    val colorBackground = Color.BLACK.alpha(backgroundPercent)
    val height = dom.menuLauncherContent.getHeight + systemBarsTint.getNavigationBarHeight
    val translate = height - (height * percent)
    (dom.menuCollectionRoot <~ vBackgroundColor(colorBackground)) ~
      (dom.menuLauncherContent <~ vTranslationY(translate)) ~
      (dom.menuWorkspaceContent <~ vAlpha(percent) <~ vTranslationY(translate))
  }

  private[this] def closeCollectionMenu(opened: Boolean): Ui[Any] =
    if (opened) {
      dom.menuCollectionRoot <~ On.click(closeCollectionMenu())
    } else {
      (dom.dockAppsPanel <~ fade()) ~
        (dom.paginationPanel <~ fade()) ~
        (dom.topBarPanel <~ fade()) ~
        (dom.menuCollectionRoot <~ vGone)
    }

  private[this] def getItemsForFabMenu = Seq(
    (w[WorkspaceItemMenu] <~
      workspaceButtonCreateCollectionStyle <~
      vAddField(typeWorkspaceButtonKey, CollectionsWorkSpace) <~
      FuncOn.click { view: View =>
        Ui {
          val iconView = getIconView(view)
          val bundle = dom.createBundle(iconView, resGetColor(R.color.collection_fab_button_item_1))
          navigationJobs.launchCreateOrCollection(bundle).resolveAsync()
        }
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonMyCollectionsStyle <~
      vAddField(typeWorkspaceButtonKey, CollectionsWorkSpace) <~
      FuncOn.click { view: View =>
        Ui {
          val iconView = getIconView(view)
          val bundle = dom.createBundle(iconView, resGetColor(R.color.collection_fab_button_item_2))
          navigationJobs.launchPrivateCollection(bundle).resolveAsync()
        }
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonPublicCollectionStyle <~
      vAddField(typeWorkspaceButtonKey, CollectionsWorkSpace) <~
      FuncOn.click { view: View =>
        Ui {
          val iconView = getIconView(view)
          val bundle = dom.createBundle(iconView, resGetColor(R.color.collection_fab_button_item_3))
          navigationJobs.launchPublicCollection(bundle).resolveAsync()
        }
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonEditMomentStyle <~
      vAddField(typeWorkspaceButtonKey, MomentWorkSpace) <~
      FuncOn.click { view: View =>
        val momentType = dom.getCurrentMomentTypeName
        momentType match {
          case Some(moment) =>
            Ui {
              val iconView = getIconView(view)
              val momentMap = Map(EditMomentFragment.momentKey -> moment)
              val bundle = dom.createBundle(iconView, resGetColor(R.color.collection_fab_button_item_1), momentMap)
              navigationJobs.launchEditMoment(bundle).resolveAsync()
            }
          case _ => Ui.nop
        }
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonChangeMomentStyle <~
      vAddField(typeWorkspaceButtonKey, MomentWorkSpace) <~
      On.click {
        closeCollectionMenu() ~~ Ui(navigationJobs.goToChangeMoment().resolveAsync())
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonAddMomentStyle <~
      vAddField(typeWorkspaceButtonKey, MomentWorkSpace) <~
      FuncOn.click { view: View =>
        val momentType = dom.getCurrentMomentTypeName
        momentType match {
          case Some(moment) =>
            Ui {
              val iconView = getIconView(view)
              val bundle = dom.createBundle(iconView, resGetColor(R.color.collection_fab_button_item_3))
              navigationJobs.launchAddMoment(bundle).resolveAsync()
            }
          case _ => Ui.nop
        }
      }).get
  )

  private[this] def getIconView(view: View): Option[View] = (view match {
    case wim: WorkspaceItemMenu => Option(wim)
    case _ => None
  }) flatMap (_.icon)

  // Styles

  private[this] def workspaceButtonCreateCollectionStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_1),
      R.drawable.fab_menu_icon_create_new_collection,
      R.string.createNewCollection)

  private[this] def workspaceButtonMyCollectionsStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_2),
      R.drawable.fab_menu_icon_my_collections,
      R.string.myCollections)

  private[this] def workspaceButtonPublicCollectionStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_3),
      R.drawable.fab_menu_icon_public_collections,
      R.string.publicCollections)

  private[this] def workspaceButtonEditMomentStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_1),
      R.drawable.fab_menu_icon_edit_moment,
      R.string.editMoment)

  private[this] def workspaceButtonChangeMomentStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_2),
      R.drawable.fab_menu_icon_change_moment,
      R.string.changeMoment)

  private[this] def workspaceButtonAddMomentStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_3),
      R.drawable.fab_menu_icon_add_moment,
      R.string.addMoment)

}
