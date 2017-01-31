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

package cards.nine.app.services.sync

import android.app.{IntentService, PendingIntent, Service}
import android.content.Intent
import cards.nine.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import cards.nine.app.services.sync.SynchronizeDeviceService._
import cards.nine.app.ui.commons.{AppLog, SynchronizeDeviceJobs}
import cards.nine.app.ui.commons.SyncDeviceState._
import cards.nine.app.ui.commons.action_filters._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.cloud.CloudStorageClientListener
import cards.nine.process.sharedcollections.SharedCollectionsConfigurationException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts

class SynchronizeDeviceService
    extends IntentService("synchronizeDeviceService")
    with Contexts[Service]
    with ContextSupportProvider
    with BroadcastDispatcher
    with CloudStorageClientListener {

  lazy val serviceJobs = new SynchronizeDeviceServiceJobs

  lazy val syncJobs = new SynchronizeDeviceJobs

  val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageQuestion(action: String): Unit =
    SyncActionFilter(action) match {
      case SyncAskActionFilter => serviceJobs.sendActualAnswer().resolveAsync()
      case _                   =>
    }

  override def onHandleIntent(intent: Intent): Unit = {
    registerDispatchers()

    statuses = statuses.reset()

    (for {
      _ <- TaskService.right(statuses = statuses.copy(currentState = Option(stateSyncing)))
      _ <- serviceJobs.sendActualState()
      _ <- serviceJobs.synchronizeCollections()
      _ <- serviceJobs.connectGoogleApiClient()
    } yield ()).resolveAsyncService(onException = onException)
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    statuses = statuses.reset()
    unregisterDispatcher()
  }

  override def onDriveConnectionSuspended(cause: Int): Unit = {}

  override def onDriveConnected(): Unit = {
    (statuses.apiClient match {
      case Some(apiClient) =>
        for {
          _ <- serviceJobs.cancelAlarm().resolveLeftTo((): Unit)
          _ <- syncJobs.synchronizeDevice(apiClient)
          _ <- TaskService.right(statuses = statuses.copy(currentState = Some(stateSuccess)))
          _ <- finalizeService(error = false)
        } yield ()
      case None => serviceJobs.connectGoogleApiClient()
    }).resolveAsyncService(onException = onException)
  }

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    finalizeService(error = true).resolveAsync()

  private[this] def finalizeService(error: Boolean): TaskService[Unit] = {
    val state = if (error) stateFailure else stateSuccess
    for {
      _ <- TaskService.right(statuses = statuses.copy(currentState = Some(state)))
      _ <- serviceJobs.sendActualState()
      _ <- serviceJobs.finalizeService(this)
    } yield ()
  }

  private[this] def onException: (Throwable) => TaskService[Unit] = {
    case e: SharedCollectionsConfigurationException =>
      AppLog.invalidConfigurationV2
      finalizeService(error = true)
    case _ => finalizeService(error = true)
  }
}

object SynchronizeDeviceService {

  val requestCode = 5001

  var statuses = SynchronizeDeviceServiceStatuses()

  def pendingIntent(implicit contextSupport: ContextSupport): PendingIntent =
    PendingIntent.getService(
      contextSupport.context,
      requestCode,
      new Intent(contextSupport.context, classOf[SynchronizeDeviceService]),
      PendingIntent.FLAG_CANCEL_CURRENT)

}

case class SynchronizeDeviceServiceStatuses(
    currentState: Option[String] = None,
    apiClient: Option[GoogleApiClient] = None) {

  def reset() = SynchronizeDeviceServiceStatuses()

}
