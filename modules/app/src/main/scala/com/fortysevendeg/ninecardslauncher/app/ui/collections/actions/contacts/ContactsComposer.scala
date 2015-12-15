package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar.LayoutParams
import android.view.Gravity
import android.view.ViewGroup.LayoutParams._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.ContactsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher.process.device.{ContactsFilter, FavoriteContacts}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.Ui

trait ContactsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  var switch = slot[SwitchCompat]

  def initUi(onCheckedChange: (Boolean) => Unit): Ui[_] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    val switchParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL)
    switchParams.setMarginStart(padding)
    switchParams.setMarginEnd(padding)
    (toolbar <~
      tbTitle(R.string.contacts) <~
      toolbarStyle(colorPrimary) <~
      vgAddView(getUi(
        w[SwitchCompat] <~
          wire(switch) <~
          scColor(colorPrimary) <~
          scChecked(checked = true) <~
          scCheckedChangeListener(onCheckedChange)
      ), switchParams) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(colorPrimary))
  }

  def showLoading: Ui[_] =
    (loading <~ vVisible) ~
      (recycler <~ vGone) ~
      (scrollerLayout <~ fslInvisible) ~
      hideError

  def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible) ~ (scrollerLayout <~ fslVisible)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateContactsAdapter(contacts: IterableContacts, clickListener: (Contact) => Unit)(implicit uiContext: UiContext[_]): Ui[_] = {
    val adapter = new ContactsAdapter(contacts, clickListener, None)
    showData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

  def reloadContactsAdapter(contacts: IterableContacts, filter: ContactsFilter)(implicit uiContext: UiContext[_]): Ui[_] = {
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.swapIterator(contacts)) ~
          (rootContent <~ uiSnackbarShort(filter match {
            case FavoriteContacts => R.string.favoriteContacts
            case _ => R.string.allContacts
          })) ~
          (scrollerLayout <~ fslReset) ~
          (recycler <~ rvScrollToTop)
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[ContactsAdapter] = recycler flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: ContactsAdapter) => Some(a)
      case _ => None
    }
  }

}
