package cards.nine.app.ui.collections.actions.contacts

import cards.nine.app.permissions.PermissionChecker
import cards.nine.app.permissions.PermissionChecker.ReadContacts
import cards.nine.app.ui.commons.{Jobs, RequestCodes}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.AllContacts
import macroid.ActivityContextWrapper

class ContactsJobs(actions: ContactsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  val permissionChecker = new PermissionChecker

  def initialize(): TaskService[Unit] = for {
    _ <- actions.initialize()
    _ <- loadContacts()
  } yield ()

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadContacts(byKeyword: Option[String] = None): TaskService[Unit] =
    for {
      _  <- actions.showLoading()
      contacts <- byKeyword match {
        case Some(keyword) => di.deviceProcess.getIterableContactsByKeyWord(keyword)
        case _ => di.deviceProcess.getIterableContacts(AllContacts)
      }
      _ <- actions.showContacts(contacts)
    } yield ()

  def askForContactsPermission(requestCode: Int): TaskService[Unit] = actions.askForContactsPermission(requestCode)

  def showContact(lookupKey: String): TaskService[Unit] =
    for {
      contact <- di.deviceProcess.getContact(lookupKey)
      _ <- actions.showSelectContactDialog(contact)
    } yield ()

  def requestPermissionsResult(
    requestCode: Int,
    permissions: Array[String],
    grantResults: Array[Int]): TaskService[Unit] =
    if (requestCode == RequestCodes.contactsPermission) {
      for {
        result <- permissionChecker.readPermissionRequestResultTask(permissions, grantResults)
        hasPermission = result.exists(_.hasPermission(ReadContacts))
        _ <- if (hasPermission) loadContacts() else actions.showErrorContactsPermission()
      } yield ()
    } else {
      TaskService.empty
    }

  def showError(): TaskService[Unit] = actions.showError()

  def showErrorLoadingContacts(): TaskService[Unit] = actions.showErrorLoadingContactsInScreen()

  def close(): TaskService[Unit] = actions.close()

}