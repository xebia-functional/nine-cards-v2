package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.support.v7.widget.Toolbar.LayoutParams
import android.support.v7.widget.{RecyclerView, SwitchCompat}
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View, ViewGroup}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.{ItemHeadered, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.ContactHeadered._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{UiContext, HeaderUtils}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher.process.device.{ContactsFilter, ContactsWithPhoneNumber}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, ActivityContextWrapper, Ui}

trait ContactsComposer
  extends Styles
  with HeaderUtils {

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

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone) ~ (scrollerLayout <~ fslInvisible)

  def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible) ~ (scrollerLayout <~ fslVisible)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateContactsAdapter(contacts: Seq[Contact], clickListener: (Contact) => Unit)(implicit uiContext: UiContext[_]): Ui[_] = {
    val contactsHeadered = generateContactsForList(contacts)
    val adapter = new ContactsAdapter(contactsHeadered, clickListener, None)
    showData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

  def reloadContactsAdapter(contacts: Seq[Contact], filter: ContactsFilter)(implicit uiContext: UiContext[_]): Ui[_] = {
    val contactsHeadered = generateContactsForList(contacts)
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.loadItems(contactsHeadered)) ~
          (rootContent <~ uiSnackbarShort(filter match {
            case ContactsWithPhoneNumber => R.string.contactsWithPhoneNumber
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

case class ViewHolderContactLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with ItemHeaderedViewHolder[Contact]
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  runUi(icon <~ (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank))

  override def bind(item: ItemHeadered[Contact], position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    item.item match {
      case Some(contact) =>
        val contactName = Option(contact.name) getOrElse resGetString(R.string.unnamed)
        (icon <~ ivUriContact(contact.photoUri, contactName, circular = true)) ~
          (name <~ tvText(contactName)) ~
          (content <~ vTag2(position))
      case _ => Ui.nop
    }

  override def findViewById(id: Int): View = content.findViewById(id)
}