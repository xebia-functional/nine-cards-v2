package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.{OnClickListener, OnLongClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.CounterStatuses
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ScrollingLinearLayoutManager
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableContacts}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
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

  var statuses = CounterStatuses(count = contacts.count())

  override def getItemCount: Int = contacts.count()

  override def onBindViewHolder(vh: ContactsIterableHolder, position: Int): Unit = {
    runUi(vh.bind(contacts.moveToPosition(position), position, statuses.isActive(position)))
  }

  override def onCreateViewHolder(parent: ViewGroup, i: Int): ContactsIterableHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.contact_item, parent, false)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        v.getPosition foreach (tag => clickListener(contacts.moveToPosition(tag)))
      }
    })
    longClickListener foreach { listener =>
      view.setOnLongClickListener(new OnLongClickListener {
        override def onLongClick(v: View): Boolean = {
          v.getPosition foreach (tag => listener(contacts.moveToPosition(tag)))
          true
        }
      })
    }
    ContactsIterableHolder(view)
  }

  def getLayoutManager: LinearLayoutManager =
    new LinearLayoutManager(activityContext.application) with ScrollingLinearLayoutManager

  def swapIterator(iter: IterableContacts) = {
    contacts.close()
    contacts = iter
    notifyDataSetChanged()
    statuses = statuses.reset(count = getItemCount)
  }

  def close() = contacts.close()

  override def getHeightAllRows: Int = contacts.count() * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = 1

  override def activeItems(f: Int, c: Int): Unit = statuses = statuses.copy(from = f, count = c)

  override def inactiveItems(): Unit = statuses = statuses.reset(count = getItemCount)

}

case class ContactsIterableHolder(content: View)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  val default = 1f

  val unselected = resGetInteger(R.integer.appdrawer_alpha_unselected_item_percentage).toFloat / 100

  lazy val icon = Option(findView(TR.contact_item_icon))

  lazy val name = Option(findView(TR.contact_item_name))

  lazy val favorite = Option(findView(TR.contact_item_favorite))

  runUi(icon <~ (Lollipop ifSupportedThen vCircleOutlineProvider() getOrElse Tweak.blank))

  def bind(contact: Contact, position: Int, active: Boolean): Ui[_] = {
    val contactName = Option(contact.name) getOrElse resGetString(R.string.unnamed)
    (content <~ (if (active) vAlpha(default) else vAlpha(unselected))) ~
      (icon <~ ivUriContact(contact.photoUri, contactName, circular = true)) ~
      (name <~ tvText(contactName)) ~
      (content <~ vSetPosition(position)) ~
      (favorite <~ (if (contact.favorite) vVisible else vGone))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}
