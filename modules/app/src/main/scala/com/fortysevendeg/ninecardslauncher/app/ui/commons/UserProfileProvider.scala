package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.app.Activity
import android.app.Activity._
import android.content.Intent
import android.util.Log
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActivityResult.resolveConnectedUser
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.{GoogleSignInAccount, GoogleSignInOptions}
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.plus.Plus
import macroid.Ui

trait UserProfileProvider {

  self: Activity =>

  private[this] def createGoogleAuthClient(account: String): GoogleApiClient = {
    val gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestScopes(Plus.SCOPE_PLUS_LOGIN, Plus.SCOPE_PLUS_PROFILE)
      .setAccountName(account)
      .build()

    val apiClient = new GoogleApiClient.Builder(this)
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .addApi(Plus.API)
      .build()

    apiClient
  }

  def connectUserProfile(account: String): Unit = {
    val signInIntent = Auth.GoogleSignInApi.getSignInIntent(createGoogleAuthClient(account))
    startActivityForResult(signInIntent, resolveConnectedUser)
  }

  def onConnectedUserProfile(name: String, avatarUrl: String): Unit

  def checkUserProfile(requestCode: Int, resultCode: Int, data: Intent): Unit =
    (requestCode, resultCode) match {
      case (resolveConnectedUser, RESULT_OK)=> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        val account = result.getSignInAccount
        onConnectedUserProfile(account.getDisplayName, account.getPhotoUrl.toString)
      }
      case _ =>
    }
}