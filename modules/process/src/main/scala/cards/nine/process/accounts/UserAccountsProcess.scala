package cards.nine.process.accounts

import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{AppPermission, PermissionResult}

trait UserAccountsProcess {

  /**
    * Get the Google accounts in the device.
    *
    * @return a sequence of accounts
    * @throws UserAccountsProcessPermissionException if the user didn't grant permission for reading the accounts
    * @throws UserAccountsProcessException           if the service found a problem getting the accounts
    */
  def getGoogleAccounts(implicit contextSupport: ContextSupport): TaskService[Seq[String]]

  /**
    * Get the auth token associated to the specified account and token
    *
    * @param accountName the account email
    * @param scope the scope
    * @return the token
    * @throws UserAccountsProcessOperationCancelledException if the user cancelled the token request
    * @throws UserAccountsProcessException                   if the service found a problem getting the token
    */
  def getAuthToken(accountName: String, scope: String)(implicit contextSupport: ActivityContextSupport): TaskService[String]

  /**
    * Invalidates the token
    *
    * @param token the token to invalidate
    * @throws UserAccountsProcessException if the service found a problem invalidating the token
    */
  def invalidateToken(token: String)(implicit contextSupport: ContextSupport): TaskService[Unit]

  /**
    * Verifies if the app has the permission
    *
    * @param permission the permission
    * @return a PermissionResult indicating the status of the permission
    * @throws UserAccountsProcessException  if the service found a problem checking the permission
    */
  def havePermission(permission: AppPermission)(implicit contextSupport: ContextSupport): TaskService[PermissionResult]

  /**
    * Verifies if the app has some permissions
    *
    * @param permissions the permissions
    * @return a Seq[PermissionResult] indicating the status of each permission
    * @throws UserAccountsProcessException  if the service found a problem checking the permission
    */
  def havePermissions(permissions: Seq[AppPermission])(implicit contextSupport: ContextSupport): TaskService[Seq[PermissionResult]]

  /**
    * Check if we should ask for the permission
    *
    * @param permission the permission
    * @return a PermissionResult indicating the status of the request
    * @throws UserAccountsProcessException  if the service found a problem
    */
  def shouldRequestPermission(permission: AppPermission)(implicit contextSupport: ActivityContextSupport): TaskService[PermissionResult]

  /**
    * Check if we should ask for the permissions
    *
    * @param permissions the permissions
    * @return a Seq[PermissionResult] indicating the status of the request
    * @throws UserAccountsProcessException  if the service found a problem
    */
  def shouldRequestPermissions(permissions: Seq[AppPermission])(implicit contextSupport: ActivityContextSupport): TaskService[Seq[PermissionResult]]

  /**
    * Try to request the permission with the Activity passed inside the contextSupport
    *
    * @param requestCode the permission request code
    * @param permission the permission
    * @throws UserAccountsProcessException  if the service found a problem
    */
  def requestPermission(requestCode: Int, permission: AppPermission)(implicit contextSupport: ActivityContextSupport): TaskService[Unit]

  /**
    * Try to request the permissions with the Activity passed inside the contextSupport
    *
    * @param requestCode the permission request code
    * @param permissions the permissions
    * @throws UserAccountsProcessException  if the service found a problem
    */
  def requestPermissions(requestCode: Int, permissions: Seq[AppPermission])(implicit contextSupport: ActivityContextSupport): TaskService[Unit]

  /**
    * Parses the response of the permission request
    *
    * @param permissions the permissions names
    * @param grantResults the status received from the request
    * @return a Seq[PermissionResult] indicating the status of the request
    * @throws UserAccountsProcessException  if the service found a problem
    */
  def parsePermissionsRequestResult(permissions: Array[String], grantResults: Array[Int]): TaskService[Seq[PermissionResult]]

}
