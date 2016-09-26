package com.fortysevendeg.ninecardslauncher.process.commons

import com.google.android.gms.common.api.GoogleApiClient

sealed trait ConnectionSuspendedCause

case object CauseNetworkLost extends ConnectionSuspendedCause

case object CauseServiceDisconnected extends ConnectionSuspendedCause

case object CauseUnknown extends ConnectionSuspendedCause

object ConnectionSuspendedCause {

  def apply(cause: Int): ConnectionSuspendedCause =
    cause match {
      case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST => CauseNetworkLost
      case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED => CauseServiceDisconnected
      case _ => CauseUnknown
    }

}
