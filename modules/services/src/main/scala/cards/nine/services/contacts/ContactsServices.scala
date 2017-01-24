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

package cards.nine.services.contacts

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.{Contact, IterableCursor, TermCounter}

trait ContactsServices {

  /**
   * Get contacts sort by name. The info field is not filled
   *
   * @return the Seq[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getContacts: TaskService[Seq[Contact]]

  /**
   * Returns the number of times the first letter of a contact is repeated alphabetically
   *
   * @return the Seq[cards.nine.models.TermCounter] contains information about the times is repeated a contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getAlphabeticalCounterContacts: TaskService[Seq[TermCounter]]

  /**
   * Get iterable contacts sort by name. The info field is not filled
   *
   * @return the IterableCursorSeq[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getIterableContacts: TaskService[IterableCursor[Contact]]

  /**
   * Get iterable contacts by keyword sort by name. The info field is not filled
   *
   * @return the IterableCursorSeq[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getIterableContactsByKeyword(keyword: String): TaskService[IterableCursor[Contact]]

  /**
   * Return contact by email if exist. The info field is not filled
   *
   * @return the Option[cards.nine.models.Contact] contains
   *         information about contact
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def fetchContactByEmail(email: String): TaskService[Option[Contact]]

  /**
   * Return contact by phone number if exist. The info field is not filled
   *
   * @return the Option[cards.nine.models.Contact] contains information about contact
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def fetchContactByPhoneNumber(phoneNumber: String): TaskService[Option[Contact]]

  /**
   * Return contact by lookup key. The info field is filled
   *
   * @return the cards.nine.models.Contact contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   * @throws ContactNotFoundException if the lookup key doesn't exits
   */
  def findContactByLookupKey(lookupKey: String): TaskService[Contact]

  /**
   * Populate the info field in every contact
   *
   * @return sequence of the cards.nine.models.Contact
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   * @throws ContactNotFoundException if any contact doesn't exits
   */
  def populateContactInfo(contacts: Seq[Contact]): TaskService[Seq[Contact]]

  /**
   * Return favorite contacts. The info field is not filled
   *
   * @return the Seq[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getFavoriteContacts: TaskService[Seq[Contact]]

  /**
   * Return iterable favorite contacts. The info field is not filled
   *
   * @return the IterableCursor[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getIterableFavoriteContacts: TaskService[IterableCursor[Contact]]

  /**
   * Return contacts with phone number. The info field is not filled
   *
   * @return the Seq[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getContactsWithPhone: TaskService[Seq[Contact]]

  /**
   * Return iterable contacts with phone number. The info field is not filled
   *
   * @return the IterableCursor[cards.nine.models.Contact] contains information about contacts
   * @throws ContactsServicePermissionException if the permission for read contacts hasn't been granted
   * @throws ContactsServiceException if exist some problem accessing to contact provider
   */
  def getIterableContactsWithPhone: TaskService[IterableCursor[Contact]]
}
