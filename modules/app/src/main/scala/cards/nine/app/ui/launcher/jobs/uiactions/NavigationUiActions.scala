package cards.nine.app.ui.launcher.jobs.uiactions

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{RequestCodes, SystemBarsTint, UiContext}
import cards.nine.app.ui.components.dialogs.AlertDialogFragment
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.actions.createoreditcollection.CreateOrEditCollectionFragment
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.actions.privatecollections.PrivateCollectionsFragment
import cards.nine.app.ui.launcher.actions.publicollections.PublicCollectionsFragment
import cards.nine.app.ui.launcher.actions.widgets.WidgetsFragment
import cards.nine.app.ui.profile.ProfileActivity
import cards.nine.app.ui.wizard.WizardActivity
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NavigationUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit lazy val widgetsJobs = createWidgetsJobs

  val pageMoments = 0

  val pageCollections = 1

  val maxBackgroundPercent: Float = 0.7f

  val tagDialog = "dialog"

  def goToWizard(): TaskService[Unit] =
    uiStartIntent(new Intent(activityContextWrapper.bestAvailable, classOf[WizardActivity])).toService

  def launchCreateOrCollection(bundle: Bundle): TaskService[Unit] =
    showAction(f[CreateOrEditCollectionFragment], bundle).toService

  def launchPrivateCollection(bundle: Bundle): TaskService[Unit] =
    showAction(f[PrivateCollectionsFragment], bundle).toService

  def launchPublicCollection(bundle: Bundle): TaskService[Unit] =
    showAction(f[PublicCollectionsFragment], bundle).toService

  def launchEditMoment(bundle: Bundle): TaskService[Unit] =
    showAction(f[EditMomentFragment], bundle).toService

  def launchWidgets(bundle: Bundle): TaskService[Unit] =
    showAction(f[WidgetsFragment], bundle).toService

  def deleteSelectedWidget(): TaskService[Unit] = Ui {
    val ft = fragmentManagerContext.manager.beginTransaction()
    Option(fragmentManagerContext.manager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    val dialog = new AlertDialogFragment(
      message = R.string.removeWidgetMessage,
      positiveAction = () => widgetsJobs.deleteDBWidget().resolveAsyncServiceOr(_ =>
        widgetsJobs.navigationUiActions.showContactUsError()))
    dialog.show(ft, tagDialog)
  }.toService

  def showAddItemMessage(nameCollection: String): TaskService[Unit] =
    showMessage(R.string.itemAddedToCollectionSuccessful, Seq(nameCollection)).toService

  def showWidgetCantResizeMessage(): TaskService[Unit] = showMessage(R.string.noResizeForWidget).toService

  def showWidgetCantMoveMessage(): TaskService[Unit] = showMessage(R.string.noMoveForWidget).toService

  def showWidgetNoHaveSpaceMessage(): TaskService[Unit] = showMessage(R.string.noSpaceForWidget).toService

  def showContactUsError(): TaskService[Unit] = showMessage(R.string.contactUsError).toService

  def showMinimumOneCollectionMessage(): TaskService[Unit]= showMessage(R.string.minimumOneCollectionMessage).toService

  def showNoImplementedYetMessage(): TaskService[Unit] = showMessage(R.string.todo).toService

  def showNoPhoneCallPermissionError(): TaskService[Unit] = showMessage(R.string.noPhoneCallPermissionMessage).toService

  def showContactPermissionError(action: () => Unit): TaskService[Unit] =
    showMessageWithAction(R.string.errorContactsPermission, R.string.buttonTryAgain, action).toService

  def showCallPermissionError(action: () => Unit): TaskService[Unit] =
    showMessageWithAction(R.string.errorCallsPermission, R.string.buttonTryAgain, action).toService

  def removeActionFragment(): TaskService[Unit] =
    dom.getFragment match {
      case Some(fragment) => TaskService.right(removeFragment(fragment))
      case _ => TaskService.empty
    }

  def unrevealActionFragment: TaskService[Unit] =
    dom.getFragment match {
      case Some(fragment) => fragment.unreveal().toService
      case _ => TaskService.empty
    }

  def goToProfile(): TaskService[Unit] =
    uiStartIntentForResult(
      new Intent(activityContextWrapper.bestAvailable, classOf[ProfileActivity]), RequestCodes.goToProfile).toService

  def goToMomentWorkspace(): TaskService[Unit] = goToWorkspace(pageMoments)

  def goToCollectionWorkspace(): TaskService[Unit] = goToWorkspace(pageCollections)

  def goToWorkspace(page: Int): TaskService[Unit] = {
    ((dom.getData.lift(page) map (data => dom.topBarPanel <~ tblReloadByType(data.workSpaceType)) getOrElse Ui.nop) ~
      (dom.workspaces <~ lwsSelect(page)) ~
      (dom.paginationPanel <~ ivReloadPager(page))).toService
  }

  def goToNextWorkspace(): TaskService[Unit] =
    (dom.workspaces ~> lwsNextScreen()).get map { next =>
      goToWorkspace(next)
    } getOrElse TaskService.empty

  def goToPreviousWorkspace(): TaskService[Unit] =
    (dom.workspaces ~> lwsPreviousScreen()).get map { previous =>
      goToWorkspace(previous)
    } getOrElse TaskService.empty

  private[this] def showMessage(res: Int, args: Seq[String] = Seq.empty): Ui[Any] =
    dom.workspaces <~ vLauncherSnackbar(res, args)

  private[this] def showMessageWithAction(resMessage: Int, resButton: Int, action: () => Unit): Ui[Any] =
    dom.workspaces <~ vLauncherSnackbarWithAction(resMessage, resButton, action, lenght = Snackbar.LENGTH_LONG)

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], bundle: Bundle): Ui[Any] = {
    (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
      closeCollectionMenu() ~
      (dom.actionFragmentContent <~ vBackgroundColor(Color.BLACK.alpha(maxBackgroundPercent))) ~
      (dom.fragmentContent <~ vClickable(true)) ~
      fragmentBuilder.pass(bundle).framed(R.id.action_fragment_content, dom.nameActionFragment)
  }

  private[this] def closeCollectionMenu(): Ui[Future[Any]] = dom.workspaces <~~ lwsCloseMenu

}
