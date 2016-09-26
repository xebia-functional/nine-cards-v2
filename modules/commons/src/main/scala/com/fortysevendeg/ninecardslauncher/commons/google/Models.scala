package com.fortysevendeg.ninecardslauncher.commons.google

import android.app.{Activity, Dialog, PendingIntent}
import android.content.Intent

trait GoogleServiceClient {

  def connect(): Unit

  def connectSignInModeOptional(): Unit

  def isConnected: Boolean

  def disconnect(): Unit

  def registerCallbacks(callbacks: GoogleServiceClientCallback): Unit

  def getSignInIntent: Intent

  def readTokenIdFromSignIn(data: Intent): Option[String]

}

trait GoogleServiceClientCallback {

  def onConnectionSuspended(cause: ConnectionSuspendedCause): Unit

  def onConnectionFailed(error: GoogleServiceClientError): Unit

  def onConnected(): Unit

}

sealed trait ConnectionSuspendedCause

case object CauseNetworkLost extends ConnectionSuspendedCause

case object CauseServiceDisconnected extends ConnectionSuspendedCause

case object CauseUnknown extends ConnectionSuspendedCause

trait GoogleServiceClientError {

  def hasResolution: Boolean

  def getResolution: PendingIntent

  def shouldShowErrorDialog: Boolean

  def getErrorDialog(activity: Activity, requestCode: Int): Dialog

}