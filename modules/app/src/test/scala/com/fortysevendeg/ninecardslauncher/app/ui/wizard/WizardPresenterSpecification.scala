package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, Ui}
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

  implicit val contextSupport = mock[ContextSupport]

  implicit val contextWrapper = mock[ActivityContextWrapper]

  case class RequestUserPermissionException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with AuthTokenException
    with AuthTokenOperationCancelledException

  val requestUserPermissionsException = RequestUserPermissionException("", None)

  trait WizardPresenterScope
    extends Scope {

    val mockGoogleApiClient = mock[GoogleApiClient]

    val mockActions = mock[WizardUiActions]
    mockActions.showLoading() returns Ui[Any]()
    mockActions.showErrorConnectingGoogle() returns Ui[Any]()
    mockActions.showErrorLoginUser() returns Ui[Any]()
    mockActions.showErrorAcceptTerms() returns Ui[Any]()
    mockActions.showErrorSelectUser() returns Ui[Any]()
    mockActions.connectGoogleApiClient(userPermission) returns Ui[Any]()
    mockActions.createGoogleApiClient(account) returns mockGoogleApiClient

    val presenter = new WizardPresenter(mockActions) {
      override protected def getAccount(username: String): Option[Account] = Some(account)
      override protected def requestUserPermissions(account: Account, client: GoogleApiClient): ServiceDef2[UserPermissions, AuthTokenException with AuthTokenOperationCancelledException] =
        Service(Task(Answer(userPermission)))
      override protected def loadCloudDevices
      (client: GoogleApiClient, username: String, userPermissions: UserPermissions): ServiceDef2[UserCloudDevices, UserException with UserConfigException with CloudStorageProcessException] =
        Service(Task(Answer(userCloudDevices)))
      override protected def invalidateToken(): Unit = {}
    }

    val presenterFailed = new WizardPresenter(mockActions) {
      override protected def getAccount(username: String): Option[Account] = None
      override protected def requestUserPermissions(account: Account, client: GoogleApiClient): ServiceDef2[UserPermissions, AuthTokenException with AuthTokenOperationCancelledException] =
        Service(Task(Errata(requestUserPermissionsException)))
      override protected def invalidateToken(): Unit = {}
    }

  }

}

class WizardPresenterSpec
  extends WizardPresenterSpecification {

  "Connect Account" should {

    "return a successful connecting account" in
      new WizardPresenterScope {
        presenter.connectAccount(accountName, termsAccept = true)
        there was after(1 seconds).one(mockActions).showLoading()
        there was after(1 seconds).one(mockActions).connectGoogleApiClient(userPermission)
        there was after(1 seconds).one(mockActions).createGoogleApiClient(account)
      }

    "return a failed connecting account if terms wasn't accepted" in
      new WizardPresenterScope {
        presenter.connectAccount(accountName, termsAccept = false)
        there was after(1 seconds).one(mockActions).showErrorAcceptTerms()
      }

    "return a failed if username doesn't exist" in
      new WizardPresenterScope {
        presenterFailed.connectAccount(accountName, termsAccept = true)
        there was after(1 seconds).one(mockActions).showErrorSelectUser()
      }

  }

  "Get Devices" should {

    "return a successful devices" in
      new WizardPresenterScope {
        presenter.getDevices(Some(mockGoogleApiClient), Some(accountName), Some(userPermission))
        there was after(1 seconds).one(mockActions).showLoading()
        there was after(1 seconds).one(mockActions).showDevices(userCloudDevices)
      }

    "error if Google Api Client don't exist" in
      new WizardPresenterScope {
        presenter.getDevices(None, Some(accountName), Some(userPermission))
        there was after(1 seconds).one(mockActions).showErrorConnectingGoogle()
      }

    "error if account name don't exist" in
      new WizardPresenterScope {
        presenter.getDevices(None, Some(accountName), Some(userPermission))
        there was after(1 seconds).one(mockActions).showErrorConnectingGoogle()
      }

    "error if user permission don't exist" in
      new WizardPresenterScope {
        presenter.getDevices(Some(mockGoogleApiClient), Some(accountName), None)
        there was after(1 seconds).one(mockActions).showErrorConnectingGoogle()
      }

  }

}
