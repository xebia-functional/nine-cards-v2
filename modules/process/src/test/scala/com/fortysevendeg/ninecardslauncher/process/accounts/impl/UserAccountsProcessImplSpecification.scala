package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import android.accounts.{AccountManager, AccountManagerFuture, OperationCanceledException}
import android.app.Activity
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceSpecification
import com.fortysevendeg.ninecardslauncher.process.accounts.{UserAccountsProcessException, UserAccountsProcessOperationCancelledException, UserAccountsProcessPermissionException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait UserAccountsProcessImplSpecification
  extends TaskServiceSpecification
    with Mockito
    with UserAccountsProcessImplData {

  trait UserAccountsProcessImplScope
    extends Scope {

    implicit val activityContextSupport = mock[ActivityContextSupport]

    val activity = mock[Activity]

    val accountManager = mock[AccountManager]

    activityContextSupport.getAccountManager returns accountManager

    val accountManagerFuture = mock[AccountManagerFuture[Bundle]]

    accountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any) returns accountManagerFuture

    val bundle = mock[Bundle]

    val securityException = new SecurityException("")

    val runtimeException = new RuntimeException("")

    val operationCancelledException = new OperationCanceledException("")

    val accountsServices = new UserAccountsProcessImpl

  }

}

class UserAccountsProcessImplSpec
  extends UserAccountsProcessImplSpecification {

  "getGoogleAccounts" should {

    "return the accounts sequence and call with the right account type" in
      new UserAccountsProcessImplScope {

        accountManager.getAccountsByType(any) returns Array(androidAccount1, androidAccount2)

        val result = accountsServices.getGoogleAccounts(activityContextSupport).run
        result shouldEqual Right(Seq(androidAccount1.name, androidAccount2.name))

        there was one(accountManager).getAccountsByType(accountType)
      }

    "return an empty sequence and call with the right account type when account manager returns null" in
      new UserAccountsProcessImplScope {

        accountManager.getAccountsByType(any) returns javaNull

        val result = accountsServices.getGoogleAccounts(activityContextSupport).run
        result shouldEqual Right(Seq.empty)

        there was one(accountManager).getAccountsByType(accountType)
      }

    "return an UserAccountsProcessPermissionException when the getAccountsByType throw a SecurityException" in
      new UserAccountsProcessImplScope {

        accountManager.getAccountsByType(any) throws securityException

        accountsServices.getGoogleAccounts(activityContextSupport).mustLeft[UserAccountsProcessPermissionException]

        there was one(accountManager).getAccountsByType(accountType)
      }

    "return an UserAccountsProcessException when the getAccountsByType throw a RuntimeException" in
      new UserAccountsProcessImplScope {

        accountManager.getAccountsByType(any) throws runtimeException

        accountsServices.getGoogleAccounts(activityContextSupport).mustLeft[UserAccountsProcessException]

        there was one(accountManager).getAccountsByType(accountType)
      }

  }

  "getAuthToken" should {

    "return the accounts sequence and call with the right account and scope" in
      new UserAccountsProcessImplScope {

        activityContextSupport.getActivity returns Some(activity)
        accountManagerFuture.getResult returns bundle
        bundle.getString(any) returns authToken

        val result = accountsServices.getAuthToken(accountName1, scope).run
        result shouldEqual Right(authToken)

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

    "return an UserAccountsProcessException when the activity is null" in
      new UserAccountsProcessImplScope {

        activityContextSupport.getActivity returns None

        accountsServices.getAuthToken(accountName1, scope).mustLeft[UserAccountsProcessException]
      }

    "return an UserAccountsProcessException when the token is null" in
      new UserAccountsProcessImplScope {

        activityContextSupport.getActivity returns Some(activity)
        accountManagerFuture.getResult returns bundle
        bundle.getString(any) returns javaNull

        accountsServices.getAuthToken(accountName1, scope).mustLeft[UserAccountsProcessException]

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

    "return an UserAccountsProcessOperationCancelledException when the service throw an OperationCanceledException" in
      new UserAccountsProcessImplScope {

        activityContextSupport.getActivity returns Some(activity)
        accountManagerFuture.getResult throws operationCancelledException

        accountsServices.getAuthToken(accountName1, scope).mustLeft[UserAccountsProcessOperationCancelledException]

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

    "return an UserAccountsProcessException when the service throw a RuntimeException" in
      new UserAccountsProcessImplScope {

        activityContextSupport.getActivity returns Some(activity)
        accountManagerFuture.getResult throws runtimeException

        accountsServices.getAuthToken(accountName1, scope).mustLeft[UserAccountsProcessException]

        there was one(accountManager).getAuthToken(androidAccount1, scope, javaNull, activity, javaNull, javaNull)
      }

  }

  "invalidateToken" should {

    "call invalidateToken in AccountManager with the right parameters" in
      new UserAccountsProcessImplScope {

        val result = accountsServices.invalidateToken(authToken).run
        result shouldEqual Right(())

        there was one(accountManager).invalidateAuthToken(androidAccount1.`type`, authToken)
      }

  }

}