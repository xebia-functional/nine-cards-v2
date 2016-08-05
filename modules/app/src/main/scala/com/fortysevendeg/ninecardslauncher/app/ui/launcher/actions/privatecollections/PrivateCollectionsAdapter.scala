package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.commons.models.PrivateCollection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

case class PrivateCollectionsAdapter(privateCollections: Seq[PrivateCollection])
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], presenter: PrivateCollectionsPresenter, theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderPrivateCollectionsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPrivateCollectionsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.private_collections_item, parent, false).asInstanceOf[ViewGroup]
    ViewHolderPrivateCollectionsLayoutAdapter(view)
  }

  override def getItemCount: Int = privateCollections.size

  override def onBindViewHolder(viewHolder: ViewHolderPrivateCollectionsLayoutAdapter, position: Int): Unit = {
    val privateCollection = privateCollections(position)
    viewHolder.bind(privateCollection, position).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
