package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{RequestCodes, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.ContactsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{TabInfo, PullToTabsListener}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.device.models.{TermCounter, Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.process.device.{AllContacts, ContactsFilter, FavoriteContacts}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait ContactsIuActionsImpl
  extends ContactsIuActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  implicit val presenter: ContactsPresenter

  val collectionsPresenter: CollectionsPagerPresenter

  val tagDialog = "dialog"

  val resistance = 2.4f

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = Option(findView(TR.action_scroller_layout))

  lazy val pullToTabsView = Option(findView(TR.actions_pull_to_tabs))

  lazy val tabs = Option(findView(TR.actions_tabs))

  lazy val contactsTabs = Seq(
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.contacts_alphabetical)),
    TabInfo(R.drawable.app_drawer_filter_favorites, getString(R.string.contacts_favorites))
  )

  override def initialize(): Ui[_] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtvInflateMenu(R.menu.contact_dialog_menu) <~
      dtvOnMenuItemClickListener(onItem = {
        case R.id.action_filter =>
          (if (isTabsOpened) closeTabs() else openTabs()).run
          true
        case _ => false
      }) <~
      dtbChangeText(R.string.allContacts) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (pullToTabsView <~
        ptvLinkTabs(
          tabs = tabs,
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(contactsTabs, 0, Some(colorPrimary)) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            presenter.loadContacts(if (pos == 0) AllContacts else FavoriteContacts)
          }
        ))) ~
      (recycler <~ recyclerStyle) ~
      (tabs <~ tvClose) ~
      (scrollerLayout <~ fslColor(colorPrimary))

  override def showLoading(): Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone) ~ hideError

  override def closeTabs(): Ui[_] = (tabs <~ tvClose <~ hideTabs) ~ (recycler <~ showList)

  override def destroy(): Ui[Any] = Ui {
    getAdapter foreach(_.close())
  }

  override def showContacts(
    filter: ContactsFilter,
    contacts: IterableContacts,
    counters: Seq[TermCounter],
    reload: Boolean): Ui[Any] = {
    if (reload) {
      reloadContactsAdapter(contacts, counters, filter)
    } else {
      generateContactsAdapter(contacts, counters, contact => presenter.showContact(contact.lookupKey))
    }
  }

  override def showGeneralError(): Ui[_] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  override def showLoadingContactsError(filter: ContactsFilter): Ui[Any] =
    showError(R.string.errorLoadingApps, presenter.loadContacts(filter))

  override def showDialog(contact: Contact): Ui[_] = Ui {
    val ft = getFragmentManager.beginTransaction()
    Option(getFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
    ft.addToBackStack(javaNull)
    val dialog = new SelectInfoContactDialogFragment(contact)
    dialog.setTargetFragment(this, RequestCodes.selectInfoContact)
    dialog.show(ft, tagDialog)
  }

  override def contactAdded(card: AddCardRequest): Ui[Any] = {
    collectionsPresenter.addCards(Seq(card))
    unreveal()
  }

  override def isTabsOpened: Boolean = (tabs ~> isOpened).get getOrElse false

  private[this] def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible)

  private[this] def generateContactsAdapter(contacts: IterableContacts, counters: Seq[TermCounter], clickListener: (Contact) => Unit)
    (implicit uiContext: UiContext[_]): Ui[_] = {
    val adapter = new ContactsAdapter(contacts, clickListener, None)
    showData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (recycler map { rv =>
        scrollerLayout <~ fslLinkRecycler(rv) <~ fslCounters(counters)
      } getOrElse showGeneralError)
  }

  private[this] def reloadContactsAdapter(contacts: IterableContacts, counters: Seq[TermCounter], filter: ContactsFilter)
    (implicit uiContext: UiContext[_]): Ui[_] = {
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

  private[this] def getAdapter: Option[ContactsAdapter] = recycler flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: ContactsAdapter) => Some(a)
      case _ => None
    }
  }

  protected def openTabs(): Ui[_] = (tabs <~ tvOpen <~ showTabs) ~ (recycler <~ hideList)

}
