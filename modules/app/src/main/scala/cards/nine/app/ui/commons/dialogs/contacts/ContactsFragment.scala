package cards.nine.app.ui.commons.dialogs.contacts

import android.app.{Activity, Dialog}
import android.content.Intent
import android.os.Bundle
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{JobException, RequestCodes}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.CardData
import cards.nine.process.device.ContactPermissionException
import com.fortysevendeg.ninecardslauncher.R

class ContactsFragment(implicit groupCollectionsJobs: GroupCollectionsJobs, singleCollectionJobs: Option[SingleCollectionJobs])
  extends BaseActionFragment
  with ContactsUiActions
  with ContactsDOM
  with ContactsUiListener
  with AppNineCardsIntentConversions { self =>

  lazy val contactsJobs = new ContactsJobs(self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
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
            case Some(card: CardData) => Some(card)
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

  override def loadContacts(): Unit =
    contactsJobs.loadContacts().resolveAsyncServiceOr(e => onError(e))

  def loadContactsByKeyword(keyword: String): Unit =
    contactsJobs.loadContacts(Option(keyword)).resolveAsyncServiceOr(e => onError(e))

  override def showContact(lookupKey: String): Unit =
    contactsJobs.showContact(lookupKey).resolveAsyncServiceOr(_ => contactsJobs.showError())

  private[this] def onError(e: Throwable) = e match {
    case e: ContactPermissionException => contactsJobs.askForContactsPermission(RequestCodes.contactsPermission)
    case _ => contactsJobs.showErrorLoadingContacts()
  }

}

object ContactsFragment {
  val addCardRequest = "add-card-request"
}


