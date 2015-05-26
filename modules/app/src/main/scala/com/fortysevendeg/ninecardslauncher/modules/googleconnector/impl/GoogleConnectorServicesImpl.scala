package com.fortysevendeg.ninecardslauncher.modules.googleconnector.impl

import android.accounts._
import android.app.Activity
import android.os.{Build, Bundle}
import com.fortysevendeg.ninecardslauncher.models.GoogleDevice
import com.fortysevendeg.ninecardslauncher.modules.googleconnector._
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.user.{AccountNotFoundException, UserService}
import com.fortysevendeg.ninecardslauncher.ui.commons.GoogleServicesConstants._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, ExecutionContext, Promise}
import scala.util.{Failure, Success, Try}

class GoogleConnectorServicesImpl(
    accountManager: AccountManager,
    oAuthScopes: String,
    repositoryServices: RepositoryServices,
    userServices: UserService)
    extends GoogleConnectorServices {

  override def requestToken(
    activity: Activity,
    username: String)(implicit executionContext: ExecutionContext): Future[Unit] = {
    val requestPromise = Promise[Unit]()
    repositoryServices.saveGoogleUser(username)
    invalidateToken()
    // TODO - This need to be improved
    requestPromise.completeWith(
      for {
      account <- getAccount(username)
      androidId <- repositoryServices.getAndroidId
    } yield {
        accountManager.getAuthToken(account, oAuthScopes, null, activity, new AccountManagerCallback[Bundle] {
          override def run(future: AccountManagerFuture[Bundle]): Unit = {
            Try {
              val authTokenBundle: Bundle = future.getResult
              val token: String = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN)
              repositoryServices.saveGoogleToken(token)
              (username,
                GoogleDevice(
                  name = Build.MODEL,
                  devideId = androidId,
                  secretToken = token,
                  permissions = Seq(oAuthScopes)))
            } match {
              case Success((email, googleDevice)) =>
                userServices.signIn(email, googleDevice) map {
                  response =>
                    requestPromise.success()
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
      })
    requestPromise.future
  }

  private def invalidateToken() {
    repositoryServices.getGoogleToken foreach (accountManager.invalidateAuthToken(AccountType, _))
    repositoryServices.resetGoogleToken
  }

  private def getAccount(name: String): Future[Account] =
    Future {
      val accounts: Seq[Account] = accountManager.getAccountsByType(AccountType).toSeq
      accounts find (_.name == name) getOrElse (throw AccountNotFoundException())
    }

}
