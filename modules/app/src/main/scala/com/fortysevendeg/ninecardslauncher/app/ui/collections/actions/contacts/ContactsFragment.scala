package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view._
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
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

  private[this] def loadContacts(
    filter: ContactsFilter,
    reload: Boolean = false) = Task.fork(di.deviceProcess.getContacts(filter).run).resolveAsyncUi(
    onPreTask = () => showLoading,
    onResult = (contacts: Seq[Contact]) => if (reload) {
      reloadContactsAdapter(contacts, filter)
    } else {
      generateContactsAdapter(contacts, contact => {
        showDialog(contact)
        //        runUi(unreveal())
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
      dialog.show(ft, tagDialog)
    },
    onException = (ex: Throwable) => runUi(showGeneralError)
  )


}


