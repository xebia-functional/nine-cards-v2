package cards.nine.app.ui.share

import android.support.v4.app.{Fragment, FragmentManager}
import macroid.extras.UIActionsExtras._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.dialogs.CollectionDialog
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Collection
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ActivityContextWrapper, FragmentManagerContext, Ui}
import SharedContentActivity._
import cards.nine.models.NineCardsTheme

class SharedContentUiActions
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  lazy val sharedContentJob: SharedContentJobs = createSharedContentJob

  implicit def theme: NineCardsTheme = statuses.theme

  def showChooseCollection(collections: Seq[Collection]): TaskService[Unit] =
    Ui(new CollectionDialog(
      moments = collections,
      onCollection = (collectionId) => {
        sharedContentJob.collectionChosen(collectionId).resolveAsyncServiceOr(_ => showUnexpectedError())
      },
      onDismissDialog = () => {
        sharedContentJob.dialogDismissed().resolveAsync()
      }).show()).toService

  def showSuccess(): TaskService[Unit] =
    (uiShortToast(R.string.sharedCardAdded) ~ finishUi()).toService

  def showErrorEmptyContent(): TaskService[Unit] =
    (uiShortToast(R.string.sharedContentErrorEmpty) ~ finishUi()).toService

  def showErrorContentNotSupported(): TaskService[Unit] =
    (uiLongToast(R.string.sharedContentErrorNotSupported) ~ finishUi()).toService

  def showUnexpectedError(): TaskService[Unit] =
    (uiShortToast(R.string.sharedContentErrorUnexpected) ~ finishUi()).toService

  def close(): TaskService[Unit] = finishUi().toService

  def finishUi(): Ui[Any] = Ui(activityContextWrapper.getOriginal.finish())

}
