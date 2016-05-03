package com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive._
import macroid.ContextWrapper

trait GoogleApiClientProvider
  extends GoogleApiClient.ConnectionCallbacks
  with GoogleApiClient.OnConnectionFailedListener {

  def createGoogleDriveClient(account: String)(implicit contextWrapper: ContextWrapper): GoogleApiClient = {
    val apiClient = new GoogleApiClient.Builder(contextWrapper.bestAvailable)
      .setAccountName(account)
      .addApi(Drive.API)
      .addScope(Drive.SCOPE_APPFOLDER)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .build()
    apiClient
  }

  override def onConnectionSuspended(i: Int): Unit = {}

  override def onConnected(bundle: Bundle): Unit = {}

  override def onConnectionFailed(connectionResult: ConnectionResult): Unit = {}

}