package cards.nine.app.ui.profile

import android.app.Activity
import android.content.{BroadcastReceiver, Context, Intent, IntentFilter}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.{Menu, MenuItem}
import cards.nine.app.commons.BroadcastDispatcher._
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.profile.models.{AccountsTab, ProfileTab, PublicationsTab, SubscriptionsTab}
import cards.nine.commons.services.TaskService._
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.sharedcollections.SharedCollectionsConfigurationException
import cards.nine.process.sharedcollections.models.SharedCollection
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.{R, TypedFindView}
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts

class ProfileActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ProfileDOM
  with ProfileListener
  with CloudStorageClientListener {

  self =>

  import SyncDeviceState._
  import ProfileActivity._

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  lazy val actions = new ProfileUiActions(self)

  lazy val jobs = new ProfileJobs(actions)

  override def actionBar = Option(getSupportActionBar)

  val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  lazy val broadcast = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit = Option(intent) map { i =>
      (Option(i.getAction), Option(i.getStringExtra(keyType)), Option(i.getStringExtra(keyCommand)))
    } match {
      case Some((Some(action: String), Some(key: String), data)) if key == commandType => manageCommand(action, data)
      case _ =>
    }
  }

  def registerDispatchers() = {
    val intentFilter = new IntentFilter()
    actionsFilters foreach intentFilter.addAction
    registerReceiver(broadcast, intentFilter)
  }

  def unregisterDispatcher() = unregisterReceiver(broadcast)

  def manageCommand(action: String, data: Option[String]): Unit = (SyncActionFilter(action), data) match {
    case (SyncStateActionFilter, Some(`stateSuccess`)) =>
      jobs.accountSynced().resolveAsyncServiceOr(_ => actions.showEmptyAccountsContent(error = true))
    case (SyncStateActionFilter, Some(`stateFailure`)) =>
      jobs.errorSyncing().resolveAsyncServiceOr(_ => actions.showContactUsError())
    case (SyncAnswerActionFilter, Some(`stateSyncing`)) =>
      jobs.stateSyncing().resolveAsyncServiceOr(_ => actions.showContactUsError())
    case _ =>
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    setContentView(R.layout.profile_activity)

    statuses = statuses.reset()

    setSupportActionBar(toolbar)

    jobs.initialize().resolveAsyncServiceOr(_ => actions.showEmptyAccountsContent(error = true))
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
      jobs.quit().resolveAsyncServiceOr(_ => actions.showContactUsError())
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
      actions.showErrorSubscribing(triedToSubscribe = newSubscribeStatus) *>
        actions.refreshCurrentSubscriptions()
    }

  override def onClickCopyDevice(cloudId: String, actualName: String): Unit =
    actions.showDialogForCopyDevice(cloudId, actualName).resolveAsync()

  override def onClickRenameDevice(cloudId: String, actualName: String): Unit =
    actions.showDialogForRenameDevice(cloudId, actualName).resolveAsync()

  override def onClickDeleteDevice(cloudId: String): Unit =
    actions.showDialogForDeleteDevice(cloudId).resolveAsync()

  override def onClickPrintInfoDevice(cloudId: String): Unit =
    jobs.printDeviceInfo(cloudId).resolveAsyncServiceOr(_ => actions.showContactUsError())

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
    jobs.driveConnected().resolveAsyncServiceOr(_ => actions.showEmptyAccountsContent(error = true))

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    jobs.driveConnectionFailed(connectionResult).resolveAsyncServiceOr(_ => actions.showContactUsError())

  override def onClickAddSharedCollection(collection: SharedCollection): Unit =
    jobs.saveSharedCollection(collection).resolveAsyncServiceOr(_ => actions.showErrorSavingCollectionInScreen())

  override def onClickShareSharedCollection(collection: SharedCollection): Unit =
    jobs.shareCollection(collection).resolveAsyncServiceOr(_ => actions.showContactUsError())

  private[this] def loadTab(tab: ProfileTab, error: Boolean = false): Unit = {

    def withError(service: TaskService[Unit]): TaskService[Unit] = if (error) {
      for {
        _ <- actions.showContactUsError()
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
      case AccountsTab => withError(jobs.loadUserAccounts())
        .resolveAsyncServiceOr(_ => actions.showEmptyAccountsContent(error = true))
      case PublicationsTab => withError(jobs.loadPublications())
        .resolveAsyncServiceOr(onException(actions.showEmptyPublicationsContent(error = true)))
      case SubscriptionsTab => withError(jobs.loadSubscriptions())
        .resolveAsyncServiceOr(onException(actions.showEmptySubscriptionsContent(error = true)))
    }
  }
}

object ProfileActivity {

  var statuses = ProfileStatuses()

}

case class ProfileStatuses(apiClient: Option[GoogleApiClient] = None) {

  def reset() = ProfileStatuses()

}
