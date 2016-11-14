package cards.nine.app.ui.collections.actions.contacts

import cards.nine.app.permissions.PermissionChecker.ReadContacts
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.adapters.contacts.ContactsAdapter
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{RequestCodes, UiContext}
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Contact
import cards.nine.process.device.models.IterableContacts
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ViewTweaks._

trait ContactsUiActions
  extends Styles {

  self: BaseActionFragment with ContactsDOM with ContactsUiListener =>

  val resistance = 2.4f

  def initialize(): TaskService[Unit] =
    ((toolbar <~
        dtbInit(colorPrimary) <~
        dtbChangeText(R.string.allContacts) <~
        dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone) ~ hideError).toService

  def destroy(): TaskService[Unit] = Ui {
    getAdapter foreach(_.close())
  }.toService

  def showContacts(
    contacts: IterableContacts,
    reload: Boolean): TaskService[Unit] = {

    def generateContactsAdapter(clickListener: (Contact) => Unit)
      (implicit uiContext: UiContext[_]): Ui[Any] = {
      val adapter = ContactsAdapter(contacts, clickListener, None)
      showData ~
        (recycler <~
          rvLayoutManager(adapter.getLayoutManager) <~
          rvAdapter(adapter)) ~
        (loading <~ vGone)
    }

    def reloadContactsAdapter()
      (implicit uiContext: UiContext[_]): Ui[Any] = {
      showData ~
        (getAdapter map { adapter =>
          Ui(adapter.swapIterator(contacts)) ~ (recycler <~ rvScrollToTop)
        } getOrElse showGeneralError)
    }

    (if (reload) {
      reloadContactsAdapter()
    } else {
      generateContactsAdapter( contact => showContact(contact.lookupKey))
    }).toService
  }

  def askForContactsPermission(requestCode: Int): TaskService[Unit] = Ui {
    requestPermissions(Array(ReadContacts.value), requestCode)
  }.toService

  def showError(): TaskService[Unit] = showGeneralError().toService

  def showErrorContactsPermission(): TaskService[Unit] =
    ((recycler <~ vGone) ~
      showMessageInScreen(R.string.errorContactsPermission, error = true, action = loadContacts())).toService

  def showErrorLoadingContactsInScreen(): TaskService[Unit] =
    ((recycler <~ vGone) ~
      showMessageInScreen(R.string.errorLoadingContacts, error = true, action = loadContacts())).toService

  def showSelectContactDialog(contact: Contact): TaskService[Unit] = {
    val dialog = SelectInfoContactDialogFragment(contact)
    dialog.setTargetFragment(this, RequestCodes.selectInfoContact)
    Ui(showDialog(dialog)).toService
  }

  def close(): TaskService[Unit] = unreveal().toService

  private[this] def showGeneralError(): Ui[Any] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  private[this] def showData: Ui[Any] = (loading <~ vGone) ~ (recycler <~ vVisible)


}
