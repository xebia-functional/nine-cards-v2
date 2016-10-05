package cards.nine.process.device.impl

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Call, Contact}
import cards.nine.process.device._
import cards.nine.process.device.models.LastCallsContact
import cards.nine.services.calls.CallsServicesPermissionException
import cats.syntax.either._
import monix.eval.Task

trait LastCallsDeviceProcessImpl extends DeviceProcess {
  self: DeviceConversions
  with DeviceProcessDependencies
  with ImplicitsDeviceException =>

  def getLastCalls(implicit context: ContextSupport) = {

    def simpleGroupCalls(lastCalls: Seq[Call]): TaskService[Seq[LastCallsContact]] = TaskService {
      Task {
        Either.right {
          (lastCalls groupBy (_.number) map { case (k, v) => toSimpleLastCallsContact(k, v) }).toSeq
        }
      }
    }

    def combineContact(lastCallsContact: LastCallsContact): TaskService[(LastCallsContact, Option[Contact])] =
      for {
        contact <- contactsServices.fetchContactByPhoneNumber(lastCallsContact.number)
      } yield (lastCallsContact, contact)

    def getCombinedContacts(items: Seq[LastCallsContact]):
    TaskService[Seq[(LastCallsContact, Option[Contact])]] = TaskService {
      val tasks = items map (item => combineContact(item).value)
      Task.gatherUnordered(tasks) map { list =>
        Either.right(list.collect { case Right(combinedContact) => combinedContact })
      }
    }

    def fillCombinedContacts(combinedContacts: Seq[(LastCallsContact, Option[Contact])]): Seq[LastCallsContact] =
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
      case e => CallException(e.getMessage, Option(e))
    }

    for {
      lastCalls <- callsServices.getLastCalls.leftMap(mapServicesException)
      simpleGroupCalls <- simpleGroupCalls(lastCalls)
      combinedContacts <- getCombinedContacts(simpleGroupCalls)
    } yield fillCombinedContacts(combinedContacts)
  }

}
