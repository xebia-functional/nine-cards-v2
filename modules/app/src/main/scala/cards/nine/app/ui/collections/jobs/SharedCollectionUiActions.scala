package com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs

import android.support.v4.app.{Fragment, FragmentManager}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImplicitsUiExceptions, UiContext, UiException}
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

class SharedCollectionUiActions(dom: GroupCollectionsDOM with GroupCollectionsUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  def reloadSharedCollectionId(sharedCollectionId: Option[String]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
      currentPosition <- adapter.getCurrentFragmentPosition
      _ = adapter.updateShareCollectionIdFromCollection(currentPosition, sharedCollectionId)
    } yield dom.invalidateOptionMenu
  }.toService

  def showPublishCollectionWizardDialog(collection: Collection): TaskService[Unit]  =
    Ui(dom.showPublicCollectionDialog(collection)).toService

  def showMessagePublishContactsCollectionError: TaskService[Unit] = showError(R.string.publishCollectionError).toService

  def showMessageNotPublishedCollectionError: TaskService[Unit] = showError(R.string.notPublishedCollectionError).toService

  def getCurrentCollection: TaskService[Option[Collection]] = TaskService {
    CatchAll[UiException](dom.getCurrentCollection)
  }

  private[this] def showError(error: Int = R.string.contactUsError): Ui[Any] = dom.root <~ vSnackbarShort(error)

}
