package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts

import android.support.v7.widget.LinearLayoutManager
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ViewHolderContactLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.{ScrollableManager, HeaderedItemAdapter, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.ContactHeadered
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import com.fortysevendeg.ninecardslauncher2.{R, TR}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import macroid.ActivityContextWrapper

case class ContactsAdapter(
  initialSeq: Seq[ContactHeadered],
  clickListener: (Contact) => Unit,
  longClickListener: Option[(Contact) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends HeaderedItemAdapter[Contact] {

  val heightItem = resGetDimensionPixelSize(R.dimen.height_contact_item)

  override def inflateView(parent: ViewGroup): ViewGroup =
    LayoutInflater.from(parent.getContext).inflate(TR.layout.contact_item, parent, false)

  override def createViewHolder(view: ViewGroup): ItemHeaderedViewHolder[Contact] =
    new ViewHolderContactLayoutAdapter(view)

  override def getLayoutManager: LinearLayoutManager =
    new LinearLayoutManager(activityContext.application) with ScrollableManager {
      override def canScrollVertically: Boolean = if (blockScroll) false else super.canScrollVertically
    }

  override def getHeight: Int = {
    val heightHeaders = (seq count (_.header.isDefined)) * heightHeader
    val heightItems = (seq count (_.item.isDefined)) * heightItem
    heightHeaders + heightItems
  }
}
