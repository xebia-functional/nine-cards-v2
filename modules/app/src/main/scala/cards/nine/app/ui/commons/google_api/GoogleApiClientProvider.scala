/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons.google_api

import android.os.Bundle
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.{GoogleApiClient, Scope}
import com.google.android.gms.drive._
import com.google.android.gms.plus.Plus
import macroid.{ActivityContextWrapper, ContextWrapper}

/**
 * @deprecated use the CloudStorageProcess.createCloudStorageClient instead
 */
trait GoogleDriveApiClientProvider {

  def createGoogleDriveClient(account: String)(
      implicit contextWrapper: ContextWrapper): GoogleApiClient =
    new GoogleApiClient.Builder(contextWrapper.bestAvailable)
      .setAccountName(account)
      .addApi(Drive.API)
      .addScope(Drive.SCOPE_APPFOLDER)
      .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks {
        override def onConnectionSuspended(cause: Int): Unit =
          onDriveConnectionSuspended(ConnectionSuspendedCause(cause))

        override def onConnected(bundle: Bundle): Unit =
          onDriveConnected(bundle)
      })
      .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener {
        override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
          onDriveConnectionFailed(connectionResult)
      })
      .build()

  def onDriveConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit

  def onDriveConnected(bundle: Bundle): Unit

  def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit

}

/**
 * @deprecated use the SocialProfileProcess.createSocialProfileClient instead
 */
trait GooglePlusApiClientProvider {

  def createGooglePlusClient(account: String)(
      implicit contextWrapper: ContextWrapper): GoogleApiClient = {
    val gso =
      new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Plus.SCOPE_PLUS_PROFILE)
        .requestIdToken(contextWrapper.bestAvailable.getString(R.string.api_v2_client_id))
        .setAccountName(account)
        .build()

    new GoogleApiClient.Builder(contextWrapper.bestAvailable)
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .addApi(Plus.API)
      .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks {
        override def onConnectionSuspended(cause: Int): Unit =
          onPlusConnectionSuspended(ConnectionSuspendedCause(cause))

        override def onConnected(bundle: Bundle): Unit =
          onPlusConnected(bundle)
      })
      .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener {
        override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
          onPlusConnectionFailed(connectionResult)
      })
      .build()
  }

  def onPlusConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit

  def onPlusConnected(bundle: Bundle): Unit

  def onPlusConnectionFailed(connectionResult: ConnectionResult): Unit

}

sealed trait ConnectionSuspendedCause

case object CauseNetworkLost extends ConnectionSuspendedCause

case object CauseServiceDisconnected extends ConnectionSuspendedCause

case object CauseUnknown extends ConnectionSuspendedCause

object ConnectionSuspendedCause {

  def apply(cause: Int): ConnectionSuspendedCause =
    cause match {
      case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST =>
        CauseNetworkLost
      case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED =>
        CauseServiceDisconnected
      case _ => CauseUnknown
    }

}
