package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.TR
import macroid.ActivityContextWrapper
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import macroid.FullDsl._

case class PublicCollectionsAdapter(sharedCollections: Seq[SharedCollection], clickListener: (SharedCollection) => Unit)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.Adapter[ViewHolderPublicCollectionsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPublicCollectionsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.public_collections_item, parent, false)
    new ViewHolderPublicCollectionsLayoutAdapter(view, clickListener)
  }

  override def getItemCount: Int = sharedCollections.size

  override def onBindViewHolder(viewHolder: ViewHolderPublicCollectionsLayoutAdapter, position: Int): Unit = {
    val publicCollection = sharedCollections(position)
    runUi(viewHolder.bind(publicCollection, position))
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
