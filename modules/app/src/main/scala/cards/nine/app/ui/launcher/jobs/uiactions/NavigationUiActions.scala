/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.launcher.jobs.uiactions

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import cards.nine.app.ui.collections.CollectionsDetailsActivity
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.dialogs.{AlertDialogFragment, MomentDialog}
import cards.nine.app.ui.components.drawables.RippleCollectionDrawable
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.commons.dialogs.addmoment.AddMomentFragment
import cards.nine.app.ui.commons.dialogs.createoreditcollection.CreateOrEditCollectionFragment
import cards.nine.app.ui.commons.dialogs.editmoment.EditMomentFragment
import cards.nine.app.ui.commons.dialogs.privatecollections.PrivateCollectionsFragment
import cards.nine.app.ui.commons.dialogs.publicollections.PublicCollectionsFragment
import cards.nine.app.ui.commons.dialogs.widgets.WidgetsFragment
import cards.nine.app.ui.launcher.jobs.LauncherJobs
import cards.nine.app.ui.preferences.NineCardsPreferencesActivity
import cards.nine.app.ui.preferences.commons.{
  CircleOpeningCollectionAnimation,
  CollectionOpeningAnimations
}
import cards.nine.app.ui.profile.ProfileActivity
import cards.nine.app.ui.wizard.WizardActivity
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.theme.CardLayoutBackgroundColor
import cards.nine.models.{Collection, Moment, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.DeviceVersion.KitKat
import macroid.extras.FragmentExtras._
import macroid.extras.ViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NavigationUiActions(val dom: LauncherDOM)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
    extends ImplicitsUiExceptions {

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit lazy val launcherJobs: LauncherJobs = createLauncherJobs

  implicit lazy val widgetsJobs = createWidgetsJobs

  implicit lazy val navigationJobs = createNavigationJobs

  implicit def theme: NineCardsTheme = statuses.theme

  val pageMoments = 0

  val pageCollections = 1

  val maxBackgroundPercent: Float = 0.7f

  val tagDialog = "dialog"

  def goToWizard(): TaskService[Unit] =
    uiStartIntent(new Intent(activityContextWrapper.bestAvailable, classOf[WizardActivity]))
      .toService()

  def goToCollection(collection: Collection, point: Point): TaskService[Unit] = {
    def rippleToCollection: Ui[Future[Any]] = {
      val color      = theme.getIndexColor(collection.themedColorIndex)
      val y          = KitKat.ifSupportedThen(point.y - systemBarsTint.getStatusBarHeight) getOrElse point.y
      val background = new RippleCollectionDrawable(point.x, y, color)
      (dom.foreground <~
        vVisible <~
        vBackground(background)) ~
        background.start()
    }

    val intent =
      new Intent(activityContextWrapper.bestAvailable, classOf[CollectionsDetailsActivity])
    intent.putExtra(CollectionsDetailsActivity.startPositionKey, collection.position)
    intent.putExtra(
      CollectionsDetailsActivity.toolbarColorKey,
      theme.getIndexColor(collection.themedColorIndex))
    intent.putExtra(
      CollectionsDetailsActivity.backgroundColorKey,
      theme.get(CardLayoutBackgroundColor))
    intent.putExtra(CollectionsDetailsActivity.toolbarIconKey, collection.icon)
    CollectionOpeningAnimations.readValue match {
      case anim @ CircleOpeningCollectionAnimation if anim.isSupported =>
        (rippleToCollection ~~ uiStartIntentForResult(intent, RequestCodes.goToCollectionDetails))
          .toService()
      case _ => uiStartIntent(intent).toService()
    }
  }

  def launchCreateOrCollection(bundle: Bundle): TaskService[Unit] =
    showAction(new CreateOrEditCollectionFragment, bundle).toService()

  def launchPrivateCollection(bundle: Bundle): TaskService[Unit] =
    showAction(new PrivateCollectionsFragment, bundle).toService()

  def launchPublicCollection(bundle: Bundle): TaskService[Unit] =
    showAction(new PublicCollectionsFragment, bundle).toService()

  def launchAddMoment(bundle: Bundle): TaskService[Unit] =
    showAction(new AddMomentFragment, bundle).toService()

  def launchEditMoment(bundle: Bundle): TaskService[Unit] =
    showAction(new EditMomentFragment, bundle).toService()

  def launchWidgets(bundle: Bundle): TaskService[Unit] =
    showAction(new WidgetsFragment, bundle).toService()

  def launchSettings(): TaskService[Unit] =
    uiStartIntentForResult(
      intent =
        new Intent(activityContextWrapper.getOriginal, classOf[NineCardsPreferencesActivity]),
      requestCode = goToPreferences).toService()

  def launchWallpaper(): TaskService[Unit] =
    uiStartIntent(new Intent(Intent.ACTION_SET_WALLPAPER)).toService()

  def deleteSelectedWidget(id: Int): TaskService[Unit] =
    Ui {
      val ft = fragmentManagerContext.manager.beginTransaction()
      Option(fragmentManagerContext.manager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(javaNull)
      val dialog = new AlertDialogFragment(
        message = R.string.removeWidgetMessage,
        positiveAction = () =>
          widgetsJobs
            .deleteWidget(id)
            .resolveAsyncServiceOr(_ => widgetsJobs.navigationUiActions.showContactUsError()))
      dialog.show(ft, tagDialog)
    }.toService()

  def showSelectMomentDialog(moments: Seq[Moment]): TaskService[Unit] =
    Ui {
      val momentDialog = new MomentDialog(moments)
      momentDialog.show(fragmentManagerContext.manager, tagDialog)
    }.toService()

  def showDialogForRemoveCollection(collection: Collection): TaskService[Unit] =
    Ui {
      val ft = fragmentManagerContext.manager.beginTransaction()
      Option(fragmentManagerContext.manager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(javaNull)
      val dialog = new AlertDialogFragment(
        message = R.string.removeCollectionMessage,
        positiveAction = () =>
          launcherJobs
            .removeCollection(collection)
            .resolveAsyncServiceOr(_ => launcherJobs.navigationUiActions.showContactUsError())
      )
      dialog.show(ft, tagDialog)
    }.toService()

  def showDialogForRemoveMoment(momentId: Int) =
    Ui {
      val ft = fragmentManagerContext.manager.beginTransaction()
      Option(fragmentManagerContext.manager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(javaNull)
      val dialog = new AlertDialogFragment(
        message = R.string.removeMomentMessage,
        positiveAction = () =>
          launcherJobs
            .removeMoment(momentId)
            .resolveAsyncServiceOr(_ => launcherJobs.navigationUiActions.showContactUsError())
      )
      dialog.show(ft, tagDialog)
    }.toService()

  def showAddItemMessage(nameCollection: String): TaskService[Unit] =
    showMessage(R.string.itemAddedToCollectionSuccessful, Seq(nameCollection)).toService()

  def showWidgetCantResizeMessage(): TaskService[Unit] =
    showMessage(R.string.noResizeForWidget).toService()

  def showWidgetCantMoveMessage(): TaskService[Unit] =
    showMessage(R.string.noMoveForWidget).toService()

  def showCantRemoveOutAndAboutMessage(): TaskService[Unit] =
    showMessage(R.string.cantRemoveOutAndAboutMoment).toService()

  def showWidgetNoHaveSpaceMessage(): TaskService[Unit] =
    showMessage(R.string.noSpaceForWidget).toService()

  def showContactUsError(): TaskService[Unit] =
    showMessage(R.string.contactUsError).toService()

  def showMinimumOneCollectionMessage(): TaskService[Unit] =
    showMessage(R.string.minimumOneCollectionMessage).toService()

  def showNoPhoneCallPermissionError(): TaskService[Unit] =
    showMessage(R.string.noPhoneCallPermissionMessage).toService()

  def showContactPermissionError(action: () => Unit): TaskService[Unit] =
    showMessageWithAction(R.string.errorContactsPermission, R.string.buttonTryAgain, action)
      .toService()

  def showCallPermissionError(action: () => Unit): TaskService[Unit] =
    showMessageWithAction(R.string.errorCallsPermission, R.string.buttonTryAgain, action)
      .toService()

  def removeActionFragment(): TaskService[Unit] =
    dom.getFragment match {
      case Some(fragment) => TaskService.right(removeFragment(fragment))
      case _              => TaskService.empty
    }

  def unrevealActionFragment: TaskService[Unit] =
    dom.getFragment match {
      case Some(fragment) => fragment.unreveal().toService()
      case _              => TaskService.empty
    }

  def goToProfile(): TaskService[Unit] =
    uiStartIntentForResult(
      new Intent(activityContextWrapper.bestAvailable, classOf[ProfileActivity]),
      RequestCodes.goToProfile).toService()

  def goToMomentWorkspace(): TaskService[Unit] = goToWorkspace(pageMoments)

  def goToCollectionWorkspace(): TaskService[Unit] =
    goToWorkspace(pageCollections)

  def goToWorkspace(page: Int): TaskService[Unit] =
    ((dom.getData.lift(page) map (data =>
                                    dom.topBarPanel <~ tblReloadByType(data.workSpaceType)) getOrElse Ui.nop) ~
      (dom.workspaces <~ lwsSelect(page)) ~
      (dom.paginationPanel <~ ivReloadPager(page))).toService()

  private[this] def showMessage(res: Int, args: Seq[String] = Seq.empty): Ui[Any] =
    dom.workspaces <~ vLauncherSnackbar(res, args)

  private[this] def showMessageWithAction(
      resMessage: Int,
      resButton: Int,
      action: () => Unit): Ui[Any] =
    dom.workspaces <~ vLauncherSnackbarWithAction(
      resMessage,
      resButton,
      action,
      length = Snackbar.LENGTH_LONG)

  private[this] def showAction[F <: DialogFragment](fragment: F, bundle: Bundle): Ui[Any] = {
    closeCollectionMenu() ~~
      Ui {
        fragment.setArguments(bundle)
        fragment.show(fragmentManagerContext.manager, tagDialog)
      }
  }

  private[this] def closeCollectionMenu(): Ui[Future[Any]] =
    dom.workspaces <~~ lwsCloseMenu

}
