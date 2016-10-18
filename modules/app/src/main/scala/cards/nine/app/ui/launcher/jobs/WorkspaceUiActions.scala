package cards.nine.app.ui.launcher.jobs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.view.View
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.{AppUtils, SystemBarsTint, UiContext}
import cards.nine.app.ui.components.drawables.EdgeWorkspaceDrawable
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.EditWidgetsBottomPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.WorkSpaceItemMenuTweaks._
import cards.nine.app.ui.components.layouts.{AnimatedWorkSpacesListener, LauncherWorkSpacesListener, WorkspaceItemMenu}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, MomentWorkSpace, WorkSpaceType}
import cards.nine.app.ui.launcher.actions.createoreditcollection.CreateOrEditCollectionFragment
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.actions.privatecollections.PrivateCollectionsFragment
import cards.nine.app.ui.launcher.actions.publicollections.PublicCollectionsFragment
import cards.nine.app.ui.launcher.snails.LauncherSnails._
import cards.nine.app.ui.preferences.NineCardsPreferencesActivity
import cards.nine.app.ui.preferences.commons.IsDeveloper
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceUiActions(dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit lazy val systemBarsTint = new SystemBarsTint

  case class State(theme: NineCardsTheme = AppUtils.getDefaultTheme)

  private[this] var actionsState = State()

  implicit def theme: NineCardsTheme = actionsState.theme

  val maxBackgroundPercent: Float = 0.7f

  val typeWorkspaceButtonKey = "type-workspace-button-key"

  val collectionId = "collectionId"

  def initialize(nineCardsTheme: NineCardsTheme): TaskService[Unit] = {

    actionsState = actionsState.copy(theme = nineCardsTheme)

    def goToSettings(): Ui[Any] = {
      closeCollectionMenu() ~~ uiStartIntentForResult(
        intent = new Intent(activityContextWrapper.getOriginal, classOf[NineCardsPreferencesActivity]),
        requestCode = goToPreferences)
    }

    ((dom.paginationPanel <~ On.longClick((dom.workspaces <~ lwsOpenMenu) ~ Ui(true))) ~
      (dom.topBarPanel <~ tblInit(CollectionsWorkSpace)) ~
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
          onClick = () => (), //presenter.clickWorkspaceBackground(),
          onLongClick = () => (dom.workspaces <~ lwsOpenMenu).run)
        )) ~
      (dom.menuWorkspaceContent <~ vgAddViews(getItemsForFabMenu)) ~
      (dom.menuLauncherWallpaper <~ On.click {
        closeCollectionMenu() ~~ uiStartIntent(new Intent(Intent.ACTION_SET_WALLPAPER))
      }) ~
      (dom.menuLauncherWidgets <~ On.click {
        closeCollectionMenu() //~~ Ui(presenter.goToWidgets())
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

  def closeCollectionMenu(): Ui[Future[Any]] = dom.workspaces <~~ lwsCloseMenu

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
        val iconView = getIconView(view)
        showAction(f[CreateOrEditCollectionFragment], iconView, resGetColor(R.color.collection_group_1))
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonMyCollectionsStyle <~
      vAddField(typeWorkspaceButtonKey, CollectionsWorkSpace) <~
      FuncOn.click { view: View =>
        val iconView = getIconView(view)
        showAction(f[PrivateCollectionsFragment], iconView, resGetColor(R.color.collection_fab_button_item_my_collections))
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonPublicCollectionStyle <~
      vAddField(typeWorkspaceButtonKey, CollectionsWorkSpace) <~
      FuncOn.click { view: View =>
        val iconView = getIconView(view)
        showAction(f[PublicCollectionsFragment], iconView, resGetColor(R.color.collection_fab_button_item_public_collection))
      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonEditMomentStyle <~
      vAddField(typeWorkspaceButtonKey, MomentWorkSpace) <~
      FuncOn.click { view: View =>
        val momentType = dom.getData.headOption flatMap (_.moment) flatMap (_.momentType) map (_.name)
        momentType match {
          case Some(moment) =>
            val iconView = getIconView(view)
            val momentMap = Map(EditMomentFragment.momentKey -> moment)
            showAction(f[EditMomentFragment], iconView, resGetColor(R.color.collection_fab_button_item_edit_moment), momentMap)
          case _ => Ui.nop
        }

      }).get,
    (w[WorkspaceItemMenu] <~
      workspaceButtonChangeMomentStyle <~
      vAddField(typeWorkspaceButtonKey, MomentWorkSpace) <~
      On.click {
        closeCollectionMenu() //~~ Ui(presenter.goToChangeMoment())
      }).get
  )

  private[this] def getIconView(view: View): Option[View] = (view match {
    case wim: WorkspaceItemMenu => Option(wim)
    case _ => None
  }) flatMap (_.icon)

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], maybeView: Option[View], color: Int, map: Map[String, String] = Map.empty): Ui[Any] = {
    val sizeIconWorkSpaceMenuItem = resGetDimensionPixelSize(R.dimen.size_workspace_menu_item)
    val (startX: Int, startY: Int) = maybeView map (_.calculateAnchorViewPosition) getOrElse(0, 0)
    val (startWX: Int, startWY: Int) = dom.workspaces.calculateAnchorViewPosition
    val (endPosX: Int, endPosY: Int) = (startWX + dom.workspaces.animatedWorkspaceStatuses.dimen.width / 2, startWY + dom.workspaces.animatedWorkspaceStatuses.dimen.height / 2)
    val x = startX + (sizeIconWorkSpaceMenuItem / 2)
    val y = startY + (sizeIconWorkSpaceMenuItem / 2)
    val args = new Bundle()
    args.putInt(BaseActionFragment.sizeIcon, sizeIconWorkSpaceMenuItem)
    args.putInt(BaseActionFragment.startRevealPosX, x)
    args.putInt(BaseActionFragment.startRevealPosY, y)
    args.putInt(BaseActionFragment.endRevealPosX, endPosX)
    args.putInt(BaseActionFragment.endRevealPosY, endPosY)
    map foreach {
      case (key, value) => args.putString(key, value)
    }
    args.putInt(BaseActionFragment.colorPrimary, color)
    (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
      closeCollectionMenu() ~
      (dom.actionFragmentContent <~ vBackgroundColor(Color.BLACK.alpha(maxBackgroundPercent))) ~
      (dom.fragmentContent <~ vClickable(true)) ~
      addFragment(fragmentBuilder.pass(args), Option(R.id.action_fragment_content), Option(ActionsBehaviours.nameActionFragment))
  }

  // Styles

  private[this] def workspaceButtonCreateCollectionStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_group_1),
      R.drawable.fab_menu_icon_create_new_collection,
      R.string.createNewCollection)

  private[this] def workspaceButtonMyCollectionsStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_my_collections),
      R.drawable.fab_menu_icon_my_collections,
      R.string.myCollections)

  private[this] def workspaceButtonPublicCollectionStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_public_collection),
      R.drawable.fab_menu_icon_public_collections,
      R.string.publicCollections)

  private[this] def workspaceButtonChangeMomentStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_change_moment),
      R.drawable.fab_menu_icon_change_moment,
      R.string.changeMoment)

  private[this] def workspaceButtonEditMomentStyle(implicit context: ContextWrapper): Tweak[WorkspaceItemMenu] =
    wimPopulate(resGetColor(R.color.collection_fab_button_item_edit_moment),
      R.drawable.fab_menu_icon_edit_moment,
      R.string.editMoment)

}
