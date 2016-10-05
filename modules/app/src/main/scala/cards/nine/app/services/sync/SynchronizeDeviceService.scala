package cards.nine.app.services.sync

import android.app.{IntentService, Service}
import android.content.Intent
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import cards.nine.app.ui.commons.AppLog
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.sharedcollections.SharedCollectionsConfigurationException
import com.google.android.gms.common.ConnectionResult
import macroid.Contexts

class SynchronizeDeviceService
  extends IntentService("synchronizeDeviceService")
  with Contexts[Service]
  with ContextSupportProvider
  with BroadcastDispatcher
  with SynchronizeDeviceUiActions
  with SynchronizeDeviceListener
  with CloudStorageClientListener {

  lazy val jobs = new SynchronizeDeviceJobs(this)

  val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageQuestion(action: String): Unit = SyncActionFilter(action) match {
    case SyncAskActionFilter => jobs.sendActualState.resolveAsync()
    case _ =>
  }


  override def onHandleIntent(intent: Intent): Unit = {
    registerDispatchers()

    jobs.startSync().resolveAsync2(onException = (e: Throwable) => e match {
      case e: SharedCollectionsConfigurationException => AppLog.invalidConfigurationV2
      case _ =>
    })
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher()
  }

  override def processFinished(): Unit = {
    stopForeground(true)
    stopSelf()
  }

  override def onDriveConnectionSuspended(cause: Int): Unit = {}

  override def onDriveConnected(): Unit =
    jobs.syncDevice().resolveAsyncServiceOr(_ => jobs.closeServiceWithError())

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    jobs.closeServiceWithError().resolveAsync()

}