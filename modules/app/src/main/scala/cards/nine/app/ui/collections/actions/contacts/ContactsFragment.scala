package cards.nine.app.ui.collections.actions.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view._
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{JobException, RequestCodes}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.collection.AddCardRequest
import cards.nine.process.device.{AllContacts, ContactPermissionException, ContactsFilter}
import com.fortysevendeg.ninecardslauncher2.R

class ContactsFragment(implicit groupCollectionsJobs: GroupCollectionsJobs, singleCollectionJobs: Option[SingleCollectionJobs])
  extends BaseActionFragment
  with ContactsUiActions
  with ContactsDOM
  with ContactsUiListener
  with AppNineCardsIntentConversions { self =>

  lazy val contactsJobs = new ContactsJobs(self)

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    contactsJobs.initialize().resolveAsyncServiceOr(e => onError(e))
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)

    def readExtras = Option(data) flatMap (d => Option(d.getExtras))

    def readExtraProperty(extras: Bundle, extra: String): Option[AnyRef] =
      if (extras.containsKey(ContactsFragment.addCardRequest)) {
        Option(extras.get(ContactsFragment.addCardRequest))
      } else {
        None
      }

    (requestCode, resultCode) match {
      case (RequestCodes.selectInfoContact, Activity.RESULT_OK) =>
        val maybeRequest = readExtras flatMap { extras =>
          readExtraProperty(extras, ContactsFragment.addCardRequest) match {
            case Some(card: AddCardRequest) => Some(card)
            case _ => None
          }
        }
        (for {
          cards <- maybeRequest match {
            case Some(request) => groupCollectionsJobs.addCards(Seq(request))
            case _ => TaskService.left(JobException("Request not found"))
          }
          _ <- singleCollectionJobs match {
            case Some(job) => job.addCards(cards)
            case _ => TaskService.empty
          }
          _ <- contactsJobs.close()
        } yield ()).resolveAsyncServiceOr(_ => contactsJobs.showError())
      case _ =>
    }
  }

  override def onRequestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): Unit =
    contactsJobs.requestPermissionsResult(requestCode, permissions, grantResults).resolveAsyncServiceOr(e => onError(e))

  override def onDestroy(): Unit = {
    contactsJobs.destroy()
    super.onDestroy()
  }

  override def loadContacts(filter: ContactsFilter, reload: Boolean): Unit =
    contactsJobs.loadContacts(filter, reload).resolveAsyncServiceOr(e => onError(e, filter))

  override def showContact(lookupKey: String): Unit =
    contactsJobs.showContact(lookupKey).resolveAsyncServiceOr(_ => contactsJobs.showError())

  override def swapFilter(): Unit = contactsJobs.swapFilter().resolveAsync()

  private[this] def onError(e: Throwable, filter: ContactsFilter = AllContacts) = e match {
    case e: ContactPermissionException => contactsJobs.askForContactsPermission(RequestCodes.contactsPermission)
    case _ => contactsJobs.showErrorLoadingContacts(filter)
  }

}

object ContactsFragment {
  val addCardRequest = "add-card-request"
}


