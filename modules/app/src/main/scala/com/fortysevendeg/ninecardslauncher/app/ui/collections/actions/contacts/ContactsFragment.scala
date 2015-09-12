package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityResult, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher.process.device.{AllContacts, ContactsFilter, ContactsWithPhoneNumber}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

import scalaz.concurrent.Task

class ContactsFragment
  extends BaseActionFragment
  with ContactsComposer
  with NineCardIntentConversions {

  val tagDialog = "dialog"

  implicit lazy val di: Injector = new Injector

  implicit lazy val fragment: Fragment = this // TODO : javi => We need that, but I don't like. We need a better way

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi(checked => loadContacts(if (checked) {
      ContactsWithPhoneNumber
    } else {
      AllContacts
    }, reload = true)))
    loadContacts(ContactsWithPhoneNumber)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (ActivityResult.selectInfoContact, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(ContactsFragment.addCardRequest) =>
            extras.get(ContactsFragment.addCardRequest) match {
              case card: AddCardRequest =>
                actionsScreenListener foreach (_.addCards(Seq(card)))
                runUi(unreveal())
              case _ => runUi(showGeneralError)
            }
        } getOrElse runUi(showGeneralError)
    }
  }

  private[this] def loadContacts(
    filter: ContactsFilter,
    reload: Boolean = false) = Task.fork(di.deviceProcess.getContacts(filter).run).resolveAsyncUi(
    onPreTask = () => showLoading,
    onResult = (contacts: Seq[Contact]) => if (reload) {
      reloadContactsAdapter(contacts, filter)
    } else {
      generateContactsAdapter(contacts, contact => {
        showDialog(contact)
      })
    },
    onException = (ex: Throwable) => showGeneralError
  )

  private[this] def showDialog(contact: Contact) = Task.fork(di.deviceProcess.getContact(contact.lookupKey).run).resolveAsync(
    onResult = (contact: Contact) => {
      val ft = getFragmentManager.beginTransaction()
      Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(null)
      val dialog = new SelectInfoContactDialogFragment(contact)
      dialog.setTargetFragment(this, ActivityResult.selectInfoContact)
      dialog.show(ft, tagDialog)
    },
    onException = (ex: Throwable) => runUi(showGeneralError)
  )

}

object ContactsFragment {
  val addCardRequest = "add-card-request"
}


