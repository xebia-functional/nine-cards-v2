package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.mycollections

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.collection.PrivateCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

case class PrivateCollectionsAdapter(privateCollections: Seq[PrivateCollection], clickListener: (PrivateCollection) => Ui[_])
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.Adapter[ViewHolderPrivateCollectionsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPrivateCollectionsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.private_collections_item, parent, false).asInstanceOf[ViewGroup]
    new ViewHolderPrivateCollectionsLayoutAdapter(view, clickListener)
  }

  override def getItemCount: Int = privateCollections.size

  override def onBindViewHolder(viewHolder: ViewHolderPrivateCollectionsLayoutAdapter, position: Int): Unit = {
    val privateCollection = privateCollections(position)
    runUi(viewHolder.bind(privateCollection, position))
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
