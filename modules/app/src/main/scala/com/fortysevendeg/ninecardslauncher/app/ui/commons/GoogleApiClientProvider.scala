package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.{ConnectionResult, GooglePlayServicesUtil}
import com.google.android.gms.drive._

import scala.util.{Failure, Try}

trait GoogleApiClientProvider
  extends GoogleApiClient.ConnectionCallbacks
  with GoogleApiClient.OnConnectionFailedListener {

  self: Activity =>

  val resolveConnectionRequestCode = 1

  var client: GoogleApiClient = null

  def createGoogleApiClient(account: String) = new GoogleApiClient.Builder(this)
    .setAccountName(account)
    .addApi(Drive.API)
    .addScope(Drive.SCOPE_APPFOLDER)
    .addConnectionCallbacks(this)
    .addOnConnectionFailedListener(this)
    .build()

  def onRequestConnectionError(): Unit

  def onResolveConnectionError(): Unit

  override def onConnectionSuspended(i: Int): Unit = { }

  override def onConnected(bundle: Bundle): Unit = {}

  override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
    if (connectionResult.hasResolution) {
      Try(connectionResult.startResolutionForResult(this, resolveConnectionRequestCode)) match {
        case Failure(e) => onRequestConnectionError()
        case _ =>
      }
    } else {
      GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode, this, 0).show()
    }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
    requestCode match {
      case `resolveConnectionRequestCode` =>
        if (resultCode == Activity.RESULT_OK) client.connect() else onResolveConnectionError()
      case _ =>
        self.onActivityResult(requestCode, resultCode, data)
    }
}