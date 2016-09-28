package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker.ReadContacts
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Jobs, RequestCodes}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.device.models.{IterableContacts, TermCounter}
import com.fortysevendeg.ninecardslauncher.process.device.{AllContacts, ContactsFilter}
import macroid.ActivityContextWrapper

class ContactsJobs(actions: ContactsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  val permissionChecker = new PermissionChecker

  def initialize(): TaskService[Unit] = for {
    _ <- actions.initialize()
    _ <- loadContacts(AllContacts, reload = false)
  } yield ()

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadContacts(
    filter: ContactsFilter,
    reload: Boolean = true): TaskService[Unit] = {

    def getLoadContacts(order: ContactsFilter): TaskService[(IterableContacts, Seq[TermCounter])] =
      for {
        iterableContacts <- di.deviceProcess.getIterableContacts(order)
        counters <- di.deviceProcess.getTermCountersForContacts(order)
      } yield (iterableContacts, counters)

    for {
      _  <- actions.showLoading()
      data <- getLoadContacts(filter)
      (contacts, counters) = data
      _ <- actions.showContacts(filter, contacts, counters, reload)
      isTabsOpened <- actions.isTabsOpened
      _ <-  actions.closeTabs().resolveIf(isTabsOpened, ())
    } yield ()
  }

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
        _ <- if (hasPermission) loadContacts(AllContacts, reload = false) else actions.showErrorContactsPermission()
      } yield ()
    } else {
      TaskService.empty
    }

  def showError(): TaskService[Unit] = actions.showError()

  def showErrorLoadingContacts(filter: ContactsFilter): TaskService[Unit] =
    actions.showErrorLoadingContactsInScreen(filter)

  def close(): TaskService[Unit] = actions.close()

  def swapFilter(): TaskService[Unit] =
    for {
      isTabsOpened <- actions.isTabsOpened
      _ <- if (isTabsOpened) actions.closeTabs() else actions.openTabs()
    } yield ()

}