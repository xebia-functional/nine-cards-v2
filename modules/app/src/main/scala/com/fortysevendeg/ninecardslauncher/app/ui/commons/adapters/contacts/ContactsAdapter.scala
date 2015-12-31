package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.{OnClickListener, OnLongClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ScrollingLinearLayoutManager
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak, Ui}

case class ContactsAdapter(
  var contacts: IterableContacts,
  clickListener: (Contact) => Unit,
  longClickListener: Option[(Contact) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends RecyclerView.Adapter[ContactsIterableHolder]
  with FastScrollerListener {

  val heightItem = resGetDimensionPixelSize(R.dimen.height_contact_item)

  override def getItemCount: Int = contacts.count()

  override def onBindViewHolder(vh: ContactsIterableHolder, position: Int): Unit = {
    runUi(vh.bind(contacts.moveToPosition(position), position))
  }

  override def onCreateViewHolder(parent: ViewGroup, i: Int): ContactsIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.contact_item, parent, false)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => clickListener(contacts.moveToPosition(Int.unbox(tag))))
      }
    })
    longClickListener foreach { listener =>
      view.setOnLongClickListener(new OnLongClickListener {
        override def onLongClick(v: View): Boolean = {
          Option(v.getTag) foreach (tag => listener(contacts.moveToPosition(Int.unbox(tag))))
          true
        }
      })
    }
    ContactsIterableHolder(view)
  }

  def getLayoutManager: LinearLayoutManager =
    new LinearLayoutManager(activityContext.application) with ScrollingLinearLayoutManager

  def swapIterator(iter: IterableContacts) = {
    contacts = iter
    notifyDataSetChanged()
  }

  override def getHeightAllRows: Int = contacts.count() * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = 1

  override def getElement(position: Int): Option[String] = Option(contacts.moveToPosition(position).name.substring(0, 1))
}

case class ContactsIterableHolder(content: View)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.contact_item_icon))

  lazy val name = Option(findView(TR.contact_item_name))

  runUi(icon <~ (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank))

  def bind(contact: Contact, position: Int)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val contactName = Option(contact.name) getOrElse resGetString(R.string.unnamed)
    (icon <~ ivUriContact(contact.photoUri, contactName, circular = true)) ~
      (name <~ tvText(contactName)) ~
      (content <~ vTag2(position))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
