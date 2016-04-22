package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts
import android.accounts.{Account, AccountManager, AccountManagerFuture, OperationCanceledException}
import android.app.Activity
import android.content.{Context, SharedPreferences}
import android.content.res.Resources
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, ContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

import scala.concurrent.duration._
//import scala.language.postfixOps
import scalaz.concurrent.Task

trait WizardPresenterSpecification
  extends Specification
  with Mockito
  with WizardPresenterData {

  case class RequestUserPermissionException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with AuthTokenException
    with AuthTokenOperationCancelledException

  val requestUserPermissionsException = RequestUserPermissionException("", None)

  trait WizardPresenterScope
    extends Scope {

    implicit val contextSupport = mock[ContextSupport]

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockGoogleApiClient = mock[GoogleApiClient]

    val mockAccountManager = mock[AccountManager]

    val mockSharedPreferences = mock[SharedPreferences]

    val mockEditor = mock[SharedPreferences.Editor]

    val mockAccountManagerFuture = mock[AccountManagerFuture[Bundle]]

    val mockBundle = mock[Bundle]

    val mockContext = mock[Activity]

    val mockResources = mock[Resources]

    val mockActions = mock[WizardUiActions]

    contextWrapper.getOriginal returns mockContext

    contextWrapper.bestAvailable returns mockContext

    mockSharedPreferences.edit() returns mockEditor

    mockEditor.putString(any, any) returns mockEditor

    mockContext.getResources returns mockResources

    mockActions.initialize(any) returns Ui[Any]()
    mockActions.showLoading() returns Ui[Any]()
    mockActions.goToUser() returns Ui[Any]()
    mockActions.goToWizard() returns Ui[Any]()
    mockActions.showErrorConnectingGoogle() returns Ui[Any]()
    mockActions.showErrorLoginUser() returns Ui[Any]()
    mockActions.showErrorAcceptTerms() returns Ui[Any]()
    mockActions.showErrorSelectUser() returns Ui[Any]()



    val presenter = new WizardPresenter(mockActions) {

      lazy override val accounts = Seq(account)

      lazy override val accountManager = mockAccountManager

      lazy override val preferences = mockSharedPreferences

      override def createGoogleDriveClient(account: String)(implicit contextWrapper: ContextWrapper): GoogleApiClient = mockGoogleApiClient

    }

  }

}

class WizardPresenterSpec
  extends WizardPresenterSpecification {

  "Initialize" should {

    "call to initialize in Actions with the accounts" in new WizardPresenterScope {

      presenter.initialize()

      there was after(1.seconds).one(mockActions).initialize(accounts)
    }
  }

  "Go to User" should {

    "call to Go to User in Actions" in new WizardPresenterScope {

      presenter.goToUser()

      there was after(1.seconds).one(mockActions).goToUser()
    }
  }

  "Go to Wizard" should {

    "call to Go to Wizard in Actions" in new WizardPresenterScope {

      presenter.goToWizard()

      there was after(1.seconds).one(mockActions).goToWizard()
    }
  }

  "Connect Account" should {

    "return a successful connecting account" in
      new WizardPresenterScope {

        mockAccountManager.getAuthToken(any, any, any, any[Activity], any, any) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult returns mockBundle

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        mockSharedPreferences.getString(any, any) returns token

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockEditor).putString(presenter.googleKeyToken, javaNull)
        there was after(1.seconds).one(mockAccountManager).invalidateAuthToken(presenter.accountType, token)
      }

    "return a successful connecting account with does not call to invalidate auth token if there is no token stored" in
      new WizardPresenterScope {

        mockAccountManager.getAuthToken(any, any, any, any[Activity], any, any) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult returns mockBundle

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        mockSharedPreferences.getString(any, any) returns javaNull

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockEditor).putString(presenter.googleKeyToken, javaNull)
        there was after(1.seconds).no(mockAccountManager).invalidateAuthToken(any, any)
      }

    "call to show error accept term in Actions when the terms are not accepted" in
      new WizardPresenterScope {
        presenter.connectAccount(accountName, termsAccept = false)
        there was after(1.seconds).one(mockActions).showErrorAcceptTerms()
      }

    "call to show error select user in Actions when there is no account" in
      new WizardPresenterScope {
        presenter.connectAccount(nonExistingAccountName, termsAccept = true)
        there was after(1.seconds).one(mockActions).showErrorSelectUser()
      }

    "call to show error Android Market not accepted in Actions when there is a Operation Cancelled error requesting Market token" in
      new WizardPresenterScope {

        val exception = mock[OperationCanceledException]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult throws exception

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).one(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockActions).showErrorAndroidMarketNotAccepted()
      }

    "call to show error Google Drive not accepted in Actions when there is a Operation Cancelled error requesting Drive token" in
      new WizardPresenterScope {

        val exception = mock[OperationCanceledException]

        val mockAccountManagerFutureEx = mock[AccountManagerFuture[Bundle]]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManager.getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull) returns mockAccountManagerFutureEx

        mockAccountManagerFuture.getResult returns mockBundle

        mockAccountManagerFutureEx.getResult throws exception

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockActions).showErrorGoogleDriveNotAccepted()
      }

    "call to show error connecting Google in Actions when there an unexpected error requesting Market token" in
      new WizardPresenterScope {

        val exception = mock[RuntimeException]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult throws exception

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).one(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
      }

    "call to show error connecting Google in Actions when there an unexpected error requesting Drive token" in
      new WizardPresenterScope {

        val exception = mock[RuntimeException]

        val mockAccountManagerFutureEx = mock[AccountManagerFuture[Bundle]]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManager.getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull) returns mockAccountManagerFutureEx

        mockAccountManagerFuture.getResult returns mockBundle

        mockAccountManagerFutureEx.getResult throws exception

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
      }

  }

//  "Get Devices" should {
//
//    "return a successful devices" in
//      new WizardPresenterScope {
//        presenter.getDevices(Some(mockGoogleApiClient), Some(accountName), Some(userPermission))
//        there was after(1.seconds).one(mockActions).showLoading()
//        there was after(1.seconds).one(mockActions).showDevices(userCloudDevices)
//      }
//
//    "error if Google Api Client don't exist" in
//      new WizardPresenterScope {
//        presenter.getDevices(None, Some(accountName), Some(userPermission))
//        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
//      }
//
//    "error if account name don't exist" in
//      new WizardPresenterScope {
//        presenter.getDevices(None, Some(accountName), Some(userPermission))
//        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
//      }
//
//    "error if user permission don't exist" in
//      new WizardPresenterScope {
//        presenter.getDevices(Some(mockGoogleApiClient), Some(accountName), None)
//        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
//      }
//
//  }

}
