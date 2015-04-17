package com.fortysevendeg.ninecardslauncher.modules.googleconnector.impl

import android.accounts.{Account, AccountManager, AccountManagerCallback, AccountManagerFuture}
import android.content.Context
import android.net.Uri
import android.os.{Build, Bundle}
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.api.{ApiServicesComponent, GoogleDevice, LoginRequest}
import com.fortysevendeg.ninecardslauncher.modules.googleconnector._
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

trait GoogleConnectorServicesComponentImpl
  extends GoogleConnectorServicesComponent {

  self: AppContextProvider with ApiServicesComponent =>

  lazy val googleConnectorServices = new GoogleConnectorServicesImpl

  class GoogleConnectorServicesImpl
    extends GoogleConnectorServices {

    import GoogleConnector._

    val preferences = appContextProvider.get.getSharedPreferences(GoogleKeyPreferentes, Context.MODE_PRIVATE)

    val accountManager = AccountManager.get(appContextProvider.get)

    override def requestToken(implicit activityContext: ActivityContext): Service[RequestTokenRequest, RequestTokenResponse] =
      request => {
        val requestPromise = Promise[RequestTokenResponse]()
        setUser(request.username)
        invalidateToken()
        getAccount(request.username) map {
          account =>
            val oauthScopes = resGetString(R.string.oauth_scopes)
            accountManager.getAuthToken(account, oauthScopes, null, activityContext.get, new AccountManagerCallback[Bundle] {
              override def run(future: AccountManagerFuture[Bundle]): Unit = {
                val authTokenBundle: Bundle = future.getResult
                val token: String = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN)
                setToken(token)
                val loginRequest = LoginRequest(
                  email = request.username,
                  device = GoogleDevice(
                    name = Build.MODEL,
                    devideId = getAndroidId.get,
                    secretToken = token,
                    permissions = Seq(appContextProvider.get.getString(R.string.oauth_scopes))
                  )
                )
                apiServices.login(loginRequest) map {
                  response =>
                    requestPromise.complete(Try(RequestTokenResponse(true)))
                } recover {
                  case _ => requestPromise.complete(Try(RequestTokenResponse(false)))
                }
              }
            }, null)
        } getOrElse requestPromise.complete(Try(RequestTokenResponse(false)))
        requestPromise.future
      }

    override def getUser: Option[String] = Option(preferences.getString(GoogleKeyUser, null))

    override def getToken: Option[String] = Option(preferences.getString(GoogleKeyToken, null))

    private def setUser(user: String) = preferences.edit.putString(GoogleKeyUser, user).apply()

    private def setToken(token: String) = preferences.edit.putString(GoogleKeyToken, token).apply()

    private def invalidateToken() {
      getToken map {
        token =>
          val accountManager = AccountManager.get(appContextProvider.get)
          accountManager.invalidateAuthToken(AccountType, token)
      }
      setToken(null)
    }

    private def getAccount(name: String): Option[Account] = {
      val accounts: Seq[Account] = accountManager.getAccountsByType(AccountType).toSeq
      accounts find (_.name == name)
    }

    def getAndroidId: Option[String] = Try {
      val cursor = Option(appContextProvider.get.getContentResolver.query(Uri.parse(ContentGServices), null, null, Array(AndroidId), null))
      cursor filter(c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
    } match {
      case Success(id) => id
      case Failure(ex) => None
    }

  }

}

object GoogleConnector {
  val GoogleKeyPreferentes = "__google_auth__"
  val GoogleKeyUser = "__google_user__"
  val GoogleKeyToken = "__google_token__"
}
