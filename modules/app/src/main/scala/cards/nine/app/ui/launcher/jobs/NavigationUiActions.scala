package cards.nine.app.ui.launcher.jobs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.launcher.LauncherPresenter
import cards.nine.app.ui.launcher.actions.createoreditcollection.CreateOrEditCollectionFragment
import cards.nine.app.ui.launcher.actions.editmoment.EditMomentFragment
import cards.nine.app.ui.launcher.actions.privatecollections.PrivateCollectionsFragment
import cards.nine.app.ui.launcher.actions.publicollections.PublicCollectionsFragment
import cards.nine.app.ui.wizard.WizardActivity
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class NavigationUiActions(dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_],
    presenter: LauncherPresenter) {

  val maxBackgroundPercent: Float = 0.7f

  def goToWizard(): TaskService[Unit] =
    uiStartIntent(new Intent(activityContextWrapper.bestAvailable, classOf[WizardActivity])).toService

  def launchCreateOrCollection(bundle: Bundle): TaskService[Unit] =
    showAction(f[CreateOrEditCollectionFragment], bundle).toService

  def launchPrivateCollection(bundle: Bundle): TaskService[Unit] =
    showAction(f[PrivateCollectionsFragment], bundle).toService

  def launchPublicCollection(bundle: Bundle): TaskService[Unit] =
    showAction(f[PublicCollectionsFragment], bundle).toService

  def launchEditMoment(bundle: Bundle, momentMap: Map[String, String]): TaskService[Unit] =
    showAction(f[EditMomentFragment], bundle, momentMap).toService

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], bundle: Bundle, map: Map[String, String] = Map.empty): Ui[Any] = {
    (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
      closeCollectionMenu() ~
      (dom.actionFragmentContent <~ vBackgroundColor(Color.BLACK.alpha(maxBackgroundPercent))) ~
      (dom.fragmentContent <~ vClickable(true)) ~
      fragmentBuilder.pass(bundle).framed(R.id.action_fragment_content, ActionsBehaviours.nameActionFragment)
  }

  private[this] def closeCollectionMenu(): Ui[Future[Any]] = dom.workspaces <~~ lwsCloseMenu

}
