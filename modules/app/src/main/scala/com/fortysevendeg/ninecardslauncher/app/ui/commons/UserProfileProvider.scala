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

import scala.util.Try

case class UserProfileStatuses(
    userProfile: Option[UserProfileProvider] = None)

class UserProfileProvider(account: String,
     onConnectedUserProfile: (String, String, Option[String]) => Unit,
     onConnectedPlusProfile: (String) => Unit)(implicit activityContextWrapper: ActivityContextWrapper)
  extends GoogleApiClient.ConnectionCallbacks {

  val me = "me"

  private[this] val apiClient: GoogleApiClient = {
    val gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestScopes(Plus.SCOPE_PLUS_PROFILE)
      .setAccountName(account)
      .build()

    new GoogleApiClient.Builder(activityContextWrapper.bestAvailable)
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .addApi(Plus.API)
      .addConnectionCallbacks(this)
      .build()
  }

  def connect(): Unit = {
    val signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient)
    runUi(uiStartIntentForResult(signInIntent, resolveConnectedUser))
  }

  override def onConnectionSuspended(i: Int): Unit = { }

  def connectUserProfile(requestCode: Int, resultCode: Int, data: Intent): Unit =
    (requestCode, resultCode) match {
      case (`resolveConnectedUser`, RESULT_OK)=> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        val signInAccount = result.getSignInAccount
        apiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
        val avatarUrl = Option(signInAccount.getPhotoUrl) map (_.toString)
        onConnectedUserProfile(signInAccount.getDisplayName, account, avatarUrl)
      }
      case _ =>
    }

  override def onConnected(bundle: Bundle): Unit = {

    Plus.PeopleApi.load(apiClient, me).setResultCallback(new ResultCallback[People.LoadPeopleResult]() {

      override def onResult(loadPeopleResult: People.LoadPeopleResult): Unit = {
        for {
          people <- Option(loadPeopleResult)
          personBuffer <- Option(people.getPersonBuffer)
          person <- Try(personBuffer.get(0)).toOption
          cover <- Option(person.getCover)
          coverPhoto <- Option(cover.getCoverPhoto)
          url <- Option(coverPhoto.getUrl)
        } yield onConnectedPlusProfile(url)
      }
    })
  }
}