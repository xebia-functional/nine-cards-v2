package com.fortysevendeg.ninecardslauncher.process.device.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, LastCallsContact}
import com.fortysevendeg.ninecardslauncher.process.device.{CallException, DeviceConversions, DeviceProcess, ImplicitsDeviceException}
import com.fortysevendeg.ninecardslauncher.services.calls.models.Call
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ImplicitsContactsServiceExceptions}

import scalaz.concurrent.Task

trait LastCallsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsContactsServiceExceptions =>

  def getLastCalls(implicit context: ContextSupport) =
    (for {
      lastCalls <- callsServices.getLastCalls
      simpleGroupCalls <- simpleGroupCalls(lastCalls)
      combinedContacts <- getCombinedContacts(simpleGroupCalls)
    } yield fillCombinedContacts(combinedContacts)).resolve[CallException]

  private[this] def simpleGroupCalls(lastCalls: Seq[Call]): TaskService[Seq[LastCallsContact]] = TaskService {
    Task {
      XorCatchAll[CallException] {
        (lastCalls groupBy (_.number) map { case (k, v) => toSimpleLastCallsContact(k, v) }).toSeq
      }
    }
  }

  private[this] def getCombinedContacts(items: Seq[LastCallsContact]):
  TaskService[Seq[(LastCallsContact, Option[Contact])]] = TaskService {
    val tasks = items map (item => combineContact(item).value)
    Task.gatherUnordered(tasks) map (list => XorCatchAll[ContactsServiceException](list.collect { case Xor.Right(combinedContact) => combinedContact }))
  }

  private[this] def combineContact(lastCallsContact: LastCallsContact):
  TaskService[(LastCallsContact, Option[Contact])] =
    for {
      contact <- contactsServices.fetchContactByPhoneNumber(lastCallsContact.number)
    } yield (lastCallsContact, contact map toContact)

  private[this] def fillCombinedContacts(combinedContacts: Seq[(LastCallsContact, Option[Contact])]): Seq[LastCallsContact] =
    (combinedContacts map { combinedContact =>
      val (lastCallsContact, maybeContact) = combinedContact
      maybeContact map { contact =>
        lastCallsContact.copy(
          lookupKey = Some(contact.lookupKey),
          photoUri = Some(contact.photoUri)
        )
      } getOrElse lastCallsContact
    }).sortWith(_.lastCallDate > _.lastCallDate)

}
