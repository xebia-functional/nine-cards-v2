package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{AccountManager, AccountManagerFuture, OperationCanceledException}
import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.Statuses.GoogleApiClientStatuses
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcess
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionExceptionImpl, CollectionProcess}
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentException, MomentProcess}
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, ContextWrapper, Ui}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

import scala.concurrent.duration._
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

    val mockContext = mock[Activity]

    val mockResources = mock[Resources]

    val mockActions = mock[WizardUiActions]

    mockContextWrapper.getOriginal returns mockContext

    mockContextWrapper.bestAvailable returns mockContext

    mockInjector.createCloudStorageProcess(any) returns mockCloudStorageProcess

    mockInjector.collectionProcess returns mockCollectionProcess

    mockInjector.momentProcess returns mockMomentProcess

    mockInjector.userConfigProcess returns mockUserConfigProcess

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
    mockActions.showDiveIn() returns Ui[Any]()

    val presenter = new WizardPresenter(mockActions) {

      override implicit def contextSupport(implicit ctx: ContextWrapper): ContextSupport = mockContextSupport

      override implicit lazy val di: Injector = mockInjector

      override lazy val accounts = Seq(account)

      override lazy val accountManager = mockAccountManager

      override lazy val preferences = mockSharedPreferences

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

  "Save Current Device" should {

    "call to show dive in Actions and create or update actual cloud storage device with the right params in CloudStorageProcess" in
      new WizardPresenterScope {
        presenter.clientStatuses = GoogleApiClientStatuses(apiClient = Some(mockGoogleApiClient))

        mockCollectionProcess.getCollections returns Service(Task(Answer(Seq(collection))))
        mockMomentProcess.getMoments returns Service(Task(Answer(moments)))
        mockCloudStorageProcess.createOrUpdateActualCloudStorageDevice(any, any)(any) returns Service(Task(Answer(Unit)))

        presenter.saveCurrentDevice()

        there was after(1.seconds).one(mockCloudStorageProcess).createOrUpdateActualCloudStorageDevice(
          Seq(cloudStorageCollection),
          Seq(cloudStorageMoment))(mockContextSupport)
        there was after(1.seconds).one(mockActions).showDiveIn()
      }

    "call to show dive in Actions but not to CloudStorageProcess if the collection process returns an error" in
      new WizardPresenterScope {
        presenter.clientStatuses = GoogleApiClientStatuses(apiClient = Some(mockGoogleApiClient))

        mockCollectionProcess.getCollections returns Service(Task(Errata(CollectionExceptionImpl(""))))
        mockMomentProcess.getMoments returns Service(Task(Answer(moments)))
        mockCloudStorageProcess.createOrUpdateActualCloudStorageDevice(any, any)(any) returns Service(Task(Answer(Unit)))

        presenter.saveCurrentDevice()

        there was after(1.seconds).no(mockCloudStorageProcess).createOrUpdateActualCloudStorageDevice(any, any)(any)
        there was after(1.seconds).one(mockActions).showDiveIn()
      }

    "call to show dive in Actions but not to CloudStorageProcess if the moment process returns an error" in
      new WizardPresenterScope {
        presenter.clientStatuses = GoogleApiClientStatuses(apiClient = Some(mockGoogleApiClient))

        mockCollectionProcess.getCollections returns Service(Task(Answer(Seq(collection))))
        mockMomentProcess.getMoments returns Service(Task(Errata(MomentException(""))))
        mockCloudStorageProcess.createOrUpdateActualCloudStorageDevice(any, any)(any) returns Service(Task(Answer(Unit)))

        presenter.saveCurrentDevice()

        there was after(1.seconds).no(mockCloudStorageProcess).createOrUpdateActualCloudStorageDevice(any, any)(any)
        there was after(1.seconds).one(mockActions).showDiveIn()
      }

    "call to connectAccount if the Google Api Client is not set but had set an account" in
      new WizardPresenterScope {
        presenter.clientStatuses = GoogleApiClientStatuses(username = Some(accountName))

        mockAccountManager.getAuthToken(any, any, any, any[Activity], any, any) returns mockAccountManagerFuture

        mockAccountManagerFuture.getResult returns mockBundle

        mockBundle.getString(AccountManager.KEY_AUTHTOKEN) returns token

        mockResources.getString(anyInt) returns googleScopes

        mockSharedPreferences.getString(any, any) returns token

        presenter.saveCurrentDevice()

        there was after(1.seconds).two(mockActions).showLoading()
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, androidMarketScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockAccountManager).getAuthToken(account, googleScopes, javaNull, mockContext, javaNull, javaNull)
        there was after(1.seconds).one(mockEditor).putString(presenter.googleKeyToken, javaNull)
        there was after(1.seconds).one(mockAccountManager).invalidateAuthToken(presenter.accountType, token)

      }

    "call to go to user in Actions if neither the Google Api Client or the account is set" in
      new WizardPresenterScope {
        presenter.clientStatuses = GoogleApiClientStatuses()

        presenter.saveCurrentDevice()

        there was after(1.seconds).one(mockActions).goToUser()
      }

  }

}
