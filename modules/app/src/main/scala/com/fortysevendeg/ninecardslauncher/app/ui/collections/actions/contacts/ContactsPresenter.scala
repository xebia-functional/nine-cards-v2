package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableContacts, TermCounter}
import com.fortysevendeg.ninecardslauncher.process.device.{AllContacts, ContactException, ContactsFilter}
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class ContactsPresenter(actions: ContactsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Presenter {

  def initialize(): Unit = {
    actions.initialize().run
    loadContacts(AllContacts, reload = false)
  }

  def destroy(): Unit = actions.destroy().run

  def loadContacts(
    filter: ContactsFilter,
    reload: Boolean = true): Unit = Task.fork(getLoadContacts(filter).value).resolveAsyncUi(
    onPreTask = () => actions.showLoading(),
    onResult = {
      case (contacts: IterableContacts, counters: Seq[TermCounter]) =>
        actions.showContacts(filter, contacts, counters, reload) ~
          (if (actions.isTabsOpened) actions.closeTabs() else Ui.nop)
    },
    onException = (ex: Throwable) => actions.showErrorLoadingContactsInScreen(filter)
  )

  def showContact(lookupKey: String): Unit = Task.fork(di.deviceProcess.getContact(lookupKey).value).resolveAsyncUi(
    onResult = actions.showDialog,
    onException = (ex: Throwable) => actions.showGeneralError()
  )

  def addContact(maybeContact: Option[AddCardRequest]): Unit =
    (maybeContact map actions.contactAdded getOrElse actions.showGeneralError()).run

  private[this] def getLoadContacts(order: ContactsFilter): TaskService[(IterableContacts, Seq[TermCounter])] =
    for {
      iterableContacts <- di.deviceProcess.getIterableContacts(order)
      counters <- di.deviceProcess.getTermCountersForContacts(order)
    } yield (iterableContacts, counters)

}

trait ContactsUiActions {

  def initialize(): Ui[Any]

  def destroy(): Ui[Any]

  def showLoading(): Ui[Any]

  def closeTabs(): Ui[Any]

  def showContacts(
    filter: ContactsFilter,
    contacts: IterableContacts,
    counters: Seq[TermCounter],
    reload: Boolean): Ui[Any]

  def showErrorLoadingContactsInScreen(filter: ContactsFilter): Ui[Any]

  def showGeneralError(): Ui[Any]

  def showDialog(contact: Contact): Ui[Any]

  def contactAdded(card: AddCardRequest): Ui[Any]

  def isTabsOpened: Boolean
}