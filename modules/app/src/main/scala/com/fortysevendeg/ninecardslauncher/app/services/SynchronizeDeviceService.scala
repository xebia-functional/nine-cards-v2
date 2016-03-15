package com.fortysevendeg.ninecardslauncher.app.services

import android.app.Service
import android.content.Intent
import android.os.{Bundle, IBinder}
import com.fortysevendeg.ninecardslauncher.app.commons.BroadcastDispatcher
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SyncDeviceState
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.{CloudStorageProcessException, Conversions}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts

import scalaz.concurrent.Task

class SynchronizeDeviceService
  extends Service
  with Contexts[Service]
  with GoogleApiClientProvider
  with BroadcastDispatcher {

  self =>

  import AppLog._
  import Conversions._
  import TasksOps._
  import SyncDeviceState._

  implicit lazy val di = new Injector

  private[this] var statuses = GoogleApiClientStatuses()

  private var currentState: Option[String] = None

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    registerDispatchers

    synchronizeDevice()

    super.onStartCommand(intent, flags, startId)
  }

  override def onBind(intent: Intent): IBinder = javaNull

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  override def tryToConnect(): Unit = statuses.apiClient foreach (_.connect())

  override def onResolveConnectionError(): Unit =
    error(getString(R.string.errorConnectingGoogle))

  override def onRequestConnectionError(errorCode: Int): Unit =
    error(getString(R.string.errorConnectingGoogle))

  override def onConnected(bundle: Bundle): Unit =
    statuses match {
      case GoogleApiClientStatuses(Some(client), Some(account)) if client.isConnected =>
        Task.fork(sync(client, account).run).resolveAsync(
          _ => success(),
          throwable => {
            error(
              message = getString(R.string.errorConnectingGoogle),
              maybeException = Some(throwable))
          })
      case GoogleApiClientStatuses(Some(client), Some(account)) =>
        tryToConnect()
      case _ =>
        error(getString(R.string.errorConnectingGoogle))
    }

  override val actionsFilters: Seq[String] = SyncActionFilter.cases map (_.action)

  override def manageQuestion(action: String): Option[BroadAction] = SyncActionFilter(action) match {
    case SyncAskActionFilter => Option(BroadAction(SyncAnswerActionFilter.action, currentState))
    case _ => None
  }

  def synchronizeDevice(): Unit = {
    Task.fork(di.userProcess.getUser.run).resolveAsync(
      user => user.email match {
        case Some(account) =>
          val client = createGoogleDriveClient(account)
          statuses = statuses.copy(
            apiClient = Some(client),
            username = Some(account))
          client.connect()
        case _ =>
          error(getString(R.string.errorLoadingUser))
      },
      throwable => {
        error(
          message = getString(R.string.errorLoadingUser),
          maybeException = Some(throwable))
      })
  }

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

  private[this] def error(message: String, maybeException: Option[Throwable] = None) = {
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