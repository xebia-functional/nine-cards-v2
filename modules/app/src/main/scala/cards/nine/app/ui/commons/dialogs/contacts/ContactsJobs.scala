/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons.dialogs.contacts

import cards.nine.app.permissions.PermissionChecker
import cards.nine.app.permissions.PermissionChecker.ReadContacts
import cards.nine.app.ui.commons.{Jobs, RequestCodes}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.AllContacts
import macroid.ActivityContextWrapper

class ContactsJobs(actions: ContactsUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs {

  val permissionChecker = new PermissionChecker

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadContacts()
    } yield ()

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadContacts(byKeyword: Option[String] = None): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      contacts <- byKeyword match {
        case Some(keyword) =>
          di.deviceProcess.getIterableContactsByKeyWord(keyword)
        case _ => di.deviceProcess.getIterableContacts(AllContacts)
      }
      _ <- actions.showContacts(contacts)
    } yield ()

  def askForContactsPermission(requestCode: Int): TaskService[Unit] =
    actions.askForContactsPermission(requestCode)

  def showContact(lookupKey: String): TaskService[Unit] =
    for {
      _       <- di.trackEventProcess.addContactByFab()
      contact <- di.deviceProcess.getContact(lookupKey)
      _       <- actions.showSelectContactDialog(contact)
    } yield ()

  def requestPermissionsResult(
      requestCode: Int,
      permissions: Array[String],
      grantResults: Array[Int]): TaskService[Unit] =
    if (requestCode == RequestCodes.contactsPermission) {
      for {
        result <- permissionChecker.readPermissionRequestResultTask(permissions, grantResults)
        hasPermission = result.exists(_.hasPermission(ReadContacts))
        _ <- if (hasPermission) loadContacts()
        else actions.showErrorContactsPermission()
      } yield ()
    } else {
      TaskService.empty
    }

  def showError(): TaskService[Unit] = actions.showError()

  def showErrorLoadingContacts(): TaskService[Unit] =
    actions.showErrorLoadingContactsInScreen()

  def close(): TaskService[Unit] = actions.close()

}
