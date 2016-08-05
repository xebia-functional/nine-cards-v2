package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts

import java.io.Closeable

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ScrollingLinearLayoutManager
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class ContactsAdapter(
  var contacts: IterableContacts,
  clickListener: (Contact) => Unit,
  longClickListener: Option[(View, Contact) => Unit])
  (implicit val activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ContactsIterableHolder]
  with FastScrollerListener
  with Closeable {

  val columnsLists = 1

  val heightItem = resGetDimensionPixelSize(R.dimen.height_contact_item)

  override def getItemCount: Int = contacts.count()

  override def onBindViewHolder(vh: ContactsIterableHolder, position: Int): Unit =
    vh.bind(contacts.moveToPosition(position), position).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): ContactsIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.contact_item, parent, false)
    ContactsIterableHolder(view, clickListener, longClickListener)
  }

  def getLayoutManager: LinearLayoutManager = new ScrollingLinearLayoutManager(columnsLists)

  def swapIterator(iter: IterableContacts) = {
    contacts.close()
    contacts = iter
    notifyDataSetChanged()
  }

  override def close() = contacts.close()

  override def getHeightAllRows: Int = contacts.count() / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists

}

case class ContactsIterableHolder(
  content: View,
  clickListener: (Contact) => Unit,
  longClickListener: Option[(View, Contact) => Unit])(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.contact_item_icon))

  lazy val name = Option(findView(TR.contact_item_name))

  lazy val favorite = Option(findView(TR.contact_item_favorite))

  (icon <~ (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank)).run

  def bind(contact: Contact, position: Int): Ui[_] = {
    val contactName = Option(contact.name) getOrElse resGetString(R.string.unnamed)
    (icon <~ ivUriContact(contact.photoUri, contactName, circular = true)) ~
      (name <~ tvText(contactName) + tvColor(theme.get(DrawerTextColor))) ~
      (favorite <~ (if (contact.favorite) vVisible else vGone)) ~
      (content <~
        On.click {
          Ui(clickListener(contact))
        } <~
        (longClickListener map { listener =>
          FuncOn.longClick { view: View =>
            icon foreach (listener(_, contact))
            Ui(true)
          }
        } getOrElse Tweak.blank))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
