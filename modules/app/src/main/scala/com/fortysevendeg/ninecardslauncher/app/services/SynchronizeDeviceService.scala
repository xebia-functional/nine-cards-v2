package com.fortysevendeg.ninecardslauncher.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.services.commons.FirebaseExtensions._
import com.fortysevendeg.ninecardslauncher.app.services.commons.GoogleDriveApiClientService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SyncDeviceState
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment}
import com.fortysevendeg.ninecardslauncher.process.moment.MomentException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts

import scalaz.concurrent.Task

class SynchronizeDeviceService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with GoogleDriveApiClientService
  with BroadcastDispatcher { self =>

  import SyncDeviceState._

  implicit lazy val di = new InjectorImpl

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

  override def connected(client: GoogleApiClient): Unit = {

    def sync(
      client: GoogleApiClient): ServiceDef2[Unit, CollectionException with MomentException with CloudStorageProcessException with UserException] = {
      val cloudStorageProcess = di.createCloudStorageProcess(client)
      for {
        collections <- di.collectionProcess.getCollections
        moments <- di.momentProcess.getMoments
        savedDevice <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          collections = collections map toCloudStorageCollection,
          moments = moments.filter(_.collectionId.isEmpty) map toCloudStorageMoment)
        _ <- di.userProcess.updateUserDevice(savedDevice.data.deviceName, savedDevice.cloudId)
      } yield ()
    }

    Task.fork(sync(client).run).resolveAsync(
      _ => sendStateAndFinish(stateSuccess),
      throwable => {
        error(
          message = getString(R.string.errorConnectingGoogle),
          maybeException = Some(throwable))
      })
  }

  override def error(message: String, maybeException: Option[Throwable] = None) = {
    maybeException foreach (ex => printErrorMessage(ex))
    sendStateAndFinish(stateFailure)
  }

  private[this] def sendStateAndFinish(state: String) = {

    def closeService() = {
      stopForeground(true)
      stopSelf()
    }

    currentState = Option(state)
    self ! BroadAction(SyncStateActionFilter.action, currentState)
    closeService()
  }

}