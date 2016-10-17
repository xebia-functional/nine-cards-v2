package cards.nine.app.ui.collections.actions.contacts

import cards.nine.app.permissions.PermissionChecker.ReadContacts
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.adapters.contacts.ContactsAdapter
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{RequestCodes, UiContext}
import cards.nine.app.ui.components.commons.SelectedItemDecoration
import cards.nine.app.ui.components.layouts.snails.TabsSnails._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TabsViewTweaks._
import cards.nine.app.ui.components.layouts.{PullToTabsListener, TabInfo}
import cards.nine.app.ui.preferences.commons.{AppDrawerSelectItemsInScroller}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Contact
import cards.nine.process.device.models.{IterableContacts, TermCounter}
import cards.nine.process.device.{AllContacts, ContactsFilter, FavoriteContacts}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

trait ContactsUiActions
  extends Styles {

  self: BaseActionFragment with ContactsDOM with ContactsUiListener =>

  val resistance = 2.4f
  
  lazy val contactsTabs = Seq(
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.contacts_alphabetical)),
    TabInfo(R.drawable.app_drawer_filter_favorites, getString(R.string.contacts_favorites)))

  def initialize(): TaskService[Unit] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue
    ((scrollerLayout <~ scrollableStyle(colorPrimary)) ~
      (toolbar <~
        dtbInit(colorPrimary) <~
        dtvInflateMenu(R.menu.contact_dialog_menu) <~
        dtvOnMenuItemClickListener(onItem = {
          case R.id.action_filter if (pullToTabsView ~> pdvIsEnabled()).get =>
            swapFilter()
            true
          case _ => false
        }) <~
        dtbChangeText(R.string.allContacts) <~
        dtbNavigationOnClickListener((_) => unreveal())) ~
      (pullToTabsView <~
        ptvLinkTabs(
          tabs = Some(tabs),
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(contactsTabs, 0, Some(colorPrimary)) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            loadContacts(if (pos == 0) AllContacts else FavoriteContacts)
          }
        ))) ~
      (recycler <~ recyclerStyle <~ (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (tabs <~ tvClose)).toService
  }

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone) ~ (pullToTabsView <~ pdvEnable(false)) ~ hideError).toService

  def openTabs(): TaskService[Unit] = ((tabs <~ tvOpen <~ showTabs) ~ (recycler <~ hideList)).toService

  def closeTabs(): TaskService[Unit] =((tabs <~ tvClose <~ hideTabs) ~ (recycler <~ showList)).toService

  def destroy(): TaskService[Unit] = Ui {
    getAdapter foreach(_.close())
  }.toService

  def showContacts(
    filter: ContactsFilter,
    contacts: IterableContacts,
    counters: Seq[TermCounter],
    reload: Boolean): TaskService[Unit] = {
    (if (reload) {
      reloadContactsAdapter(contacts, counters, filter)
    } else {
      generateContactsAdapter(contacts, counters, contact => showContact(contact.lookupKey))
    }).toService
  }

  def askForContactsPermission(requestCode: Int): TaskService[Unit] = Ui {
    requestPermissions(Array(ReadContacts.value), requestCode)
  }.toService

  def showError(): TaskService[Unit] = showGeneralError().toService

  def showErrorContactsPermission(): TaskService[Unit] =
    ((recycler <~ vGone) ~
      (pullToTabsView <~ pdvEnable(false)) ~
      showMessageInScreen(R.string.errorContactsPermission, error = true, action = loadContacts(filter = AllContacts))).toService

  def showErrorLoadingContactsInScreen(filter: ContactsFilter): TaskService[Unit] =
    ((recycler <~ vGone) ~
      (pullToTabsView <~ pdvEnable(false)) ~
      showMessageInScreen(R.string.errorLoadingContacts, error = true, action = loadContacts(filter))).toService

  def showSelectContactDialog(contact: Contact): TaskService[Unit] = {
    val dialog = SelectInfoContactDialogFragment(contact)
    dialog.setTargetFragment(this, RequestCodes.selectInfoContact)
    Ui(showDialog(dialog)).toService
  }

  def close(): TaskService[Unit] = unreveal().toService

  def isTabsOpened: TaskService[Boolean] = TaskService.right((tabs ~> isOpened).get)

  private[this] def showGeneralError(): Ui[Any] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  private[this] def showData: Ui[Any] = (loading <~ vGone) ~ (recycler <~ vVisible) ~ (pullToTabsView <~ pdvEnable(true))

  private[this] def generateContactsAdapter(contacts: IterableContacts, counters: Seq[TermCounter], clickListener: (Contact) => Unit)
    (implicit uiContext: UiContext[_]): Ui[Any] = {
    val adapter = ContactsAdapter(contacts, clickListener, None)
    showData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler(recycler) <~ fslCounters(counters))
  }

  private[this] def reloadContactsAdapter(contacts: IterableContacts, counters: Seq[TermCounter], filter: ContactsFilter)
    (implicit uiContext: UiContext[_]): Ui[Any] = {
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.swapIterator(contacts)) ~
          (toolbar <~ dtbChangeText(filter match {
            case FavoriteContacts => R.string.favoriteContacts
            case _ => R.string.allContacts
          })) ~
          (scrollerLayout <~ fslReset <~ fslCounters(counters)) ~
          (recycler <~ rvScrollToTop)
      } getOrElse showGeneralError)
  }

}
