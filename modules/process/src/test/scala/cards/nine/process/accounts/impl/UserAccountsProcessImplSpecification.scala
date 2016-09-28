package cards.nine.process.accounts.impl

import android.accounts._
import android.app.Activity
import android.os.Bundle
import cards.nine.commons._
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.process.accounts._
import cards.nine.services.permissions.{PermissionDenied, PermissionGranted, PermissionsServices}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait UserAccountsProcessImplSpecification
  extends TaskServiceSpecification
    with Mockito
    with UserAccountsProcessImplData {

  trait UserAccountsProcessImplScope
    extends Scope {

    val contextSupport = mock[ActivityContextSupport]

    val activity = mock[Activity]

    val accountManagerFuture = mock[AccountManagerFuture[Bundle]]

    val mockAccountManager = mock[AccountManager]

    contextSupport.getAccountManager returns mockAccountManager

    val bundle = mock[Bundle]

    val securityException = new SecurityException("")

    val runtimeException = new RuntimeException("")

    val operationCancelledException = new OperationCanceledException("")

    val permissionsServices = mock[PermissionsServices]

    val accountsProcess = new UserAccountsProcessImpl(permissionsServices)

  }

}

class UserAccountsProcessImplSpec
  extends UserAccountsProcessImplSpecification {

  "getGoogleAccounts" should {

    "return the accounts sequence and call with the right account type" in
      new UserAccountsProcessImplScope {

        mockAccountManager.getAccountsByType(any) returns Array(androidAccount1, androidAccount2)

        val result = accountsProcess.getGoogleAccounts(contextSupport).run
        result shouldEqual Right(Seq(androidAccount1.name, androidAccount2.name))

        there was one(mockAccountManager).getAccountsByType(accountType)
      }

    "return an empty sequence and call with the right account type when account manager returns null" in
      new UserAccountsProcessImplScope {

        mockAccountManager.getAccountsByType(any) returns javaNull

        val result = accountsProcess.getGoogleAccounts(contextSupport).run
        result shouldEqual Right(Seq.empty)

        there was one(mockAccountManager).getAccountsByType(accountType)
      }

    "return an UserAccountsProcessPermissionException when the getAccountsByType throw a SecurityException" in
      new UserAccountsProcessImplScope {

        mockAccountManager.getAccountsByType(any) throws securityException

        accountsProcess.getGoogleAccounts(contextSupport).mustLeft[UserAccountsProcessPermissionException]

        there was one(mockAccountManager).getAccountsByType(accountType)
      }

    "return an UserAccountsProcessException when the getAccountsByType throw a RuntimeException" in
      new UserAccountsProcessImplScope {

        mockAccountManager.getAccountsByType(any) throws runtimeException

        accountsProcess.getGoogleAccounts(contextSupport).mustLeft[UserAccountsProcessException]

        there was one(mockAccountManager).getAccountsByType(accountType)
      }

  }

  "getAuthToken" should {

    "return the accounts sequence and call with the right account and scope" in
      new UserAccountsProcessImplScope {

        contextSupport.getActivity returns Some(activity)
        mockAccountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any).answers { (params, mock) =>
          params match {
            case Array(_, _, _, _, callback: AccountManagerCallback[_], _) =>
              callback.asInstanceOf[AccountManagerCallback[Bundle]].run(accountManagerFuture)
              accountManagerFuture
          }
        }
        accountManagerFuture.getResult returns bundle
        bundle.getString(any) returns authToken

        val result = accountsProcess.getAuthToken(accountName1, scope)(contextSupport).run
        result shouldEqual Right(authToken)
      }

    "return an UserAccountsProcessException when the activity is null" in
      new UserAccountsProcessImplScope {

        contextSupport.getActivity returns None

        accountsProcess.getAuthToken(accountName1, scope)(contextSupport).mustLeft[UserAccountsProcessException]
      }

    "return an UserAccountsProcessException when the token is null" in
      new UserAccountsProcessImplScope {

        contextSupport.getActivity returns Some(activity)
        mockAccountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any).answers { (params, mock) =>
          params match {
            case Array(_, _, _, _, callback: AccountManagerCallback[_], _) =>
              callback.asInstanceOf[AccountManagerCallback[Bundle]].run(accountManagerFuture)
              accountManagerFuture
          }
        }
        accountManagerFuture.getResult returns bundle
        bundle.getString(any) returns javaNull

        accountsProcess.getAuthToken(accountName1, scope)(contextSupport).mustLeft[UserAccountsProcessException]
      }

    "return an UserAccountsProcessException when the result is null" in
      new UserAccountsProcessImplScope {

        contextSupport.getActivity returns Some(activity)
        mockAccountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any).answers { (params, mock) =>
          params match {
            case Array(_, _, _, _, callback: AccountManagerCallback[_], _) =>
              callback.asInstanceOf[AccountManagerCallback[Bundle]].run(javaNull)
              javaNull
          }
        }
        accountsProcess.getAuthToken(accountName1, scope)(contextSupport).mustLeft[UserAccountsProcessException]
      }

    "return an UserAccountsProcessOperationCancelledException when the service throw an OperationCanceledException" in
      new UserAccountsProcessImplScope {

        contextSupport.getActivity returns Some(activity)
        mockAccountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any).answers { (params, mock) =>
          params match {
            case Array(_, _, _, _, callback: AccountManagerCallback[_], _) =>
              callback.asInstanceOf[AccountManagerCallback[Bundle]].run(accountManagerFuture)
              accountManagerFuture
          }
        }
        accountManagerFuture.getResult throws operationCancelledException

        accountsProcess.getAuthToken(accountName1, scope)(contextSupport).mustLeft[UserAccountsProcessOperationCancelledException]
      }

    "return an UserAccountsProcessException when the service throw a RuntimeException" in
      new UserAccountsProcessImplScope {

        contextSupport.getActivity returns Some(activity)
        mockAccountManager.getAuthToken(any, any, any[Bundle], any[Activity], any, any).answers { (params, mock) =>
          params match {
            case Array(_, _, _, _, callback: AccountManagerCallback[_], _) =>
              callback.asInstanceOf[AccountManagerCallback[Bundle]].run(accountManagerFuture)
              accountManagerFuture
          }
        }
        accountManagerFuture.getResult throws runtimeException

        accountsProcess.getAuthToken(accountName1, scope)(contextSupport).mustLeft[UserAccountsProcessException]
      }

  }

  "invalidateToken" should {

    "call invalidateToken in AccountManager with the right parameters" in
      new UserAccountsProcessImplScope {

        val result = accountsProcess.invalidateToken(authToken)(contextSupport).run
        result shouldEqual Right(())

        there was one(mockAccountManager).invalidateAuthToken(androidAccount1.`type`, authToken)
      }

  }

  "havePermission" should {

    "return true if the service return PermissionGranted for the specified permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.checkPermissions(any)(any) returns serviceRight(Map(GetAccounts.value -> PermissionGranted))
        val result = accountsProcess.havePermission(GetAccounts)(contextSupport).run

        result shouldEqual Right(PermissionResult(GetAccounts, result = true))
      }

    "return false if the service return PermissionDenied for the specified permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.checkPermissions(any)(any) returns serviceRight(Map(GetAccounts.value -> PermissionDenied))
        val result = accountsProcess.havePermission(GetAccounts)(contextSupport).run

        result shouldEqual Right(PermissionResult(GetAccounts, result = false))
      }

    "return false if the service return PermissionGranted for another permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.checkPermissions(any)(any) returns serviceRight(Map(ReadContacts.value -> PermissionGranted))
        val result = accountsProcess.havePermission(GetAccounts)(contextSupport).run

        result shouldEqual Right(PermissionResult(GetAccounts, result = false))
      }

  }

  "havePermissions" should {

    "return true for all permissions if the service return PermissionGranted for the permissions" in
      new UserAccountsProcessImplScope {

        permissionsServices.checkPermissions(any)(any) returns
          serviceRight(Map(GetAccounts.value -> PermissionGranted, ReadContacts.value -> PermissionGranted))
        val result = accountsProcess.havePermissions(Seq(GetAccounts, ReadContacts))(contextSupport).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = true), PermissionResult(ReadContacts, result = true)))
      }

    "return true or false when the service returns PermissionGranted and PermissionDenied for the specified permissions" in
      new UserAccountsProcessImplScope {

        permissionsServices.checkPermissions(any)(any) returns
          serviceRight(Map(GetAccounts.value -> PermissionGranted, ReadContacts.value -> PermissionDenied))
        val result = accountsProcess.havePermissions(Seq(GetAccounts, ReadContacts))(contextSupport).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = true), PermissionResult(ReadContacts, result = false)))
      }

    "return false for all permissions if the service return PermissionGranted for another permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.checkPermissions(any)(any) returns serviceRight(Map(ReadCallLog.value -> PermissionGranted))
        val result = accountsProcess.havePermissions(Seq(GetAccounts, ReadContacts))(contextSupport).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = false), PermissionResult(ReadContacts, result = false)))
      }

  }

  "shouldRequestPermission" should {

    "return true if the service return true for the specified permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.shouldShowRequestPermissions(any)(any) returns
          serviceRight(Map(GetAccounts.value -> true))
        val result = accountsProcess.shouldRequestPermission(GetAccounts)(contextSupport).run

        result shouldEqual Right(PermissionResult(GetAccounts, result = true))
      }

    "return false if the service return false for the specified permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.shouldShowRequestPermissions(any)(any) returns
          serviceRight(Map(GetAccounts.value -> false))
        val result = accountsProcess.shouldRequestPermission(GetAccounts)(contextSupport).run

        result shouldEqual Right(PermissionResult(GetAccounts, result = false))
      }

    "return false if the service return true for another permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.shouldShowRequestPermissions(any)(any) returns
          serviceRight(Map(ReadContacts.value -> true))
        val result = accountsProcess.shouldRequestPermission(GetAccounts)(contextSupport).run

        result shouldEqual Right(PermissionResult(GetAccounts, result = false))
      }

  }

  "shouldRequestPermissions" should {

    "return true for all permissions if the service return true for the permissions" in
      new UserAccountsProcessImplScope {

        permissionsServices.shouldShowRequestPermissions(any)(any) returns
          serviceRight(Map(GetAccounts.value -> true, ReadContacts.value -> true))
        val result = accountsProcess.shouldRequestPermissions(Seq(GetAccounts, ReadContacts))(contextSupport).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = true), PermissionResult(ReadContacts, result = true)))
      }

    "return true or false when the service returns true and false for the specified permissions" in
      new UserAccountsProcessImplScope {

        permissionsServices.shouldShowRequestPermissions(any)(any) returns
          serviceRight(Map(GetAccounts.value -> true, ReadContacts.value -> false))
        val result = accountsProcess.shouldRequestPermissions(Seq(GetAccounts, ReadContacts))(contextSupport).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = true), PermissionResult(ReadContacts, result = false)))
      }

    "return false for all permissions if the service return true for another permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.shouldShowRequestPermissions(any)(any) returns serviceRight(Map(ReadCallLog.value -> true))
        val result = accountsProcess.shouldRequestPermissions(Seq(GetAccounts, ReadContacts))(contextSupport).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = false), PermissionResult(ReadContacts, result = false)))
      }

  }

  "requestPermission" should {

    "call to the service with the right parameters" in
      new UserAccountsProcessImplScope {

        permissionsServices.requestPermissions(any, any)(any) returns serviceRight((): Unit)
        accountsProcess.requestPermission(permissionCode, GetAccounts)(contextSupport).run
        there was one(permissionsServices).requestPermissions(permissionCode, Seq(GetAccounts.value))(contextSupport)
      }

  }

  "requestPermissions" should {

    "call to the service with the right parameters" in
      new UserAccountsProcessImplScope {

        permissionsServices.requestPermissions(any, any)(any) returns serviceRight((): Unit)
        accountsProcess.requestPermissions(permissionCode, Seq(GetAccounts, ReadContacts))(contextSupport).run
        there was one(permissionsServices).requestPermissions(permissionCode, Seq(GetAccounts.value, ReadContacts.value))(contextSupport)
      }

  }

  "parsePermissionRequestResult" should {

    "return true for all permissions if the service return PermissionGranted for the permissions" in
      new UserAccountsProcessImplScope {

        permissionsServices.readPermissionsRequestResult(any, any) returns
          serviceRight(Map(GetAccounts.value -> PermissionGranted, ReadContacts.value -> PermissionGranted))
        val result = accountsProcess.parsePermissionsRequestResult(Array(GetAccounts.value, ReadContacts.value), Array.empty).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = true), PermissionResult(ReadContacts, result = true)))
      }

    "return true or false when the service returns PermissionGranted and PermissionDenied for the specified permissions" in
      new UserAccountsProcessImplScope {

        permissionsServices.readPermissionsRequestResult(any, any) returns
          serviceRight(Map(GetAccounts.value -> PermissionGranted, ReadContacts.value -> PermissionDenied))
        val result = accountsProcess.parsePermissionsRequestResult(Array(GetAccounts.value, ReadContacts.value), Array.empty).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = true), PermissionResult(ReadContacts, result = false)))
      }

    "return false for all permissions if the service return PermissionGranted for another permission" in
      new UserAccountsProcessImplScope {

        permissionsServices.readPermissionsRequestResult(any, any) returns serviceRight(Map(ReadCallLog.value -> PermissionGranted))
        val result = accountsProcess.parsePermissionsRequestResult(Array(GetAccounts.value, ReadContacts.value), Array.empty).run

        result shouldEqual Right(Seq(PermissionResult(GetAccounts, result = false), PermissionResult(ReadContacts, result = false)))
      }

  }

}