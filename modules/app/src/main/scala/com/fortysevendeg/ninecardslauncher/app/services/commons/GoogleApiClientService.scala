package com.fortysevendeg.ninecardslauncher.app.services.commons

import android.app.Service
import android.os.Bundle
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts

import scalaz.concurrent.Task

trait GoogleApiClientService
  extends GoogleApiClientProvider {

  self: Service with Contexts[Service] =>

  private[this] var statuses = GoogleApiClientStatuses()

  private[this] def tryToConnect(): Unit = statuses.apiClient foreach (_.connect())

  def connected(client: GoogleApiClient): Unit

  def error(message: String, maybeException: Option[Throwable] = None): Unit

  override def onConnected(bundle: Bundle): Unit =
    statuses match {
      case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
        connected(client)
      case GoogleApiClientStatuses(Some(client)) =>
        tryToConnect()
      case _ =>
        error(resGetString(R.string.errorConnectingGoogle))
    }

  override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
    error(resGetString(R.string.errorConnectingGoogle))

  def synchronizeDevice(implicit di: Injector, contextSupport: ContextSupport): Unit = {
    Task.fork(di.userProcess.getUser.run).resolveAsync(
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
