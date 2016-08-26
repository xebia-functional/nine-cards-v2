package com.fortysevendeg.ninecardslauncher.services.accounts.impl

import android.accounts.{AccountManager, AccountManagerFuture, OperationCanceledException}
import android.app.Activity
import android.os.Bundle
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.services.accounts.{AccountsServicesException, AccountsServicesExceptionImpl, AccountsServicesOperationCancelledException, AccountsServicesPermissionException}
import com.fortysevendeg.ninecardslauncher.services.accounts.models.GoogleAccount
import macroid.{ActivityContextWrapper, ContextWrapper}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.ref.WeakReference

trait AccountsServicesImplSpecification
  extends Specification
    with Mockito {

  trait AccountsServicesImplScope
    extends Scope
      with AccountsServicesImplData {

    implicit val contextWrapper = mock[ContextWrapper]

    implicit val activityContextWrapper = mock[ActivityContextWrapper]

    val activity = mock[Activity]

    val accountManager = mock[AccountManager]

    val accountManagerFuture = mock[AccountManagerFuture[Bundle]]

    accountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any) returns accountManagerFuture

    val bundle = mock[Bundle]

    val securityException = new SecurityException("")

    val runtimeException = new RuntimeException("")

    val operationCancelledException = new OperationCanceledException("")

    val accountsServices = new AccountsServicesImpl {
      override def getAccountManager(implicit contextWrapper: ContextWrapper): AccountManager = accountManager
    }

  }

}

class AccountsServicesImplSpec
  extends  AccountsServicesImplSpecification {

  "getAccounts" should {

    "returns the accounts sequence and call with the right account type" in
      new AccountsServicesImplScope {

        accountManager.getAccountsByType(any) returns Array(androidAccount1, androidAccount2)

        val result = accountsServices.getAccounts(Some(GoogleAccount)).value.run
        result shouldEqual Xor.Right(Seq(account1, account2))

        there was one(accountManager).getAccountsByType(GoogleAccount.value)
      }

    "returns the accounts sequence when call without account type" in
      new AccountsServicesImplScope {

        accountManager.getAccounts returns Array(androidAccount1, androidAccount2)

        val result = accountsServices.getAccounts(None).value.run
        result shouldEqual Xor.Right(Seq(account1, account2))

        there was one(accountManager).getAccounts
      }

    "returns an AccountsServicePermissionDeniedException when the getAccountsByType throw a SecurityException" in
      new AccountsServicesImplScope {

        accountManager.getAccountsByType(any) throws securityException

        val result = accountsServices.getAccounts(Some(GoogleAccount)).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesPermissionException]]

        there was one(accountManager).getAccountsByType(GoogleAccount.value)
      }

    "returns an AccountsServicePermissionDeniedException when the getAccounts throw a SecurityException" in
      new AccountsServicesImplScope {

        accountManager.getAccounts throws securityException

        val result = accountsServices.getAccounts(None).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesPermissionException]]

        there was one(accountManager).getAccounts
      }

    "returns an AccountsServiceException when the getAccountsByType throw a RuntimeException" in
      new AccountsServicesImplScope {

        accountManager.getAccountsByType(any) throws runtimeException

        val result = accountsServices.getAccounts(Some(GoogleAccount)).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesException]]

        there was one(accountManager).getAccountsByType(GoogleAccount.value)
      }

    "returns an AccountsServicePermissionDeniedException when the getAccounts throw a RuntimeException" in
      new AccountsServicesImplScope {

        accountManager.getAccounts throws runtimeException

        val result = accountsServices.getAccounts(None).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesException]]

        there was one(accountManager).getAccounts
      }
  }

  "getAuthToken" should {

    "returns the accounts sequence and call with the right account and scope" in
      new AccountsServicesImplScope {

        activityContextWrapper.original returns new WeakReference[Activity](activity)
        accountManagerFuture.getResult returns bundle
        bundle.getString(any) returns authToken

        val result = accountsServices.getAuthToken(account1, scope).value.run
        result shouldEqual Xor.Right(authToken)

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

    "returns an AccountsServiceException when the activity is null" in
      new AccountsServicesImplScope {

        activityContextWrapper.original returns new WeakReference[Activity](javaNull)

        val result = accountsServices.getAuthToken(account1, scope).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesException]]
      }

    "returns an AccountsServiceException when the token is null" in
      new AccountsServicesImplScope {

        activityContextWrapper.original returns new WeakReference[Activity](activity)
        accountManagerFuture.getResult returns bundle
        bundle.getString(any) returns javaNull

        val result = accountsServices.getAuthToken(account1, scope).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesException]]

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

    "returns an AccountsServicesOperationCancelledException when the service throw an OperationCanceledException" in
      new AccountsServicesImplScope {

        activityContextWrapper.original returns new WeakReference[Activity](activity)
        accountManagerFuture.getResult throws operationCancelledException

        val result = accountsServices.getAuthToken(account1, scope).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesOperationCancelledException]]

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

    "returns an AccountsServiceException when the service throw a RuntimeException" in
      new AccountsServicesImplScope {

        activityContextWrapper.original returns new WeakReference[Activity](activity)
        accountManagerFuture.getResult throws runtimeException

        val result = accountsServices.getAuthToken(account1, scope).value.run
        result must beAnInstanceOf[Xor.Left[AccountsServicesException]]

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

  }

  "invalidateToken" should {

    "call invalidateToken in AccountManager with the right parameters" in
      new AccountsServicesImplScope {

        val result = accountsServices.invalidateToken(account1.accountType, authToken).value.run
        result shouldEqual Xor.Right(())

        there was one(accountManager).invalidateAuthToken(account1.accountType, authToken)
      }

  }

}

