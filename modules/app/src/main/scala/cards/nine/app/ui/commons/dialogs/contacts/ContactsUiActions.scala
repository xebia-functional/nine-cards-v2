package cards.nine.app.ui.commons.dialogs.contacts

import cards.nine.app.permissions.PermissionChecker.ReadContacts
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.commons.adapters.contacts.ContactsAdapter
import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.{Contact, IterableContacts}
import cards.nine.models.types.DialogToolbarSearch
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ViewTweaks._

trait ContactsUiActions
  extends Styles {

  self: BaseActionFragment with ContactsDOM with ContactsUiListener =>

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary, DialogToolbarSearch) <~
      dtbChangeText(R.string.allContacts) <~
      dtbNavigationOnClickListener((_) => hideKeyboard ~ unreveal()) <~
      dtbOnSearchTextChangedListener((text: String, start: Int, before: Int, count: Int) => {
        loadContactsByKeyword(text)
      })) ~
      (recycler <~ recyclerStyle)).toService()

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone) ~ hideError).toService()

  def destroy(): TaskService[Unit] = Ui {
    getAdapter foreach(_.close())
  }.toService()

  def showContacts(contacts: IterableContacts): TaskService[Unit] =
    ((getAdapter match {
      case Some(adapter) =>
        showData ~ Ui(adapter.swapIterator(contacts)) ~ (recycler <~ rvScrollToTop)
      case _ =>
        val adapter = ContactsAdapter(contacts, contact => showContact(contact.lookupKey), None)
        showData ~
          (recycler <~
            rvLayoutManager(adapter.getLayoutManager) <~
            rvAdapter(adapter))
    }) ~ (loading <~ vGone)).toService()

  def askForContactsPermission(requestCode: Int): TaskService[Unit] = Ui {
    requestPermissions(Array(ReadContacts.value), requestCode)
  }.toService()

  def showError(): TaskService[Unit] = showGeneralError().toService()

  def showErrorContactsPermission(): TaskService[Unit] =
    ((recycler <~ vGone) ~
      showMessageInScreen(R.string.errorContactsPermission, error = true, action = loadContacts())).toService()

  def showErrorLoadingContactsInScreen(): TaskService[Unit] =
    ((recycler <~ vGone) ~
      showMessageInScreen(R.string.errorLoadingContacts, error = true, action = loadContacts())).toService()

  def showSelectContactDialog(contact: Contact): TaskService[Unit] = {
    val dialog = SelectInfoContactDialogFragment(contact)
    dialog.setTargetFragment(this, RequestCodes.selectInfoContact)
    Ui(showDialog(dialog)).toService()
  }

  def close(): TaskService[Unit] = unreveal().toService()

  private[this] def showGeneralError(): Ui[Any] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  private[this] def showData: Ui[Any] = (loading <~ vGone) ~ (recycler <~ vVisible)

  private[this] def hideKeyboard: Ui[Any] = toolbar <~ dtbHideKeyboardSearchText

}
