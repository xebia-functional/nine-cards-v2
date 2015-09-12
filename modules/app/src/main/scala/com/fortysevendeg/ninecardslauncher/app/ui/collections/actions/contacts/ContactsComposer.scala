package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar.LayoutParams
import android.support.v7.widget.{RecyclerView, SwitchCompat}
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageCardsTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ColorsUtils, HeaderUtils}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.{ContactsWithPhoneNumber, AllContacts, ContactsFilter}
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

import scala.annotation.tailrec

trait ContactsComposer
  extends Styles
  with HeaderUtils {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  var switch = slot[SwitchCompat]

  def initUi(onCheckedChange: (Boolean) => Unit): Ui[_] = {
    val switchParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL)
    switchParams.setMarginStart(resGetDimensionPixelSize(R.dimen.padding_default))
    switchParams.setMarginEnd(resGetDimensionPixelSize(R.dimen.padding_default))
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

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateContactsAdapter(contacts: Seq[Contact], clickListener: (Contact) => Unit)(implicit fragment: Fragment): Ui[_] = {
    val contactsHeadered = generateContactsForList(contacts.toList, Seq.empty)
    val adapter = new ContactsAdapter(contactsHeadered, clickListener)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

  def reloadContactsAdapter(contacts: Seq[Contact], filter: ContactsFilter)(implicit fragment: Fragment): Ui[_] = {
    val contactsHeadered = generateContactsForList(contacts.toList, Seq.empty)
    (recycler <~ vVisible) ~
      (loading <~ vGone) ~
      (getAdapter map { adapter =>
        Ui(adapter.loadContacts(contactsHeadered)) ~
          (rootContent <~ uiSnackbarShort(filter match {
            case ContactsWithPhoneNumber => R.string.contactsWithPhoneNumber
            case _ => R.string.allContacts
          }))
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[ContactsAdapter] = recycler flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: ContactsAdapter) => Some(a)
      case _ => None
    }
  }

  @tailrec
  private[this] def generateContactsForList(contacts: List[Contact], acc: Seq[ContactHeadered]): Seq[ContactHeadered] = contacts match {
    case Nil => acc
    case h :: t =>
      val currentChar = Option(h.name) map (name => generateChar(name.substring(0, 1))) getOrElse charUnnamed
      val lastChar = acc.lastOption flatMap (_.contact map (c => Option(c.name) map (name => generateChar(name.substring(0, 1))) getOrElse charUnnamed))
      val skipChar = lastChar exists (_ equals currentChar)
      if (skipChar) {
        generateContactsForList(t, acc :+ ContactHeadered(contact = Option(h)))
      } else {
        generateContactsForList(t, acc ++ Seq(ContactHeadered(header = Option(currentChar)), ContactHeadered(contact = Option(h))))
      }
  }

}

case class ViewHolderContactLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, fragment: Fragment)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(contact: Contact, position: Int)(implicit fragment: Fragment): Ui[_] = {
    val contactName = Option(contact.name) map (name => name) getOrElse resGetString(R.string.unnamed)
    (icon <~ ivUriContact(fragment, contact.photoUri, contactName, circular = true)) ~
      (name <~ tvText(contactName)) ~
      (content <~ vIntTag(position))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}