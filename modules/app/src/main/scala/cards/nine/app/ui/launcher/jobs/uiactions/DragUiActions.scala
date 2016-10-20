package cards.nine.app.ui.launcher.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{AppUtils, UiContext}
import cards.nine.app.ui.components.layouts._
import cards.nine.app.ui.components.layouts.tweaks.CollectionActionsPanelLayoutTweaks._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{AppCardType, CardType}
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

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
