package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.app.Activity._
import android.content.Intent
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActivityResult._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.{GoogleApiClient, ResultCallback}
import com.google.android.gms.plus.{People, Plus}
import macroid.ActivityContextWrapper
import macroid.FullDsl._

class UserProfileProvider(account: String,
     onConnectedUserProfile: (String, String, String) => Unit,
     onConnectedPlusProfile: (String) => Unit)(implicit activityContextWrapper: ActivityContextWrapper)
  extends GoogleApiClient.ConnectionCallbacks {

  private[this] val apiClient: GoogleApiClient = {
    val gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestScopes(Plus.SCOPE_PLUS_LOGIN, Plus.SCOPE_PLUS_PROFILE)
      .setAccountName(account)
      .build()

    new GoogleApiClient.Builder(activityContextWrapper.bestAvailable)
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .addApi(Plus.API)
      .addConnectionCallbacks(this)
      .build()
  }

  def connect: Unit = {
    val signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient)
    runUi(uiStartIntentForResult(signInIntent, resolveConnectedUser))
  }

  override def onConnectionSuspended(i: Int): Unit = { }

  def connectUserProfile(requestCode: Int, resultCode: Int, data: Intent): Unit =
    (requestCode, resultCode) match {
      case (`resolveConnectedUser`, RESULT_OK)=> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        val account = result.getSignInAccount
        apiClient.connect(signInModeOptional)
        onConnectedUserProfile(account.getDisplayName, account.getEmail, account.getPhotoUrl.toString)
      }
      case _ =>
    }

  override def onConnected(bundle: Bundle): Unit = {

    val me = "me"

    Plus.PeopleApi.load(apiClient, me).setResultCallback(new ResultCallback[People.LoadPeopleResult]() {

      override def onResult(loadPeopleResult: People.LoadPeopleResult): Unit = {
        val person = loadPeopleResult.getPersonBuffer.get(0)

        onConnectedPlusProfile(person.getCover.getCoverPhoto.getUrl)

      }
    })
  }
}