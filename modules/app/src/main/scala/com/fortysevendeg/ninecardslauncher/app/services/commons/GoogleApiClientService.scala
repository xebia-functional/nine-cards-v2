package com.fortysevendeg.ninecardslauncher.app.services.commons

import android.app.Service
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient

import scalaz.concurrent.Task

trait GoogleApiClientService
  extends GoogleApiClientProvider{

  self : Service =>

  private[this] var statuses = GoogleApiClientStatuses()

  override def tryToConnect(): Unit = statuses.apiClient foreach (_.connect())

  override def onResolveConnectionError(): Unit =
    error(getString(R.string.errorConnectingGoogle))

  override def onRequestConnectionError(errorCode: Int): Unit =
    error(getString(R.string.errorConnectingGoogle))

  def connected(client: GoogleApiClient, account: String): Unit

  def error(message: String, maybeException: Option[Throwable] = None): Unit

  override def onConnected(bundle: Bundle): Unit =
    statuses match {
      case GoogleApiClientStatuses(Some(client), Some(account)) if client.isConnected =>
        connected(client, account)
      case GoogleApiClientStatuses(Some(client), Some(account)) =>
        tryToConnect()
      case _ =>
        error(getString(R.string.errorConnectingGoogle))
    }

  def synchronizeDevice(implicit di: Injector, contextSupport: ContextSupport): Unit = {
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

}

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None)
