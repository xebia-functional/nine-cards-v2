package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ActivityResult, FragmentUiContext, UiContext}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher.process.device.{AllContacts, ContactsFilter, FavoriteContacts}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

import scalaz.concurrent.Task

class ContactsFragment
  extends BaseActionFragment
  with ContactsComposer
  with NineCardIntentConversions {

  val tagDialog = "dialog"

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi(checked => loadContacts(if (checked) {
      AllContacts
    } else {
      FavoriteContacts
    }, reload = true)))
    loadContacts(AllContacts)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (ActivityResult.selectInfoContact, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) map {
          case extras if extras.containsKey(ContactsFragment.addCardRequest) =>
            extras.get(ContactsFragment.addCardRequest) match {
              case card: AddCardRequest =>
                activity[CollectionsDetailsActivity] foreach (_.addCards(Seq(card)))
                runUi(unreveal())
              case _ => runUi(showGeneralError)
            }
        } getOrElse runUi(showGeneralError)
    }
  }

  private[this] def loadContacts(
    filter: ContactsFilter,
    reload: Boolean = false): Unit = Task.fork(di.deviceProcess.getIterableContacts(filter).run).resolveAsyncUi(
    onPreTask = () => showLoading,
    onResult = (contacts: IterableContacts) => if (reload) {
      reloadContactsAdapter(contacts, filter)
    } else {
      generateContactsAdapter(contacts, contact => {
        showDialog(contact)
      })
    },
    onException = (ex: Throwable) => showError(R.string.errorLoadingContacts, loadContacts(filter, reload))
  )

  private[this] def showDialog(contact: Contact) = Task.fork(di.deviceProcess.getContact(contact.lookupKey).run).resolveAsync(
    onResult = (contact: Contact) => {
      val ft = getFragmentManager.beginTransaction()
      Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(javaNull)
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


