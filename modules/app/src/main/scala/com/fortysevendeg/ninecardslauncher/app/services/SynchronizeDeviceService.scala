package com.fortysevendeg.ninecardslauncher.app.services

import android.app.Service
import android.content.Intent
import android.os.{Build, Bundle, IBinder}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.{CloudStorageProcessException, Conversions}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts
import rapture.core.scalazInterop.ResultT

import scalaz.concurrent.Task

class SynchronizeDeviceService
  extends Service
  with Contexts[Service]
  with GoogleApiClientProvider {

  import Conversions._

  implicit lazy val di = new Injector

  private[this] var statuses = GoogleApiClientStatuses()

  override def onBind(intent: Intent): IBinder = ???

  override def tryToConnect(): Unit = statuses.apiClient foreach (_.connect())

  override def onResolveConnectionError(): Unit = ???

  override def onRequestConnectionError(errorCode: Int): Unit = ???

  override def onConnected(bundle: Bundle): Unit =
    statuses match {
      case GoogleApiClientStatuses(Some(client), Some(account)) if client.isConnected =>
        sync(client, account)
      case GoogleApiClientStatuses(Some(client), Some(account)) =>
        tryToConnect()
      case _ =>
        // TODO
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
}

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None)