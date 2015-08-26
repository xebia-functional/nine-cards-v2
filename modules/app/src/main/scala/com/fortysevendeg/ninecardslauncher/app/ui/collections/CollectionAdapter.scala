package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.LauncherExecutor
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import macroid.ActivityContextWrapper
import macroid.FullDsl._

class CollectionAdapter(collection: Collection, heightCard: Int)
  (implicit activityContext: ActivityContextWrapper, fragment: Fragment, theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderCollectionAdapter]
  with LauncherExecutor {

  override def onCreateViewHolder(parentViewGroup: ViewGroup, viewType: Int): ViewHolderCollectionAdapter = {
    val adapter = new CollectionLayoutAdapter(heightCard)
    adapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => execute(collection.cards(tag.toString.toInt).intent))
      }
    })
    new ViewHolderCollectionAdapter(adapter)
  }

  override def getItemCount: Int = collection.cards.size

  override def onBindViewHolder(viewHolder: ViewHolderCollectionAdapter, position: Int): Unit = {
    val card = collection.cards(position)
    runUi(viewHolder.bind(card, position))
  }

}


