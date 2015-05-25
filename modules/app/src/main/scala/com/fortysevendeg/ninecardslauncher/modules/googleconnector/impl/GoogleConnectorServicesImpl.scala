package com.fortysevendeg.ninecardslauncher.modules.googleconnector.impl

import android.accounts._
import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.{Build, Bundle}
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models.GoogleDevice
import com.fortysevendeg.ninecardslauncher.modules.api.LoginRequest
import com.fortysevendeg.ninecardslauncher.modules.googleconnector._
import com.fortysevendeg.ninecardslauncher.modules.user.UserServices
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.ninecardslauncher2.R

import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

class GoogleConnectorServicesImpl(
    accountManager: AccountManager,
    resources: Resources,
    preferences: SharedPreferences,
    userServices: UserServices)
    extends GoogleConnectorServices {

  import GoogleConnector._

  override def requestToken(activity: Activity): Service[RequestTokenRequest, RequestTokenResponse] =
    request => {
      val requestPromise = Promise[RequestTokenResponse]()
      setUser(request.username)
      invalidateToken()
      (for {
        account <- getAccount(request.username)
        androidId <- userServices.getAndroidId
      } yield {
          val oauthScopes = resources.getString(R.string.oauth_scopes)
          accountManager.getAuthToken(account, oauthScopes, null, activity, new AccountManagerCallback[Bundle] {
            override def run(future: AccountManagerFuture[Bundle]): Unit = {
              Try {
                val authTokenBundle: Bundle = future.getResult
                val token: String = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN)
                setToken(token)
                LoginRequest(
                  email = request.username,
                  device = GoogleDevice(
                    name = Build.MODEL,
                    devideId = androidId,
                    secretToken = token,
                    permissions = Seq(resources.getString(R.string.oauth_scopes))
                  )
                )
              } match {
                case Success(loginRequest) =>
                  userServices.signIn(loginRequest) map {
                    response =>
                      requestPromise.success(RequestTokenResponse())
                  } recover {
                    case ex: Throwable => requestPromise.failure(ex)
                    case _ => requestPromise.failure(GoogleUnexpectedException())
                  }
                case Failure(ex) => ex match {
                  case ex: OperationCanceledException => requestPromise.failure(GoogleOperationCanceledException())
                  case _ => requestPromise.failure(GoogleUnexpectedException())
                }
              }
            }
          }, null)
        }) getOrElse requestPromise.failure(GoogleUnexpectedException())
      requestPromise.future
    }

  override def getUser: Option[String] = Option(preferences.getString(GoogleKeyUser, null))

  override def getToken: Option[String] = Option(preferences.getString(GoogleKeyToken, null))

  private def setUser(user: String) = preferences.edit.putString(GoogleKeyUser, user).apply()

  private def setToken(token: String) = preferences.edit.putString(GoogleKeyToken, token).apply()

  private def invalidateToken() {
    getToken foreach (accountManager.invalidateAuthToken(AccountType, _))
    setToken(null)
  }

  private def getAccount(name: String): Option[Account] = {
    val accounts: Seq[Account] = accountManager.getAccountsByType(AccountType).toSeq
    accounts find (_.name == name)
  }

}

object GoogleConnector {
  val GoogleKeyPreferences = "__google_auth__"
  val GoogleKeyUser = "__google_user__"
  val GoogleKeyToken = "__google_token__"
}
