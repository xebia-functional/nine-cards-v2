package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsAdapter._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.ViewHolderCategoryLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper
import macroid.FullDsl._

case class ContactsAdapter(var contacts: Seq[ContactHeadered], clickListener: (Contact) => Unit)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.Adapter[RecyclerView.ViewHolder]
  with FastScrollerListener {

  val heightHeader = resGetDimensionPixelSize(R.dimen.height_simple_category)

  val heightApp = resGetDimensionPixelSize(R.dimen.height_simple_app)

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = viewType match {
    case `itemViewTypeHeader` =>
      val view = LayoutInflater.from(parent.getContext).inflate(R.layout.simple_category, parent, false).asInstanceOf[ViewGroup]
      new ViewHolderCategoryLayoutAdapter(view)
    case `itemViewTypeApp` =>
      val view = LayoutInflater.from(parent.getContext).inflate(R.layout.simple_item, parent, false).asInstanceOf[ViewGroup]
      view.setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          Option(v.getTag) foreach (tag => contacts(Int.unbox(tag)).contact foreach clickListener)
        }
      })
      new ViewHolderContactLayoutAdapter(view)
  }

  override def getItemCount: Int = contacts.size

  override def getItemViewType(position: Int): Int = if (contacts(position).header.isDefined) itemViewTypeHeader else itemViewTypeApp

  override def onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int): Unit = {
    val contact = contacts(position)
    viewHolder match {
      case vh: ViewHolderCategoryLayoutAdapter =>
        contact.header map (category => runUi(vh.bind(category)))
      case vh: ViewHolderContactLayoutAdapter =>
        contact.contact map (contact => runUi(vh.bind(contact, position)))
    }

  }

  def getLayoutManager = {
    val manager = new GridLayoutManager(activityContext.application, numInLine)
    manager.setSpanSizeLookup(new SpanSizeLookup {
      override def getSpanSize(position: Int): Int = if (contacts(position).header.isDefined) manager.getSpanCount else 1
    })
    manager
  }

  def loadContacts(newContacts: Seq[ContactHeadered]) = {
    contacts = newContacts
    notifyDataSetChanged()
  }

  override def getHeight = {
    val heightHeaders = (contacts count (_.header.isDefined)) * heightHeader
    // Calculate the number of column showing contacts
    val rowsWithContacts = contacts.foldLeft((0, 0))((counter, contact) =>
      (contact.header, counter._1) match {
        case (Some(_), _) => (0, counter._2)
        case (_, 0) => (1, counter._2 + 1)
        case (_, columns) if columns < numInLine => (counter._1 + 1, counter._2)
        case _ => (0, counter._2)
      })
    val heightApps = rowsWithContacts._2 * heightApp
    heightHeaders + heightApps
  }

  val defaultElement: Option[String] = None

  override def getElement(position: Int): Option[String] = contacts.foldLeft((defaultElement, false))((info, contact) =>
    if (contact == contacts(position)) {
      (info._1, true)
    } else {
      (info._1, info._2) match {
        case (_, false) => contact.header map (header => (Option(header), info._2)) getOrElse info
        case _ => info
      }
    }
  )._1

}

object ContactsAdapter {
  val itemViewTypeHeader = 0
  val itemViewTypeApp = 1
}
