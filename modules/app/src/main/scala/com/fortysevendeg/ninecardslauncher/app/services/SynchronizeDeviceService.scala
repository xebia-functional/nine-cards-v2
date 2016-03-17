package com.fortysevendeg.ninecardslauncher.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.commons.GoogleApiClientService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SyncDeviceState
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import macroid.Contexts

import scalaz.concurrent.Task

class SynchronizeDeviceService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with GoogleApiClientService
  with BroadcastDispatcher { self =>

  import SyncDeviceState._

  implicit lazy val di = new Injector

  private var currentState: Option[String] = None

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    registerDispatchers

    synchronizeDevice

    super.onStartCommand(intent, flags, startId)
  }

  override def onBind(intent: Intent): IBinder = javaNull

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  override val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageQuestion(action: String): Option[BroadAction] = SyncActionFilter(action) match {
    case SyncAskActionFilter => Option(BroadAction(SyncAnswerActionFilter.action, currentState))
    case _ => None
  }

  override def connected(client: GoogleApiClient, account: String): Unit =
    Task.fork(sync(client, account).run).resolveAsync(
      _ => success(),
      throwable => {
        error(
          message = getString(R.string.errorConnectingGoogle),
          maybeException = Some(throwable))
      })

  private[this] def sync(
    client: GoogleApiClient,
    account: String): ServiceDef2[Unit, CollectionException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client, account)
    for {
      collections <- di.collectionProcess.getCollections
      _ <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
        collections = collections map toCloudStorageCollection)
    } yield ()
  }

  private[this] def success() = sendStateAndFinish(stateSuccess)

  override def error(message: String, maybeException: Option[Throwable] = None) = {
    maybeException foreach (ex => printErrorMessage(ex))
    sendStateAndFinish(stateFailure)
  }

  private[this] def sendStateAndFinish(state: String) = {
    currentState = Option(state)
    self ! BroadAction(SyncStateActionFilter.action, currentState)
    closeService()
  }


  private[this] def closeService() = {
    stopForeground(true)
    stopSelf()
  }
}

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None)