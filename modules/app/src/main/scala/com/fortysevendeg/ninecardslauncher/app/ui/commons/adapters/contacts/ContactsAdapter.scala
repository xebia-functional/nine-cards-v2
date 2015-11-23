package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts

import android.support.v7.widget.LinearLayoutManager
import android.view.View.{OnClickListener, OnLongClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ViewHolderContactLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.{ScrollableManager, HeaderedItemAdapter, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.ContactHeadered
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

case class ContactsAdapter(
  initialSeq: Seq[ContactHeadered],
  clickListener: (Contact) => Unit,
  longClickListener: Option[(Contact) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends HeaderedItemAdapter[Contact]
    with FastScrollerListener {

  val heightItem = resGetDimensionPixelSize(R.dimen.height_contact_item)

  override def createViewHolder(parent: ViewGroup): ItemHeaderedViewHolder[Contact] = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.contact_item, parent, false).asInstanceOf[ViewGroup]
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => seq(Int.unbox(tag)).item foreach clickListener)
      }
    })
    longClickListener foreach { listener =>
      view.setOnLongClickListener(new OnLongClickListener {
        override def onLongClick(v: View): Boolean = {
          Option(v.getTag) foreach (tag => seq(Int.unbox(tag)).item foreach listener)
          true
        }
      })
    }
    new ViewHolderContactLayoutAdapter(view)
  }

  override def getLayoutManager: LinearLayoutManager =
    new LinearLayoutManager(activityContext.application) with ScrollableManager {
      override def canScrollVertically: Boolean = if (blockScroll) false else super.canScrollVertically
    }

  override def getHeight: Int = {
    val heightHeaders = (seq count (_.header.isDefined)) * heightHeader
    val heightItems = (seq count (_.item.isDefined)) * heightItem
    heightHeaders + heightItems
  }

  val defaultElement: Option[String] = None

  override def getElement(position: Int): Option[String] = seq.foldLeft((defaultElement, false))((info, itemHeadered) =>
    if (itemHeadered == seq(position)) {
      (info._1, true)
    } else {
      (info._1, info._2) match {
        case (_, false) => itemHeadered.header map (header => (Option(header), info._2)) getOrElse info
        case _ => info
      }
    }
  )._1
}
