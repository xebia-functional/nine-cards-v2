package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.services.accounts.AccountsServices
import com.fortysevendeg.ninecardslauncher.services.accounts.models.GoogleAccount
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait UserAccountsProcessImplSpecification
  extends Specification
  with Mockito
  with UserAccountsProcessImplData {

  trait UserAccountsProcessImplScope
    extends Scope {

    implicit val context = mock[ContextSupport]

    implicit val activityContextWrapper = mock[ActivityContextWrapper]

    val accountsServices = mock[AccountsServices]

    val userAccountProcess = new UserAccountsProcessImpl(accountsServices)

  }

}

class UserAccountsProcessImplSpec
  extends UserAccountsProcessImplSpecification {

  "getGoogleAccounts" should {

    "call getAccounts with the right parameters" in new UserAccountsProcessImplScope {

      accountsServices.getAccounts(any)(any) returns CatsService(Task(Xor.right(accounts)))

      val result = userAccountProcess.getGoogleAccounts.value.run
      result shouldEqual Xor.Right(accounts.map(_.accountName))

      there was one(accountsServices).getAccounts(Some(GoogleAccount))(activityContextWrapper)
    }

  }

  "getAuthToken" should {

    "call getAuthToken with the right parameters" in new UserAccountsProcessImplScope {

      accountsServices.getAuthToken(any, any)(any) returns CatsService(Task(Xor.right(authToken)))

      val result = userAccountProcess.getAuthToken(account1.accountName, scope).value.run
      result shouldEqual Xor.Right(authToken)

      there was one(accountsServices).getAuthToken(account1, scope)(activityContextWrapper)
    }

  }

  "invalidateToken" should {

    "call invalidateToken with the right parameters" in new UserAccountsProcessImplScope {

      accountsServices.invalidateToken(any, any)(any) returns CatsService(Task(Xor.right(())))

      val result = userAccountProcess.invalidateToken(authToken).value.run
      result shouldEqual Xor.Right(())

      there was one(accountsServices).invalidateToken(account1.accountType, authToken)(activityContextWrapper)
    }

  }

}