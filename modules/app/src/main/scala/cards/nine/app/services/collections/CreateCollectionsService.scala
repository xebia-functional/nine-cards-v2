package cards.nine.app.services.collections

import android.app.{Notification, Service}
import android.content.{BroadcastReceiver, Context, Intent, IntentFilter}
import android.os.IBinder
import cards.nine.app.commons.BroadcastDispatcher._
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.SyncDeviceState.{stateFailure => _, stateSuccess => _}
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.javaNull
import cards.nine.process.cloud.CloudStorageClientListener
import com.google.android.gms.common.ConnectionResult
import macroid.Contexts

class CreateCollectionsService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with CreateCollectionsUiActions
  with CreateCollectionsListener
  with CloudStorageClientListener {

  lazy val jobs = new CreateCollectionsJobs(this)

  val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  lazy val broadcast = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit = Option(intent) map { i =>
      (Option(i.getAction), Option(i.getStringExtra(keyType)), Option(i.getStringExtra(keyCommand)))
    } match {
      case Some((Some(action: String), Some(key: String), _)) if key == questionType =>
        jobs.sendActualState.resolveAsync()
      case _ =>
    }
  }

  def registerDispatchers() = {
    val intentFilter = new IntentFilter()
    actionsFilters foreach intentFilter.addAction
    registerReceiver(broadcast, intentFilter)
  }

  def unregisterDispatcher() = unregisterReceiver(broadcast)

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {

    registerDispatchers()

    jobs.startCommand(intent).resolveAsyncServiceOr(_ => jobs.closeServiceWithError())

    Service.START_NOT_STICKY
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher()
  }

  override def processStarted(notificationId: Int, notification: Notification): Unit =
    startForeground(notificationId, notification)

  override def processFinished(): Unit = {
    stopForeground(true)
    stopSelf()
  }

  override def onBind(intent: Intent): IBinder = javaNull

  override def onDriveConnectionSuspended(cause: Int): Unit = {}

  override def onDriveConnected(): Unit =
    jobs.createConfiguration().resolveAsyncServiceOr(_ => jobs.closeServiceWithError())

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    jobs.closeServiceWithError().resolveAsync()
}

object CreateCollectionsService {
  val cloudIdKey: String = "__key_device__"
  val newConfiguration: String = "new-configuration"
}