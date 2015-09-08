package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageCardsTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

import scala.annotation.tailrec
import scala.math.Ordering.Implicits._

trait ContactsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.contacts) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (loading <~ vVisible) ~
      (recycler <~ recyclerStyle)

  def addContact(contacts: Seq[Contact], clickListener: (Contact) => Unit)(implicit fragment: Fragment) = {
    val sortedContacts = contacts sortBy sortByName
    val contactsHeadered = generateContactsForList(sortedContacts.toList, Seq.empty)
    val adapter = new ContactsAdapter(contactsHeadered, clickListener)
    (recycler <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

  private[this] def sortByName(contact: Contact) = contact.name map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

  @tailrec
  private[this] def generateContactsForList(contacts: List[Contact], acc: Seq[ContactHeadered]): Seq[ContactHeadered] = contacts match {
    case Nil => acc
    case h :: t =>
      val currentChar = h.name.substring(0, 1).toUpperCase
      val lastChar = acc.lastOption flatMap (_.contact map (_.name.substring(0, 1).toUpperCase))
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

  def bind(contact: Contact, position: Int)(implicit fragment: Fragment): Ui[_] =
    (icon <~ ivUriContact(fragment, contact.photoUri, contact.name, circular = true)) ~
      (name <~ tvText(contact.name)) ~
      (content <~ vIntTag(position))

  override def findViewById(id: Int): View = content.findViewById(id)

}