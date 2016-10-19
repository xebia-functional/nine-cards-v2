package cards.nine.app.ui.launcher.jobs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
import cards.nine.app.ui.components.dialogs.AlertDialogFragment
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.actions.createoreditcollection.CreateOrEditCollectionFragment
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.actions.privatecollections.PrivateCollectionsFragment
import cards.nine.app.ui.launcher.actions.publicollections.PublicCollectionsFragment
import cards.nine.app.ui.launcher.actions.widgets.WidgetsFragment
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

case class NavigationUiActions(dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit lazy val widgetsJobs = createWidgetsJobs

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

  def deleteSelectedWidget(): TaskService[Unit] = TaskService.right {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        val dialog = new AlertDialogFragment(
          message = R.string.removeWidgetMessage,
          positiveAction = () => widgetsJobs.deleteDBWidget().resolveAsyncServiceOr(_ =>
            widgetsJobs.navigationUiActions.showContactUsError()))
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  def showAddItemMessage(nameCollection: String): TaskService[Unit] = showMessage(R.string.itemAddedToCollectionSuccessful, Seq(nameCollection))

  def showWidgetCantResizeMessage(): TaskService[Unit] = showMessage(R.string.noResizeForWidget)

  def showWidgetCantMoveMessage(): TaskService[Unit] = showMessage(R.string.noMoveForWidget)

  def showWidgetNoHaveSpaceMessage(): TaskService[Unit] = showMessage(R.string.noSpaceForWidget)

  def showContactUsError(): TaskService[Unit] = showMessage(R.string.contactUsError)

  def showMinimumOneCollectionMessage(): TaskService[Unit]= showMessage(R.string.minimumOneCollectionMessage)

  def showNoImplementedYetMessage(): TaskService[Unit] = showMessage(R.string.todo)

  def showNoPhoneCallPermissionError: TaskService[Unit] = showMessage(R.string.noPhoneCallPermissionMessage)

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

  private[this] def showMessage(res: Int, args: Seq[String] = Seq.empty): TaskService[Unit] =
    (dom.workspaces <~ vLauncherSnackbar(res, args)).toService

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], bundle: Bundle): Ui[Any] = {
    (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
      closeCollectionMenu() ~
      (dom.actionFragmentContent <~ vBackgroundColor(Color.BLACK.alpha(maxBackgroundPercent))) ~
      (dom.fragmentContent <~ vClickable(true)) ~
      fragmentBuilder.pass(bundle).framed(R.id.action_fragment_content, ActionsBehaviours.nameActionFragment)
  }

  private[this] def closeCollectionMenu(): Ui[Future[Any]] = dom.workspaces <~~ lwsCloseMenu

}
