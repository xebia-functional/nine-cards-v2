package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts

import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.{HeaderedItemAdapter, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.ContactHeadered
import com.fortysevendeg.ninecardslauncher.process.device.models.Contact
import macroid.ActivityContextWrapper

case class ContactsAdapter(
  initialSeq: Seq[ContactHeadered],
  clickListener: (Contact) => Unit,
  longClickListener: Option[(Contact) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends HeaderedItemAdapter[Contact] {

  override def createViewHolder(view: ViewGroup): ItemHeaderedViewHolder[Contact] = new ViewHolderContactLayoutAdapter(view)

}
