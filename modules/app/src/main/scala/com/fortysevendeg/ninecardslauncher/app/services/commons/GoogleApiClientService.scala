package com.fortysevendeg.ninecardslauncher.app.services.commons

import android.app.Service
import android.os.Bundle
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.{ConnectionSuspendedCause, GoogleDriveApiClientProvider}
import cards.nine.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts


trait GoogleDriveApiClientService
  extends GoogleDriveApiClientProvider { // TODO - Use the CloudStorageProcess.createCloudStorageClient in Jobs

  self: Service with Contexts[Service] =>

  private[this] var statuses = GoogleApiClientStatuses()

  private[this] def tryToConnect(): Unit = statuses.apiClient foreach (_.connect())

  def connected(client: GoogleApiClient): Unit

  def error(message: String, maybeException: Option[Throwable] = None): Unit

  override def onDriveConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit = {}

  override def onDriveConnected(bundle: Bundle): Unit =
    statuses match {
      case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
        connected(client)
      case GoogleApiClientStatuses(Some(client)) =>
        tryToConnect()
      case _ =>
        error(resGetString(R.string.errorConnectingGoogle))
    }

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    error(resGetString(R.string.errorConnectingGoogle))

  def synchronizeDevice(implicit di: Injector, contextSupport: ContextSupport): Unit = {
    di.userProcess.getUser.resolveAsync2(
      user => user.email match {
        case Some(account) =>
          val client = createGoogleDriveClient(account)
          statuses = statuses.copy(apiClient = Some(client))
          client.connect()
        case _ =>
          error(resGetString(R.string.errorLoadingUser))
      },
      throwable => {
        error(
          message = resGetString(R.string.errorLoadingUser),
          maybeException = Some(throwable))
      })
  }

}

case class GoogleApiClientStatuses(apiClient: Option[GoogleApiClient] = None)
