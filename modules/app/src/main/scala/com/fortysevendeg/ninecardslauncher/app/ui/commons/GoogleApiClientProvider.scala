package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.{ConnectionResult, GooglePlayServicesUtil}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActivityResult._
import com.google.android.gms.drive._

import scala.util.{Failure, Try}

trait GoogleApiClientProvider
  extends GoogleApiClient.ConnectionCallbacks
  with GoogleApiClient.OnConnectionFailedListener {

  self: Activity =>

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
    if (connectionResult.hasResolution) {
      Try(connectionResult.startResolutionForResult(this, resolveGooglePlayConnection)) match {
        case Failure(e) => onRequestConnectionError(connectionResult.getErrorCode)
        case _ =>
      }
    } else {
      onRequestConnectionError(connectionResult.getErrorCode)
    }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) => tryToConnect()
      case (`resolveGooglePlayConnection`, _) => onResolveConnectionError()
      case _ => self.onActivityResult(requestCode, resultCode, data)
    }
}