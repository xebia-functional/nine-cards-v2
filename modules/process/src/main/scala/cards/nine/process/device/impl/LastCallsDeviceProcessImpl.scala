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

package cards.nine.process.device.impl

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Call, Contact}
import cards.nine.process.device._
import cards.nine.models.LastCallsContact
import cards.nine.services.calls.CallsServicesPermissionException
import cats.syntax.either._
import monix.eval.Task

trait LastCallsDeviceProcessImpl extends DeviceProcess {
  self: DeviceProcessDependencies with ImplicitsDeviceException =>

  def getLastCalls(implicit context: ContextSupport) = {

    def simpleGroupCalls(lastCalls: Seq[Call]): TaskService[Seq[LastCallsContact]] = TaskService {
      Task {
        Either.right {

          val defaultDate = 0L

          def toSimpleLastCallsContact(number: String, calls: Seq[Call]): LastCallsContact = {
            val (hasContact, name, date) = calls.headOption map { call =>
              (call.name.isDefined, call.name getOrElse number, call.date)
            } getOrElse (false, number, defaultDate)
            LastCallsContact(
              hasContact = hasContact,
              number = number,
              title = name,
              lastCallDate = date,
              calls = calls)
          }

          (lastCalls groupBy (_.number) map { case (k, v) => toSimpleLastCallsContact(k, v) }).toSeq
        }
      }
    }

    def combineContact(
        lastCallsContact: LastCallsContact): TaskService[(LastCallsContact, Option[Contact])] =
      for {
        contact <- contactsServices.fetchContactByPhoneNumber(lastCallsContact.number)
      } yield (lastCallsContact, contact)

    def getCombinedContacts(
        items: Seq[LastCallsContact]): TaskService[Seq[(LastCallsContact, Option[Contact])]] =
      TaskService {
        val tasks = items map (item => combineContact(item).value)
        Task.gatherUnordered(tasks) map { list =>
          Either.right(list.collect { case Right(combinedContact) => combinedContact })
        }
      }

    def fillCombinedContacts(
        combinedContacts: Seq[(LastCallsContact, Option[Contact])]): Seq[LastCallsContact] =
      (combinedContacts map { combinedContact =>
        val (lastCallsContact, maybeContact) = combinedContact
        maybeContact map { contact =>
          lastCallsContact.copy(
            lookupKey = Some(contact.lookupKey),
            photoUri = Some(contact.photoUri)
          )
        } getOrElse lastCallsContact
      }).sortWith(_.lastCallDate > _.lastCallDate)

    def mapServicesException[E >: NineCardException]: (NineCardException => E) = {
      case e: CallsServicesPermissionException => CallPermissionException(e.message, Some(e))
      case e                                   => CallException(e.getMessage, Option(e))
    }

    for {
      lastCalls        <- callsServices.getLastCalls.leftMap(mapServicesException)
      simpleGroupCalls <- simpleGroupCalls(lastCalls)
      combinedContacts <- getCombinedContacts(simpleGroupCalls)
    } yield fillCombinedContacts(combinedContacts)
  }

}
