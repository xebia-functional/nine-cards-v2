package com.fortysevendeg.ninecardslauncher.google.impl

import android.app.{Activity, Dialog, PendingIntent}
import android.content.Intent
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.commons.google._
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.{ConnectionCallbacks, OnConnectionFailedListener}

class GoogleServiceClientImpl(val googleApiClient: GoogleApiClient) extends GoogleServiceClient {

  override def connect(): Unit = googleApiClient.connect()

  override def connectSignInModeOptional(): Unit = googleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)

  override def isConnected: Boolean = googleApiClient.isConnected

  override def disconnect(): Unit = googleApiClient.disconnect()

  override def registerCallbacks(callbacks: GoogleServiceClientCallback): Unit = {
    googleApiClient.registerConnectionCallbacks(new ConnectionCallbacks {

      override def onConnectionSuspended(cause: Int): Unit =
        callbacks.onConnectionSuspended(ConnectionSuspendedCause(cause))

      override def onConnected(bundle: Bundle): Unit =
        callbacks.onConnected()
    })
    googleApiClient.registerConnectionFailedListener(new OnConnectionFailedListener {
      override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
        callbacks.onConnectionFailed(new GoogleServiceClientErrorImpl(connectionResult))
    })
  }

  override def getSignInIntent: Intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)

  override def readTokenIdFromSignIn(data: Intent): Option[String] =
    Option(Auth.GoogleSignInApi.getSignInResultFromIntent(data)) match {
      case Some(result) if result.isSuccess =>
        Option(result.getSignInAccount) flatMap (account => Option(account.getIdToken))
      case _ => None
    }
}

object ConnectionSuspendedCause {

  def apply(cause: Int): ConnectionSuspendedCause =
    cause match {
      case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST => CauseNetworkLost
      case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED => CauseServiceDisconnected
      case _ => CauseUnknown
    }

}

class GoogleServiceClientErrorImpl(connectionResult: ConnectionResult)
  extends GoogleServiceClientError {

  override def hasResolution: Boolean = connectionResult.hasResolution

  override def getResolution: PendingIntent = connectionResult.getResolution

  override def shouldShowErrorDialog: Boolean =
    connectionResult.getErrorCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
    connectionResult.getErrorCode == ConnectionResult.SERVICE_MISSING ||
    connectionResult.getErrorCode == ConnectionResult.SERVICE_DISABLED

  override def getErrorDialog(activity: Activity, requestCode: Int): Dialog =
    GoogleApiAvailability.getInstance()
      .getErrorDialog(activity, connectionResult.getErrorCode, requestCode)
}