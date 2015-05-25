package com.fortysevendeg.ninecardslauncher.modules.googleconnector.impl

import android.accounts._
import android.app.Activity
import android.os.{Build, Bundle}
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models.GoogleDevice
import com.fortysevendeg.ninecardslauncher.modules.googleconnector._
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.user.UserService
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

class GoogleConnectorServicesImpl(
    accountManager: AccountManager,
    oAuthScopes: String,
    repositoryServices: RepositoryServices,
    userServices: UserService)
    extends GoogleConnectorServices {

  override def requestToken(activity: Activity): Service[RequestTokenRequest, RequestTokenResponse] =
    request => {
      val requestPromise = Promise[RequestTokenResponse]()
      repositoryServices.saveGoogleUser(request.username)
      invalidateToken()
      (for {
        account <- getAccount(request.username)
        androidId <- repositoryServices.getAndroidId
      } yield {
          accountManager.getAuthToken(account, oAuthScopes, null, activity, new AccountManagerCallback[Bundle] {
            override def run(future: AccountManagerFuture[Bundle]): Unit = {
              Try {
                val authTokenBundle: Bundle = future.getResult
                val token: String = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN)
                repositoryServices.saveGoogleToken(token)
                (request.username,
                 GoogleDevice(
                    name = Build.MODEL,
                    devideId = androidId,
                    secretToken = token,
                    permissions = Seq(oAuthScopes)))
              } match {
                case Success((email, googleDevice)) =>
                  userServices.signIn(email, googleDevice) map {
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

  private def invalidateToken() {
    repositoryServices.getGoogleToken foreach (accountManager.invalidateAuthToken(AccountType, _))
    repositoryServices.resetGoogleToken
  }

  private def getAccount(name: String): Option[Account] = {
    val accounts: Seq[Account] = accountManager.getAccountsByType(AccountType).toSeq
    accounts find (_.name == name)
  }

}
