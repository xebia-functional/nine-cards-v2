package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserPermissions
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
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

  case class CustomException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with AuthTokenException
    with AuthTokenOperationCancelledException

  val requestUserPermissionsException = CustomException("", None)

  trait WizardPresenterScope
    extends Scope {

    val mockActions = mock[WizardActions]
    mockActions.onResultLoadUser(account) returns Ui[Any]()
    mockActions.onExceptionLoadUser() returns Ui[Any]()
    mockActions.onResultLoadAccount(userPermission) returns Ui[Any]()
    mockActions.onExceptionLoadAccount(requestUserPermissionsException) returns Ui[Any]()

    val presenter = new WizardPresenter(mockActions) {
      override protected def getAccount(username: String): Option[Account] = Some(account)
      override protected def requestUserPermissions(account: Account, client: GoogleApiClient): ServiceDef2[UserPermissions, AuthTokenException with AuthTokenOperationCancelledException] =
        Service(Task(Answer(userPermission)))
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

  "loadUser" should {

    "return a successful account" in
      new WizardPresenterScope {
        presenter.loadUser(accountName)
        there was one(mockActions).onResultLoadUser(account)
      }

    "return a failed account" in
      new WizardPresenterScope {
        presenterFailed.loadUser(accountName)
        there was one(mockActions).onExceptionLoadUser()
      }

  }

  "loadAccount" should {

    "return a successful account" in
      new WizardPresenterScope {
        presenter.loadAccount(account, mock[GoogleApiClient])
        there was after(1 seconds).one(mockActions).onResultLoadAccount(userPermission)
      }

    "return a failed account" in
      new WizardPresenterScope {
        presenterFailed.loadAccount(account, mock[GoogleApiClient])
        there was after(1 seconds).one(mockActions).onExceptionLoadAccount(requestUserPermissionsException)
      }

  }

}
