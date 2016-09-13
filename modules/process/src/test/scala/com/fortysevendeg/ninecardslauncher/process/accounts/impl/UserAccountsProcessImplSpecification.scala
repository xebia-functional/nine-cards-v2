package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.accounts.{AccountsProcessException, AccountsProcessOperationCancelledException, AccountsProcessPermissionException}
import com.fortysevendeg.ninecardslauncher.services.accounts.{AccountsServices, AccountsServicesOperationCancelledException, AccountsServicesPermissionException}
import com.fortysevendeg.ninecardslauncher.services.accounts.models.GoogleAccount
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import cats.syntax.either._


trait UserAccountsProcessImplSpecification
  extends Specification
    with Mockito
    with UserAccountsProcessImplData {

  trait UserAccountsProcessImplScope
    extends Scope {

    implicit val activityContextSupport = mock[ActivityContextSupport]

    val accountsServices = mock[AccountsServices]

    val userAccountProcess = new UserAccountsProcessImpl(accountsServices)

  }

}

class UserAccountsProcessImplSpec
  extends UserAccountsProcessImplSpecification {

  "getGoogleAccounts" should {

    "call getAccounts with the right parameters" in new UserAccountsProcessImplScope {

      accountsServices.getAccounts(any)(any) returns TaskService(Task(Either.right(accounts)))

      val result = userAccountProcess.getGoogleAccounts.value.run
      result shouldEqual Right(accounts.map(_.accountName))

      there was one(accountsServices).getAccounts(Some(GoogleAccount))(activityContextSupport)
    }

  }

  "getAuthToken" should {

    "call getAuthToken with the right parameters" in new UserAccountsProcessImplScope {

      accountsServices.getAuthToken(any, any)(any) returns TaskService(Task(Either.right(authToken)))

      val result = userAccountProcess.getAuthToken(account1.accountName, scope).value.run
      result shouldEqual Right(authToken)

      there was one(accountsServices).getAuthToken(account1, scope)(activityContextSupport)
    }

  }

  "invalidateToken" should {

    "call invalidateToken with the right parameters" in new UserAccountsProcessImplScope {

      accountsServices.invalidateToken(any, any)(any) returns TaskService(Task(Either.right(())))

      val result = userAccountProcess.invalidateToken(authToken).value.run
      result shouldEqual Right(())

      there was one(accountsServices).invalidateToken(account1.accountType, authToken)(activityContextSupport)
    }

  }

  "mapServicesException" should {

    "should map an AccountsServicesPermissionException into a AccountsProcessPermissionException" in
      new UserAccountsProcessImplScope {

        val exception = AccountsServicesPermissionException("Mocked permission exception", None)

        val result = userAccountProcess.mapServicesException(exception)
        result must beAnInstanceOf[AccountsProcessPermissionException]
        result.cause must beSome(exception)
      }

    "should map an AccountsServicesOperationCancelledException into a AccountsProcessOperationCancelledException" in
      new UserAccountsProcessImplScope {

        val exception = AccountsServicesOperationCancelledException("Mocked operation cancelled exception", None)

        val result = userAccountProcess.mapServicesException(exception)
        result must beAnInstanceOf[AccountsProcessOperationCancelledException]
        result.cause must beSome(exception)
      }

    "should map any other NineCardException into a AccountsProcessException" in
      new UserAccountsProcessImplScope {

        val exception = new NineCardException {
          override def message: String = "Mocked exception"

          override def cause: Option[Throwable] = None
        }

        val result = userAccountProcess.mapServicesException(exception)
        result must haveInterface[AccountsProcessException]
        result.cause must beSome(exception)
      }

  }

}