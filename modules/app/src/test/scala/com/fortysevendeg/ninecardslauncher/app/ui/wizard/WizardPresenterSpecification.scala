package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{AccountManager, AccountManagerFuture, OperationCanceledException}
import android.app.Activity
import android.content.{ComponentName, Intent, SharedPreferences}
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager, FragmentTransaction}
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.Statuses.WizardPresenterStatuses
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcess
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionExceptionImpl, CollectionProcess}
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentException, MomentExceptionImpl, MomentProcess}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, ContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

import scala.concurrent.duration._
import scala.ref.WeakReference
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

    implicit val mockContextSupport = mock[ContextSupport]

    implicit val mockContextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    val mockCollectionProcess = mock[CollectionProcess]

    val mockMomentProcess = mock[MomentProcess]

    val mockUserConfigProcess = mock[UserConfigProcess]

    val mockGoogleApiClient = mock[GoogleApiClient]

    val mockAccountManager = mock[AccountManager]

    val mockSharedPreferences = mock[SharedPreferences]

    val mockEditor = mock[SharedPreferences.Editor]

    val mockAccountManagerFuture = mock[AccountManagerFuture[Bundle]]

    val mockBundle = mock[Bundle]

    val mockContextActivity = mock[AppCompatActivity]

    val mockFragmentManager = mock[FragmentManager]

    val mockFragmentTransaction = mock[FragmentTransaction]

    val mockResources = mock[Resources]

    val mockActions = mock[WizardUiActions]

    val mockIntent = mock[Intent]

    mockContextWrapper.getOriginal returns mockContextActivity

    mockContextWrapper.original returns new WeakReference[Activity](mockContextActivity)

    mockContextWrapper.bestAvailable returns mockContextActivity

    mockContextActivity.getSupportFragmentManager returns mockFragmentManager

    mockFragmentManager.beginTransaction() returns mockFragmentTransaction

    mockInjector.createCloudStorageProcess(any) returns mockCloudStorageProcess

    mockInjector.collectionProcess returns mockCollectionProcess

    mockInjector.momentProcess returns mockMomentProcess

    mockInjector.userConfigProcess returns mockUserConfigProcess

    mockSharedPreferences.edit() returns mockEditor

    mockEditor.putString(any, any) returns mockEditor

    mockContextActivity.getResources returns mockResources

    mockActions.initialize(any) returns Ui[Any]()
    mockActions.showLoading() returns Ui[Any]()
    mockActions.goToUser() returns Ui[Any]()
    mockActions.goToWizard() returns Ui[Any]()
    mockActions.showErrorConnectingGoogle() returns Ui[Any]()
    mockActions.showErrorLoginUser() returns Ui[Any]()
    mockActions.showErrorAcceptTerms() returns Ui[Any]()
    mockActions.showErrorSelectUser() returns Ui[Any]()
    mockActions.showDiveIn() returns Ui[Any]()

    val presenter = new WizardPresenter(mockActions) {

      override implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport = mockContextSupport

      override implicit lazy val di: Injector = mockInjector

      override lazy val accounts = Seq(account)

      override lazy val accountManager = mockAccountManager

      override lazy val preferences = mockSharedPreferences

      override def createGoogleDriveClient(account: String)(implicit contextWrapper: ContextWrapper): GoogleApiClient = mockGoogleApiClient

      override protected def createIntent[T](activity: Activity, targetClass: Class[T]): Intent = mockIntent
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
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockEditor).putString(presenter.googleKeyToken, token)
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
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockEditor).putString(presenter.googleKeyToken, token)
        there was after(1.seconds).no(mockEditor).putString(presenter.googleKeyToken, javaNull)
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

        mockFragmentTransaction.add(any[Fragment], anyString) returns mockFragmentTransaction

        mockFragmentTransaction.addToBackStack(any) returns mockFragmentTransaction

        val exception = mock[OperationCanceledException]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult throws exception

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).one(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockFragmentTransaction).add(any[DialogFragment], anyString)
      }

    "show a dialog when there is a Operation Cancelled error requesting Drive token" in
      new WizardPresenterScope {

        mockFragmentTransaction.add(any[Fragment], anyString) returns mockFragmentTransaction

        mockFragmentTransaction.addToBackStack(any) returns mockFragmentTransaction

        val exception = mock[OperationCanceledException]

        val mockAccountManagerFutureEx = mock[AccountManagerFuture[Bundle]]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManager.getAuthToken(account, googleScopes, javaNull, mockContextActivity, javaNull, javaNull) returns mockAccountManagerFutureEx

        mockAccountManagerFuture.getResult returns mockBundle

        mockAccountManagerFutureEx.getResult throws exception

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockFragmentTransaction).add(any[DialogFragment], anyString)
      }

    "call to show error connecting Google in Actions when there an unexpected error requesting Market token" in
      new WizardPresenterScope {

        val exception = mock[RuntimeException]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult throws exception

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).one(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
      }

    "call to show error connecting Google in Actions when there an unexpected error requesting Drive token" in
      new WizardPresenterScope {

        val exception = mock[RuntimeException]

        val mockAccountManagerFutureEx = mock[AccountManagerFuture[Bundle]]

        mockAccountManager.getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull) returns mockAccountManagerFuture

        mockAccountManager.getAuthToken(account, googleScopes, javaNull, mockContextActivity, javaNull, javaNull) returns mockAccountManagerFutureEx

        mockAccountManagerFuture.getResult returns mockBundle

        mockAccountManagerFutureEx.getResult throws exception

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        presenter.connectAccount(accountName, termsAccept = true)

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContextActivity, javaNull, javaNull)
        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
      }

  }

  "Generate Collections" should {

    "call to go to wizard in Actions and startService in the activity with a new configuration" in
      new WizardPresenterScope {

        mockContextActivity.startService(any) returns mock[ComponentName]

        presenter.generateCollections(None)

        there was after(1.seconds).one(mockActions).goToWizard()
        there was after(1.seconds).one(mockIntent).putExtra(CreateCollectionService.cloudIdKey, CreateCollectionService.newConfiguration)
        there was after(1.seconds).one(mockContextActivity).startService(mockIntent)
    }

    "call to go to wizard in Actions and startService in the activity with device id" in
      new WizardPresenterScope {

        mockContextActivity.startService(any) returns mock[ComponentName]

        presenter.generateCollections(Some(intentKey))

        there was after(1.seconds).one(mockActions).goToWizard()
        there was after(1.seconds).one(mockIntent).putExtra(CreateCollectionService.cloudIdKey, intentKey)
        there was after(1.seconds).one(mockContextActivity).startService(mockIntent)
    }
  }

  "Finish Wizard" should {

    "set the result and call finish in the activity" in
      new WizardPresenterScope {

        presenter.finishWizard()

        there was after(1.seconds).one(mockContextActivity).setResult(Activity.RESULT_OK)
        there was after(1.seconds).one(mockContextActivity).finish()
    }

  }

  "Activity Result" should {

    "return true and call to connect in Google Api Client when pass `resolveGooglePlayConnection` and RESULT_OK" in
      new WizardPresenterScope {
        presenter.clientStatuses = WizardPresenterStatuses(driveApiClient = Some(mockGoogleApiClient))

        val result = presenter.activityResult(RequestCodes.resolveGooglePlayConnection, Activity.RESULT_OK, javaNull)

        result should beTrue

        there was after(1.seconds).one(mockGoogleApiClient).connect()
    }

    "return true and call show error connecting google when pass `resolveGooglePlayConnection` and a value distinct to RESULT_OK" in
      new WizardPresenterScope {
        val result = presenter.activityResult(RequestCodes.resolveGooglePlayConnection, Activity.RESULT_CANCELED, javaNull)

        result should beTrue

        there was after(1.seconds).one(mockActions).showErrorConnectingGoogle()
    }

    "return true and call show error connecting google when pass a different request code" in
      new WizardPresenterScope {
        val result = presenter.activityResult(RequestCodes.goToProfile, Activity.RESULT_OK, javaNull)

        result should beFalse
    }

  }

  "Stop" should {

    "call to disconnect in the Google Api client" in
      new WizardPresenterScope {
        presenter.clientStatuses = WizardPresenterStatuses(driveApiClient = Some(mockGoogleApiClient))

        presenter.stop()

        there was after(1.seconds).one(mockGoogleApiClient).disconnect()
    }

  }

}
