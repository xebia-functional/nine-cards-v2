package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.sharedcollections

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import macroid._

case class ViewHolderSharedCollectionsLayoutAdapter(content: ViewGroup)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with SharedCollectionItem {

  initialize().run

}