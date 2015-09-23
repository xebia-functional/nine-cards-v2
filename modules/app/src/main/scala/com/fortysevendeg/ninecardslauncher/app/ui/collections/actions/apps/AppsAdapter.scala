package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.{HeaderedItemAdapter, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import macroid.ActivityContextWrapper

case class AppsAdapter(
  initialSeq: Seq[AppHeadered],
  clickListener: (AppCategorized) => Unit)
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends HeaderedItemAdapter[AppCategorized] {

  override def createViewHolder(view: ViewGroup): ItemHeaderedViewHolder[AppCategorized] = new ViewHolderAppLayoutAdapter(view)

}