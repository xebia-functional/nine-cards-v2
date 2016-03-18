package com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive._

trait GoogleApiClientProvider
  extends GoogleApiClient.ConnectionCallbacks
  with GoogleApiClient.OnConnectionFailedListener {

  self: Context =>

  def createGoogleDriveClient(account: String): GoogleApiClient = {
    val apiClient = new GoogleApiClient.Builder(this)
      .setAccountName(account)
      .addApi(Drive.API)
      .addScope(Drive.SCOPE_APPFOLDER)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .build()
    apiClient
  }

  def onRequestConnectionError(errorCode: Int): Unit

  def onResolveConnectionError(): Unit

  def tryToConnect(): Unit

  override def onConnectionSuspended(i: Int): Unit = { }

  override def onConnected(bundle: Bundle): Unit = {}

  override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
    onRequestConnectionError(connectionResult.getErrorCode)

}