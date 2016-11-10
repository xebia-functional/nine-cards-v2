package cards.nine.app.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.{Menu, MenuItem}
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.profile.jobs.{ProfileDOM, ProfileJobs, ProfileListener, ProfileUiActions}
import cards.nine.app.ui.profile.models.{AccountsTab, ProfileTab, PublicationsTab, SubscriptionsTab}
import cards.nine.commons.services.TaskService._
import cards.nine.models.SharedCollection
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.sharedcollections.SharedCollectionsConfigurationException
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, Contexts, FragmentManagerContext}
import monix.execution.cancelables.SerialCancelable

class ProfileActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ProfileListener
  with CloudStorageClientListener
  with BroadcastDispatcher {

  import ProfileActivity._
  import SyncDeviceState._

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  lazy val jobs = createProfileJobs

  val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (SyncActionFilter(action), data) match {
    case (SyncStateActionFilter, Some(`stateSuccess`)) =>
      jobs.accountSynced().resolveAsyncServiceOr(_ => jobs.profileUiActions.showEmptyAccountsContent(error = true))
    case (SyncStateActionFilter, Some(`stateFailure`)) =>
      jobs.errorSyncing().resolveAsyncServiceOr(_ => jobs.profileUiActions.showContactUsError())
    case (SyncAnswerActionFilter, Some(`stateSyncing`)) =>
      jobs.stateSyncing().resolveAsyncServiceOr(_ => jobs.profileUiActions.showContactUsError())
    case _ =>
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.profile_activity)
    statuses = statuses.reset()
    jobs.initialize().resolveAsyncServiceOr(_ => jobs.profileUiActions.showEmptyAccountsContent(error = true))
  }

  override def onResume(): Unit = {
    super.onResume()
    registerDispatchers()
    jobs.resume().resolveAsync()
  }

  override def onPause(): Unit = {
    super.onPause()
    unregisterDispatcher()
  }

  override def onStop(): Unit = {
    jobs.stop().resolveAsync()
    super.onStop()
  }

  override def onDestroy(): Unit = {
    statuses = statuses.reset()
    super.onDestroy()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.profile_menu, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      finish()
      true
    case R.id.action_logout =>
      jobs.quit().resolveAsyncServiceOr(_ => jobs.profileUiActions.showContactUsError())
      true
    case _ =>
      super.onOptionsItemSelected(item)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
   jobs.activityResult(requestCode, resultCode, data).resolveAsync()

  override def onBarLayoutOffsetChanged(maxScroll: Float, offset: Int): Unit = {
    val percentage = Math.abs(offset) / maxScroll
    jobs.onOffsetChanged(percentage).resolveAsync()
  }

  override def onClickProfileTab(tab: ProfileTab): Unit = loadTab(tab)

  override def onClickReloadTab(tab: ProfileTab): Unit = loadTab(tab)

  override def onClickSynchronizeDevice(): Unit =
    jobs.launchService().resolveAsync()

  override def onClickSubscribeCollection(sharedCollectionId: String, newSubscribeStatus: Boolean): Unit =
    jobs.changeSubscriptionStatus(sharedCollectionId, newSubscribeStatus).resolveAsyncServiceOr { _ =>
      jobs.profileUiActions.showErrorSubscribing(triedToSubscribe = newSubscribeStatus) *>
        jobs.profileUiActions.refreshCurrentSubscriptions()
    }

  override def onClickCopyDevice(cloudId: String, actualName: String): Unit =
    jobs.profileUiActions.showDialogForCopyDevice(cloudId, actualName).resolveAsync()

  override def onClickRenameDevice(cloudId: String, actualName: String): Unit =
    jobs.profileUiActions.showDialogForRenameDevice(cloudId, actualName).resolveAsync()

  override def onClickDeleteDevice(cloudId: String): Unit =
    jobs.profileUiActions.showDialogForDeleteDevice(cloudId).resolveAsync()

  override def onClickPrintInfoDevice(cloudId: String): Unit =
    jobs.printDeviceInfo(cloudId).resolveAsyncServiceOr(_ => jobs.profileUiActions.showContactUsError())

  override def onClickOkRemoveDeviceDialog(cloudId: String): Unit =
    jobs.deleteDevice(cloudId)
      .resolveAsync(onException = _ => loadTab(AccountsTab, error = true))

  override def onClickOkRenameDeviceDialog(maybeName: Option[String], cloudId: String, actualName: String): Unit =
    jobs.renameDevice(maybeName, cloudId, actualName)
      .resolveAsync(onException = _ => loadTab(AccountsTab, error = true))

  override def onClickOkOnCopyDeviceDialog(maybeName: Option[String], cloudId: String, actualName: String): Unit =
    jobs.copyDevice(maybeName, cloudId, actualName)
      .resolveAsync(onException = _ => loadTab(AccountsTab, error = true))

  override def onDriveConnectionSuspended(cause: Int): Unit = {}

  override def onDriveConnected(): Unit =
    jobs.driveConnected().resolveAsyncServiceOr(_ => jobs.profileUiActions.showEmptyAccountsContent(error = true))

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    jobs.driveConnectionFailed(connectionResult).resolveAsyncServiceOr(_ => jobs.profileUiActions.showContactUsError())

  override def onClickAddSharedCollection(collection: SharedCollection): Unit =
    jobs.saveSharedCollection(collection).resolveAsyncServiceOr(_ => jobs.profileUiActions.showErrorSavingCollectionInScreen())

  override def onClickShareSharedCollection(collection: SharedCollection): Unit =
    jobs.shareCollection(collection).resolveAsyncServiceOr(_ => jobs.profileUiActions.showContactUsError())

  private[this] def loadTab(tab: ProfileTab, error: Boolean = false): Unit = {

    def withError(service: TaskService[Unit]): TaskService[Unit] = if (error) {
      for {
        _ <- jobs.profileUiActions.showContactUsError()
        _ <- service
      } yield ()
    } else service

    def onException(service: TaskService[Unit]): (Throwable) => TaskService[Unit] = {
      case e: SharedCollectionsConfigurationException =>
        AppLog.invalidConfigurationV2
        service
      case _ => service
    }

    tab match {
      case AccountsTab =>
        serialCancelableTaskRef := withError(jobs.loadUserAccounts())
          .resolveAsyncServiceOr(_ => jobs.profileUiActions.showEmptyAccountsContent(error = true))
      case PublicationsTab =>
        serialCancelableTaskRef := withError(jobs.loadPublications())
          .resolveAsyncServiceOr(onException(jobs.profileUiActions.showEmptyPublicationsContent(error = true)))
      case SubscriptionsTab =>
        serialCancelableTaskRef := withError(jobs.loadSubscriptions())
          .resolveAsyncServiceOr(onException(jobs.profileUiActions.showEmptySubscriptionsContent(error = true)))
    }
  }
}

object ProfileActivity {

  var statuses = ProfileStatuses()

  val serialCancelableTaskRef = SerialCancelable()

  def createProfileJobs(implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) = {
    val dom = new ProfileDOM(activityContextWrapper.getOriginal)
    val listener = activityContextWrapper.getOriginal.asInstanceOf[ProfileListener]
    new ProfileJobs(new ProfileUiActions(dom, listener))
  }

}

case class ProfileStatuses(apiClient: Option[GoogleApiClient] = None) {

  def reset() = ProfileStatuses()

}
