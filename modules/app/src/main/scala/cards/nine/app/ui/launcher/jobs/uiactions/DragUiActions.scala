package cards.nine.app.ui.launcher.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{AppUtils, UiContext}
import cards.nine.app.ui.components.layouts._
import cards.nine.app.ui.components.layouts.tweaks.CollectionActionsPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{AppCardType, CardType}
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class DragUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  case class State(theme: NineCardsTheme = AppUtils.getDefaultTheme)

  private[this] var actionsState = State()

  implicit def theme: NineCardsTheme = actionsState.theme

  lazy val actionForCollections = Seq(
    CollectionActionItem(resGetString(R.string.edit), R.drawable.icon_launcher_action_edit, CollectionActionEdit),
    CollectionActionItem(resGetString(R.string.remove), R.drawable.icon_launcher_action_remove, CollectionActionRemove))

  lazy val actionForApps = Seq(
    CollectionActionItem(resGetString(R.string.appInfo), R.drawable.icon_launcher_action_info_app, CollectionActionAppInfo),
    CollectionActionItem(resGetString(R.string.uninstall), R.drawable.icon_launcher_action_uninstall, CollectionActionUninstall))

  def initialize(nineCardsTheme: NineCardsTheme): TaskService[Unit] =
    TaskService.right {
      actionsState = actionsState.copy(theme = nineCardsTheme)
    }

  def startAddItem(cardType: CardType): TaskService[Unit] = {
    ((dom.topBarPanel <~ applyFadeOut()) ~
      (cardType match {
        case AppCardType => dom.collectionActionsPanel <~ caplLoad(actionForApps) <~ applyFadeIn()
        case _ => Ui.nop
      }) ~
      reloadEdges()).toService
  }

  def endAddItem(): TaskService[Unit] =
    ((dom.topBarPanel <~ applyFadeIn()) ~
      (dom.collectionActionsPanel <~~ applyFadeOut()) ~
      hideEdges()).toService

  def startReorder(): TaskService[Unit] =
    ((dom.dockAppsPanel <~ applyFadeOut()) ~
      (dom.topBarPanel <~ applyFadeOut()) ~
      (dom.collectionActionsPanel <~ caplLoad(actionForCollections) <~ applyFadeIn()) ~
      reloadEdges()).toService

  def endReorder(): TaskService[Unit] =
    ((dom.dockAppsPanel <~ applyFadeIn()) ~
      (dom.topBarPanel <~ applyFadeIn()) ~
      (dom.collectionActionsPanel <~~ applyFadeOut()) ~
      hideEdges()).toService

  def goToNextScreenReordering(): TaskService[Unit] = {
    val canMoveToNextScreen = (dom.workspaces ~> lwsCanMoveToNextScreenOnlyCollections()).get
    (goToNextWorkspace() ~
      (dom.workspaces <~ lwsPrepareItemsScreenInReorder(0)) ~
      reloadEdges()).ifUi(canMoveToNextScreen).toService
  }

  def goToPreviousScreenReordering(): TaskService[Unit] = {
    val canMoveToPreviousScreen = (dom.workspaces ~> lwsCanMoveToPreviousScreenOnlyCollections()).get
    (goToPreviousWorkspace() ~
      (dom.workspaces <~ lwsPrepareItemsScreenInReorder(numSpaces - 1)) ~
      reloadEdges()).ifUi(canMoveToPreviousScreen).toService
  }

  def goToPreviousScreenAddingItem(): TaskService[Unit] = {
    val canMoveToPreviousScreen = (dom.workspaces ~> lwsCanMoveToPreviousScreen()).get
    (goToPreviousWorkspace() ~ reloadEdges()).ifUi(canMoveToPreviousScreen).toService
  }

  def goToNextScreenAddingItem(): TaskService[Unit] = {
    val canMoveToNextScreen = (dom.workspaces ~> lwsCanMoveToNextScreen()).get
    (goToNextWorkspace() ~ reloadEdges()).ifUi(canMoveToNextScreen).toService
  }

  private[this] def goToNextWorkspace(): Ui[Any] =
    (dom.workspaces ~> lwsNextScreen()).get map { next =>
      goToWorkspace(next)
    } getOrElse Ui.nop

  private[this] def goToPreviousWorkspace(): Ui[Any] =
    (dom.workspaces ~> lwsPreviousScreen()).get map { previous =>
      goToWorkspace(previous)
    } getOrElse Ui.nop

  private[this] def goToWorkspace(page: Int): Ui[Any] =
    (dom.getData.lift(page) map (data => dom.topBarPanel <~ tblReloadByType(data.workSpaceType)) getOrElse Ui.nop) ~
      (dom.workspaces <~ lwsSelect(page)) ~
      (dom.paginationPanel <~ ivReloadPager(page))

  private[this] def reloadEdges(): Ui[Any] = {
    val canMoveToNextScreen = (dom.workspaces ~> lwsCanMoveToNextScreenOnlyCollections()).get
    val canMoveToPreviousScreen = (dom.workspaces ~> lwsCanMoveToPreviousScreenOnlyCollections()).get
    (dom.workspacesEdgeLeft <~ (if (canMoveToPreviousScreen) vVisible else vGone)) ~
      (dom.workspacesEdgeRight <~ (if (canMoveToNextScreen) vVisible else vGone))
  }

  private[this] def hideEdges(): Ui[Any] =
    (dom.workspacesEdgeLeft <~ vGone) ~
      (dom.workspacesEdgeRight <~ vGone)

}
