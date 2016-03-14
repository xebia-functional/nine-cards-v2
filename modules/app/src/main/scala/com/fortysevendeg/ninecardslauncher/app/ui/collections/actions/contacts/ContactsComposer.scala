package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.ContactsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{TabInfo, PullToTabsListener}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.models.{TermCounter, Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.process.device.{AllContacts, ContactsFilter, FavoriteContacts}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait ContactsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  val resistance = 2.4f

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = Option(findView(TR.action_scroller_layout))

  lazy val pullToTabsView = Option(findView(TR.actions_pull_to_tabs))

  lazy val tabs = Option(findView(TR.actions_tabs))

  lazy val appTabs = Seq(
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.contacts_alphabetical)),
    TabInfo(R.drawable.app_drawer_filter_favorites, getString(R.string.contacts_favorites))
  )

  def initUi(onChange: (ContactsFilter) => Unit): Ui[_] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtvInflateMenu(R.menu.contact_dialog_menu) <~
      dtvOnMenuItemClickListener(onItem = {
        case R.id.action_filter =>
          (if (isTabsOpened) closeTabs else openTabs).run
          true
        case _ => false
      }) <~
      dtbChangeText(R.string.contacts) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (pullToTabsView <~
        ptvLinkTabs(
          tabs = tabs,
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(appTabs, 0, Some(colorPrimary)) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            (Ui(onChange(if (pos == 0) AllContacts else FavoriteContacts)) ~
              (if (isTabsOpened) closeTabs else Ui.nop)).run
          }
        ))) ~
      (recycler <~ recyclerStyle) ~
      (tabs <~ tvClose) ~
      (scrollerLayout <~ fslColor(colorPrimary))

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone) ~ hideError

  def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateContactsAdapter(contacts: IterableContacts, counters: Seq[TermCounter], clickListener: (Contact) => Unit)
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

  def reloadContactsAdapter(contacts: IterableContacts, counters: Seq[TermCounter], filter: ContactsFilter)
    (implicit uiContext: UiContext[_]): Ui[_] = {
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.swapIterator(contacts)) ~
          (rootContent <~ uiSnackbarShort(filter match {
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

  private[this] def isTabsOpened: Boolean = (tabs ~> isOpened).get getOrElse false

  protected def openTabs: Ui[_] =
    (tabs <~ tvOpen <~ showTabs) ~
      (recycler <~ hideList)

  protected def closeTabs: Ui[_] =
    (tabs <~ tvClose <~ hideTabs) ~
      (recycler <~ showList)

}
