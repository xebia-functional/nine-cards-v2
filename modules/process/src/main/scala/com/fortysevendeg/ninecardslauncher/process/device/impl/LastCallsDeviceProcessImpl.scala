package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.{CatchAll, _}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, LastCallsContact}
import com.fortysevendeg.ninecardslauncher.process.device.{CallException, DeviceConversions, ImplicitsDeviceException}
import com.fortysevendeg.ninecardslauncher.services.calls.models.Call
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ImplicitsContactsServiceExceptions}
import rapture.core.Answer

import scalaz.concurrent.Task

trait LastCallsDeviceProcessImpl {

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

  private[this] def simpleGroupCalls(lastCalls: Seq[Call]): ServiceDef2[Seq[LastCallsContact], CallException] = Service {
    Task {
      CatchAll[CallException] {
        (lastCalls groupBy (_.number) map { case (k, v) => toSimpleLastCallsContact(k, v) }).toSeq
      }
    }
  }

  private[this] def getCombinedContacts(items: Seq[LastCallsContact]):
  ServiceDef2[Seq[(LastCallsContact, Option[Contact])], ContactsServiceException] = Service {
    val tasks = items map (item => combineContact(item).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[ContactsServiceException](list.collect { case Answer(combinedContact) => combinedContact }))
  }

  private[this] def combineContact(lastCallsContact: LastCallsContact):
  ServiceDef2[(LastCallsContact, Option[Contact]), ContactsServiceException] =
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
