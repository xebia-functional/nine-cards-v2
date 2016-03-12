package com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api

import android.app.Activity
import android.content.Intent
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.google.android.gms.common.ConnectionResult

import scala.util.{Failure, Try}

trait GoogleApiClientActivityProvider
  extends GoogleApiClientProvider {

  self: Activity =>

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